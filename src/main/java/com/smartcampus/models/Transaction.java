package com.smartcampus.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Transaction implements Serializable, Comparable<Transaction> {

    public enum Status { ISSUED, RETURNED, OVERDUE, RESERVED, LOST }

    private String transactionId;
    private String userId;
    private String isbn;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Status status;
    private double fine;
    private int priority;

    private static final double FINE_PER_DAY = 2.0;

    public Transaction() {}

    public Transaction(String transactionId, String userId, String isbn, LocalDate issueDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.isbn = isbn;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(14);
        this.status = Status.ISSUED;
        this.fine = 0.0;
        this.priority = 0;
    }

    
    public double calculateFine() {
        if (status == Status.RETURNED && returnDate != null) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            fine = daysLate > 0 ? daysLate * FINE_PER_DAY : 0.0;
        } else if (status == Status.ISSUED) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            fine = daysLate > 0 ? daysLate * FINE_PER_DAY : 0.0;
            if (fine > 0) status = Status.OVERDUE;
        }
        return fine;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && status != Status.RETURNED;
    }

    
    @Override
    public int compareTo(Transaction other) {
        return Integer.compare(this.priority, other.priority);
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String id) { this.transactionId = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        return Objects.equals(transactionId, ((Transaction) o).transactionId);
    }

    @Override
    public int hashCode() { return Objects.hash(transactionId); }

    @Override
    public String toString() {
        return String.format("Txn[%s | User: %s | Book: %s | Due: %s | Status: %s | Fine: ₹%.2f]",
                transactionId, userId, isbn, dueDate, status, fine);
    }
}
