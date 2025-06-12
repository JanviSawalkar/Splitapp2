package com.splitapp.backend.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO to receive expense creation or update requests from the client.
 */
public class ExpenseRequest {

    private String description; // Description of the expense
    private BigDecimal amount;  // Total expense amount
    private String paidBy;      // Name of the person who paid
    private List<String> participants; // List of participant names
    private String splitType;   // EQUAL, EXACT, or PERCENTAGE
    private List<BigDecimal> shareValues; // Shares per person (used for EXACT or PERCENTAGE)

    // Getters and Setters
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaidBy() {
        return paidBy;
    }
    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public List<String> getParticipants() {
        return participants;
    }
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getSplitType() {
        return splitType;
    }
    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public List<BigDecimal> getShareValues() {
        return shareValues;
    }
    public void setShareValues(List<BigDecimal> shareValues) {
        this.shareValues = shareValues;
    }
}
