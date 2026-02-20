package com.smartcampus.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Book implements Serializable, Comparable<Book> {

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String language;
    private String section;
    private double rating;
    private int totalCopies;
    private int availableCopies;
    private int timesIssued;
    private double carbonScore;
    private LocalDate addedDate;
    private boolean isReserved;
    private String kannadaTitle;
    private String hindiTitle;

    public Book() {}

    public Book(String isbn, String title, String author, String genre,
                String language, String section, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.language = language;
        this.section = section;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.timesIssued = 0;
        this.rating = 0.0;
        this.carbonScore = 0.0;
        this.addedDate = LocalDate.now();
        this.isReserved = false;
        this.kannadaTitle = "";
        this.hindiTitle = "";
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public int getTimesIssued() { return timesIssued; }
    public void setTimesIssued(int timesIssued) { this.timesIssued = timesIssued; }

    public double getCarbonScore() { return carbonScore; }
    public void setCarbonScore(double carbonScore) { this.carbonScore = carbonScore; }

    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }

    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { isReserved = reserved; }

    public String getKannadaTitle() { return kannadaTitle; }
    public void setKannadaTitle(String kannadaTitle) { this.kannadaTitle = kannadaTitle; }

    public String getHindiTitle() { return hindiTitle; }
    public void setHindiTitle(String hindiTitle) { this.hindiTitle = hindiTitle; }

    public boolean isAvailable() { return availableCopies > 0; }

    
    @Override
    public int compareTo(Book other) {
        return Integer.compare(other.timesIssued, this.timesIssued);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() { return Objects.hash(isbn); }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" by %s | Section: %s | Available: %d/%d | Rating: %.1f",
                isbn, title, author, section, availableCopies, totalCopies, rating);
    }
}
