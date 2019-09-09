package com.salman.tourmateapp.model;

public class Expense {
    String expenseId;
    String expenseDesc;
    String expenseBal;
    String tripLocation;
    String userId;

    public Expense() {
    }

    public Expense(String expenseId, String expenseDesc, String expenseBal, String tripLocation, String userId) {
        this.expenseId = expenseId;
        this.expenseDesc = expenseDesc;
        this.expenseBal = expenseBal;
        this.tripLocation = tripLocation;
        this.userId = userId;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public String getExpenseDesc() {
        return expenseDesc;
    }

    public String getExpenseBal() {
        return expenseBal;
    }

    public String getTripLocation() {
        return tripLocation;
    }

    public String getUserId() {
        return userId;
    }
}
