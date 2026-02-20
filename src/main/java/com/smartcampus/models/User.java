package com.smartcampus.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User implements Serializable {

    public enum Role { STUDENT, FACULTY, ADMIN }
    public enum Department { CSE, ECE, MTECH_AI, PHD, MBA, DESIGN }

    private String userId;
    private String name;
    private String email;
    private Role role;
    private Department department;
    private int semester;
    private int readingStreak;
    private int totalBooksRead;
    private double carbonSaved;

    
    private Map<String, Double> bookRatings;

    
    private List<String> borrowHistory;

    private List<String> studySubjects;

    public User() {
        bookRatings = new HashMap<>();
        borrowHistory = new ArrayList<>();
        studySubjects = new ArrayList<>();
    }

    public User(String userId, String name, String email, Role role,
                Department department, int semester) {
        this();
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.semester = semester;
        this.readingStreak = 0;
        this.totalBooksRead = 0;
        this.carbonSaved = 0.0;
    }

    public void rateBook(String isbn, double rating) {
        bookRatings.put(isbn, rating);
    }

    public void addBorrowHistory(String isbn) {
        if (!borrowHistory.contains(isbn)) {
            borrowHistory.add(isbn);
        }
        totalBooksRead++;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public int getReadingStreak() { return readingStreak; }
    public void setReadingStreak(int readingStreak) { this.readingStreak = readingStreak; }

    public int getTotalBooksRead() { return totalBooksRead; }
    public void setTotalBooksRead(int totalBooksRead) { this.totalBooksRead = totalBooksRead; }

    public double getCarbonSaved() { return carbonSaved; }
    public void setCarbonSaved(double carbonSaved) { this.carbonSaved = carbonSaved; }

    public Map<String, Double> getBookRatings() { return bookRatings; }
    public void setBookRatings(Map<String, Double> bookRatings) { this.bookRatings = bookRatings; }

    public List<String> getBorrowHistory() { return borrowHistory; }
    public void setBorrowHistory(List<String> borrowHistory) { this.borrowHistory = borrowHistory; }

    public List<String> getStudySubjects() { return studySubjects; }
    public void setStudySubjects(List<String> studySubjects) { this.studySubjects = studySubjects; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return Objects.equals(userId, ((User) o).userId);
    }

    @Override
    public int hashCode() { return Objects.hash(userId); }

    @Override
    public String toString() {
        return String.format("User[%s | %s | %s | Sem %d | Streak: %d days | Books: %d]",
                userId, name, department, semester, readingStreak, totalBooksRead);
    }
}
