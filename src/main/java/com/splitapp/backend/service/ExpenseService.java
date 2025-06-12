package com.splitapp.backend.service;

import com.splitapp.backend.model.*;
import com.splitapp.backend.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.Map.Entry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service layer for managing expense logic and business rules.
 */
@Service
public class ExpenseService {

    private final PersonRepository personRepo;
    private final ExpenseRepository expenseRepo;
    private final ExpenseSplitRepository splitRepo;

    public ExpenseService(PersonRepository personRepo, ExpenseRepository expenseRepo, ExpenseSplitRepository splitRepo) {
        this.personRepo = personRepo;
        this.expenseRepo = expenseRepo;
        this.splitRepo = splitRepo;
    }

    /**
     * Add a new expense and calculate how it should be split among participants.
     */
    public Expense addExpense(String description, BigDecimal amount, String paidByName, List<String> participants, String splitType, List<BigDecimal> shareValues) {
        // Validate input
        if (description == null || amount == null || paidByName == null || participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields");
        }
        if (!List.of("EQUAL", "EXACT", "PERCENTAGE").contains(splitType)) {
            throw new IllegalArgumentException("Invalid splitType");
        }
        if (!"EQUAL".equals(splitType) && (shareValues == null || shareValues.size() != participants.size())) {
            throw new IllegalArgumentException("shareValues size mismatch with participants");
        }

        // Ensure payer exists
        Person paidBy = personRepo.findByName(paidByName).orElseGet(() -> personRepo.save(new Person(paidByName)));

        // Create expense entry
        Expense expense = expenseRepo.save(new Expense(description, amount, paidBy));

        // Create splits for each participant
        for (int i = 0; i < participants.size(); i++) {
            int finalI = i;
            Person p = personRepo.findByName(participants.get(i)).orElseGet(() -> personRepo.save(new Person(participants.get(finalI))));
            BigDecimal shareVal = null;
            BigDecimal owedAmount;

            switch (splitType) {
                case "EXACT":
                    shareVal = shareValues.get(i);
                    owedAmount = shareVal;
                    break;
                case "PERCENTAGE":
                    shareVal = shareValues.get(i);
                    owedAmount = amount.multiply(shareVal).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    break;
                default:
                    owedAmount = amount.divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);
            }

            ExpenseSplit split = new ExpenseSplit(expense, p, owedAmount, splitType, shareVal);
            splitRepo.save(split);
        }

        return expense;
    }

    /**
     * Fetch all recorded expenses.
     */
    public List<Expense> getAllExpenses() {
        return expenseRepo.findAll();
    }

    /**
     * Get all people involved in expenses.
     */
    public List<Person> getAllPeople() {
        return personRepo.findAll();
    }

    /**
     * Calculate current balance for each person (owed vs paid).
     */
    public List<Map<String, Object>> getBalances() {
        Map<String, BigDecimal> balances = new HashMap<>();

        // Subtract what each person owes
        for (ExpenseSplit split : splitRepo.findAll()) {
            balances.put(split.getPerson().getName(),
                    balances.getOrDefault(split.getPerson().getName(), BigDecimal.ZERO)
                            .subtract(split.getAmountOwed()));
        }

        // Add what each person paid
        for (Expense e : expenseRepo.findAll()) {
            balances.put(e.getPaidBy().getName(),
                    balances.getOrDefault(e.getPaidBy().getName(), BigDecimal.ZERO)
                            .add(e.getAmount()));
        }

        // Format result
        List<Map<String, Object>> result = new ArrayList<>();
        balances.forEach((name, balance) -> result.add(Map.of("name", name, "netBalance", balance)));
        return result;
    }

    /**
     * Calculate simplified settlement: who pays whom and how much.
     */
    public List<Map<String, Object>> getSettlements() {
        Map<String, BigDecimal> balances = new HashMap<>();
        for (Map<String, Object> bal : getBalances()) {
            balances.put((String) bal.get("name"), (BigDecimal) bal.get("netBalance"));
        }

        // Separate debtors and creditors

        PriorityQueue<Map.Entry<String, BigDecimal>> debtors = new PriorityQueue<>(Map.Entry.comparingByValue());

        PriorityQueue<Map.Entry<String, BigDecimal>> creditors =
                new PriorityQueue<>((a, b) -> b.getValue().compareTo(a.getValue())); // positive balances

        for (var entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) debtors.add(entry);
            else if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) creditors.add(entry);
        }

        // Match debtors to creditors
        List<Map<String, Object>> settlements = new ArrayList<>();
        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            var debtor = debtors.poll();
            var creditor = creditors.poll();
            BigDecimal amt = debtor.getValue().abs().min(creditor.getValue());

            settlements.add(Map.of("from", debtor.getKey(), "to", creditor.getKey(), "amount", amt));

            BigDecimal dBal = debtor.getValue().add(amt);
            BigDecimal cBal = creditor.getValue().subtract(amt);

            if (dBal.compareTo(BigDecimal.ZERO) < 0) debtors.add(Map.entry(debtor.getKey(), dBal));
            if (cBal.compareTo(BigDecimal.ZERO) > 0) creditors.add(Map.entry(creditor.getKey(), cBal));
        }

        return settlements;
    }

    /**
     * Update an existing expense and reassign the splits.
     */
    @Transactional
    public Expense updateExpense(Long id, String description, BigDecimal amount, String paidByName, List<String> participants, String splitType, List<BigDecimal> shareValues) {
        Expense existing = expenseRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));

        // Remove old splits
        splitRepo.deleteAllByExpenseId(id);

        // Update core expense data
        Person paidBy = personRepo.findByName(paidByName)
                .orElseGet(() -> personRepo.save(new Person(paidByName)));
        existing.setDescription(description);
        existing.setAmount(amount);
        existing.setPaidBy(paidBy);
        expenseRepo.save(existing);

        // Recreate splits
        for (int i = 0; i < participants.size(); i++) {
            int finalI = i;
            Person p = personRepo.findByName(participants.get(i)).orElseGet(() -> personRepo.save(new Person(participants.get(finalI))));
            BigDecimal shareVal = null;
            BigDecimal owedAmount;

            switch (splitType) {
                case "EXACT":
                    shareVal = shareValues.get(i);
                    owedAmount = shareVal;
                    break;
                case "PERCENTAGE":
                    shareVal = shareValues.get(i);
                    owedAmount = amount.multiply(shareVal).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    break;
                default:
                    owedAmount = amount.divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);
            }

            ExpenseSplit split = new ExpenseSplit(existing, p, owedAmount, splitType, shareVal);
            splitRepo.save(split);
        }

        return existing;
    }

    /**
     * Delete an expense and its associated splits.
     */
    @Transactional
    public void deleteExpense(Long id) {
        splitRepo.deleteAllByExpenseId(id);
        expenseRepo.deleteById(id);
    }
}
