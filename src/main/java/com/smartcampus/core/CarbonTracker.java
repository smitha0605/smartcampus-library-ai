package com.smartcampus.core;

import com.smartcampus.models.Book;
import com.smartcampus.models.User;

import java.util.*;

public class CarbonTracker {

    
    private final HashMap<String, Double> carbonCache = new HashMap<>();

    
    private static final Map<String, Integer> GENRE_PAGES = new HashMap<>();
    static {
        GENRE_PAGES.put("algorithms",        650);
        GENRE_PAGES.put("data structures",   500);
        GENRE_PAGES.put("operating systems", 700);
        GENRE_PAGES.put("machine learning",  600);
        GENRE_PAGES.put("fiction",           350);
        GENRE_PAGES.put("physics",           800);
        GENRE_PAGES.put("mathematics",       500);
        GENRE_PAGES.put("kannada literature",300);
        GENRE_PAGES.put("hindi literature",  320);
        GENRE_PAGES.put("hindi fiction",     380);
        GENRE_PAGES.put("hindi science",     420);
    }

    
    public double calculateBookCarbon(Book book) {

        if (carbonCache.containsKey(book.getIsbn())) {
            return carbonCache.get(book.getIsbn());
        }

        int pages = GENRE_PAGES.getOrDefault(
            book.getGenre().toLowerCase(),
            400
        );

        double paperCarbon = pages * 0.002;

        double transportFactor = switch (book.getLanguage().toLowerCase()) {
            case "kannada" -> 0.1;
            case "hindi"   -> 0.3;
            case "english" -> 0.8;
            default        -> 0.5;
        };

        double productionFactor = 0.5;

        double total = paperCarbon + transportFactor + productionFactor;
        total = Math.round(total * 100.0) / 100.0;

        carbonCache.put(book.getIsbn(), total);
        book.setCarbonScore(total);
        return total;
    }

    
    public double calculateUserCarbon(User user, Map<String, Book> booksByISBN) {
        double total = 0.0;
        for (String isbn : user.getBorrowHistory()) {
            Book book = booksByISBN.get(isbn);
            if (book != null) {
                total += calculateBookCarbon(book);
            }
        }
        return Math.round(total * 100.0) / 100.0;
    }

    
    public List<Book> getEcoAlternatives(Book target, List<Book> catalog) {
        double targetCarbon = calculateBookCarbon(target);
        List<Book> alternatives = new ArrayList<>();

        for (Book b : catalog) {
            if (!b.getIsbn().equals(target.getIsbn())
                    && b.getGenre().equalsIgnoreCase(target.getGenre())) {
                double carbon = calculateBookCarbon(b);
                if (carbon < targetCarbon) alternatives.add(b);
            }
        }

        alternatives.sort(Comparator.comparingDouble(this::calculateBookCarbon));
        return alternatives;
    }

    
    public void printCarbonReport(List<Book> books) {
        System.out.println("\n+══════════════════════════════════════════════════+");
        System.out.println("|       [ECO] CARBON FOOTPRINT SCANNER                |");
        System.out.println("+══════════════════════════════════════════════════+");
        System.out.printf("|  %-38s %8s |%n", "Book Title", "CO₂ (kg)");
        System.out.println("+══════════════════════════════════════════════════+");

        double total = 0;
        for (Book b : books) {
            double carbon = calculateBookCarbon(b);
            total += carbon;
            String bar = carbon < 1.5 ? "[low]" : carbon < 2.5 ? "[med]" : "[high]";
            System.out.printf("|  %-38s %5.2f %s |%n",
                    b.getTitle().length() > 38 ? b.getTitle().substring(0, 35) + "..." : b.getTitle(),
                    carbon, bar);
        }
        System.out.println("+══════════════════════════════════════════════════+");
        System.out.printf("|  %-38s %5.2f kg |%n", "TOTAL FOOTPRINT:", total);
        System.out.println("+══════════════════════════════════════════════════+");
        System.out.println("|  [low] < 1.5 (eco-friendly)  [med] 1.5-2.5  [high] > 2.5 |");
        System.out.println("+══════════════════════════════════════════════════+");
        System.out.printf("  [TIP] Tip: Prefer Hindi or local-published books to save %.0f%% CO₂%n",
                30.0);
    }
}
