package com.splitapp.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "expense_split")
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expense_id")
    private Expense expense; // Linked expense

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    private Person person; // Participant involved

    @Column(nullable = false)
    private BigDecimal amountOwed; // Final amount this person owes

    @Column(nullable = false)
    private String shareType; // EQUAL, PERCENTAGE, or EXACT

    private BigDecimal shareValue; // Used if shareType is not EQUAL

    // Default constructor for JPA
    public ExpenseSplit() {}

    public ExpenseSplit(Expense expense, Person person, BigDecimal amountOwed, String shareType, BigDecimal shareValue) {
        this.expense = expense;
        this.person = person;
        this.amountOwed = amountOwed;
        this.shareType = shareType;
        this.shareValue = shareValue;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Expense getExpense() {
        return expense;
    }

    public Person getPerson() {
        return person;
    }

    public BigDecimal getAmountOwed() {
        return amountOwed;
    }

    public String getShareType() {
        return shareType;
    }

    public BigDecimal getShareValue() {
        return shareValue;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setAmountOwed(BigDecimal amountOwed) {
        this.amountOwed = amountOwed;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public void setShareValue(BigDecimal shareValue) {
        this.shareValue = shareValue;
    }
}
