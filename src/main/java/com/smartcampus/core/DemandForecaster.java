package com.smartcampus.core;

import com.smartcampus.models.Book;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class DemandForecaster {

    
    public static class TimeSlot implements Comparable<TimeSlot> {
        public final int hour;
        public final double demand;
        public final String label;

        public TimeSlot(int hour, double demand) {
            this.hour = hour;
            this.demand = demand;
            this.label = formatHour(hour);
        }

        private static String formatHour(int h) {
            if (h == 0)  return "12:00 AM";
            if (h < 12)  return h + ":00 AM";
            if (h == 12) return "12:00 PM";
            return (h - 12) + ":00 PM";
        }

        
        @Override
        public int compareTo(TimeSlot other) {
            return Double.compare(other.demand, this.demand);
        }

        @Override
        public String toString() {
            return String.format("%s → Demand: %.1f", label, demand);
        }
    }

    
    private static final double[] BASELINE_DEMAND = {
        0.1, 0.1, 0.1, 0.1, 0.1, 0.2,
        0.5, 1.0, 1.8, 2.5, 2.8,
        2.3, 2.0, 2.6, 2.1, 1.9,
        1.8, 2.0, 2.8, 3.2, 3.0,
        2.5, 1.5, 0.8
    };

    private final SmartLibrary library;

    public DemandForecaster(SmartLibrary library) {
        this.library = library;
    }

    
    public PriorityQueue<TimeSlot> predictPeakHours(LocalDate date, LocalDate examDate) {
        PriorityQueue<TimeSlot> slots = new PriorityQueue<>();

        double dayFactor = switch (date.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> 0.7;
            case MONDAY, FRIDAY   -> 0.9;
            default               -> 1.0;
        };

        double examMultiplier = 1.0;
        if (examDate != null) {
            long daysToExam = java.time.temporal.ChronoUnit.DAYS.between(date, examDate);
            if (daysToExam >= 0 && daysToExam <= 14) {
                examMultiplier = 1.0 + 1.5 * Math.exp(-daysToExam / 5.0);
            }
        }

        for (int h = 0; h < 24; h++) {
            double score = BASELINE_DEMAND[h] * dayFactor * examMultiplier;

            score += (new Random(date.getDayOfYear() + h).nextDouble() * 0.2);
            slots.offer(new TimeSlot(h, score));
        }

        return slots;
    }

    
    public List<TimeSlot> getTopPeakHours(LocalDate date, LocalDate examDate, int topN) {
        PriorityQueue<TimeSlot> pq = predictPeakHours(date, examDate);
        List<TimeSlot> result = new ArrayList<>();
        for (int i = 0; i < topN && !pq.isEmpty(); i++) {
            result.add(pq.poll());
        }
        return result;
    }

    
    public List<Book> autoReserveExamBooks(String semester, int topK) {

        List<Book> topBooks = library.getTopDemandedBooks(topK);

        for (Book b : topBooks) {
            b.setReserved(true);
        }

        System.out.println("\n  [AUTO-RESERVE] Exam Season: " + semester);
        System.out.println("  Reserved " + topBooks.size() + " high-demand books:");
        for (Book b : topBooks) {
            System.out.printf("     %-40s | Issued: %d times%n", b.getTitle(), b.getTimesIssued());
        }
        return topBooks;
    }

    
    public void printExamRushCalendar() {
        System.out.println("\n+══════════════════════════════════════════+");
        System.out.println("|    [CAL] EXAM RUSH PREDICTION              |");
        System.out.println("+══════════════════════════════════════════+");
        for (Month m : Month.values()) {
            double demandMultiplier = getMonthlyDemandMultiplier(m);
            String bar = "█".repeat((int)(demandMultiplier * 5));
            String warning = demandMultiplier >= 2.0 ? " [!] EXAM RUSH" : "";
            System.out.printf("|  %-9s %s%.0f%%%s%n",
                    m.name().substring(0, 3), bar, demandMultiplier * 50, warning);
        }
        System.out.println("+══════════════════════════════════════════+");
    }

    
    private double getMonthlyDemandMultiplier(Month m) {
        return switch (m) {
            case APRIL, MAY, NOVEMBER, DECEMBER -> 2.2;
            case MARCH, OCTOBER                 -> 1.8;
            case JANUARY, AUGUST                -> 1.4;
            case JUNE, JULY                     -> 0.6;
            default                             -> 1.0;
        };
    }
}
