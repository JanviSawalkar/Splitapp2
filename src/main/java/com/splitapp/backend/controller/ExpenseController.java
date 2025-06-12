package com.splitapp.backend.controller;

import com.splitapp.backend.dto.ExpenseRequest;
import com.splitapp.backend.model.Expense;
import com.splitapp.backend.model.Person;
import com.splitapp.backend.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing expense-related endpoints.
 */
@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Add a new expense with split details.
     */
    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRequest request) {
        try {
            Expense expense = expenseService.addExpense(
                    request.getDescription(),
                    request.getAmount(),
                    request.getPaidBy(),
                    request.getParticipants(),
                    request.getSplitType(),
                    request.getShareValues()
            );
            return ResponseEntity.ok(Map.of("success", true, "data", expense, "message", "Expense added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    /**
     * Fetch a list of all recorded expenses.
     */
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    /**
     * Retrieve all unique people involved in any expenses.
     */
    @GetMapping("/people")
    public ResponseEntity<List<Person>> getAllPeople() {
        return ResponseEntity.ok(expenseService.getAllPeople());
    }

    /**
     * Calculate and return current balances per person (how much each owes/is owed).
     */
    @GetMapping("/balances")
    public ResponseEntity<?> getBalances() {
        return ResponseEntity.ok(expenseService.getBalances());
    }

    /**
     * Calculate simplified settlement summary (who pays whom and how much).
     */
    @GetMapping("/settlements")
    public ResponseEntity<?> getSettlements() {
        return ResponseEntity.ok(expenseService.getSettlements());
    }

    /**
     * Update an existing expense by its ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        try {
            Expense updated = expenseService.updateExpense(
                    id,
                    request.getDescription(),
                    request.getAmount(),
                    request.getPaidBy(),
                    request.getParticipants(),
                    request.getSplitType(),
                    request.getShareValues()
            );
            return ResponseEntity.ok(Map.of("success", true, "data", updated, "message", "Expense updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "Internal server error"));
        }
    }

    /**
     * Delete an expense and its associated splits by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Expense deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error deleting expense"));
        }
    }

}
