package com.splitapp.backend.repository;

import com.splitapp.backend.model.Expense;
import com.splitapp.backend.model.ExpenseSplit;
import com.splitapp.backend.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {

    /**
     * Returns all split records for a given expense.
     * @param expense the expense entity
     * @return list of ExpenseSplit entries
     */
    List<ExpenseSplit> findByExpense(Expense expense);

    /**
     * Returns all split records associated with a specific person.
     * @param person the person entity
     * @return list of ExpenseSplit entries
     */
    List<ExpenseSplit> findByPerson(Person person);

    /**
     * Returns split details for a specific person in a specific expense.
     * Useful for validations or detailed views.
     */
    List<ExpenseSplit> findByPersonAndExpense(Person person, Expense expense);

    /**
     * Deletes all splits related to a given expense ID.
     * Useful when deleting the entire expense and its shares.
     */
    void deleteAllByExpenseId(Long expenseId);
}
