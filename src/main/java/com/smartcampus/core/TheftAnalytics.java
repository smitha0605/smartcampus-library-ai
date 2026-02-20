package com.smartcampus.core;

import java.util.*;

public class TheftAnalytics {

    private final SmartLibrary library;

    public TheftAnalytics(SmartLibrary library) {
        this.library = library;
    }

    
    public double[] computeRowRiskScores() {
        int[][] grid = library.getTheftHeatmap();
        double[] rowScores = new double[grid.length];
        for (int r = 0; r < grid.length; r++) {
            double sum = 0;
            for (int c = 0; c < grid[r].length; c++) sum += grid[r][c];
            rowScores[r] = sum / grid[r].length;
        }
        return rowScores;
    }

    
    public List<String> getHighRiskSections(int topN) {
        int[][] grid = library.getTheftHeatmap();

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[0] - a[0]);
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] > 0) {
                    pq.offer(new int[]{grid[r][c], r, c});
                }
            }
        }
        List<String> results = new ArrayList<>();
        for (int i = 0; i < topN && !pq.isEmpty(); i++) {
            int[] top = pq.poll();
            char row = (char)('A' + top[1]);
            results.add(row + "" + (top[2] + 1) + " (" + top[0] + " incidents)");
        }
        return results;
    }

    
    public void printRiskReport() {
        System.out.println("\n  [!] Top High-Risk Sections:");
        List<String> hotspots = getHighRiskSections(5);
        for (int i = 0; i < hotspots.size(); i++) {
            System.out.printf("    %d. Section %s%n", i + 1, hotspots.get(i));
        }

        double[] rowScores = computeRowRiskScores();
        System.out.println("\n   Average Risk by Row:");
        String[] labels = {"A-General Fiction","B-Science","C-CompSci","D-DataStructures",
                           "E-Networks","F-Math","G-Physics","H-English","I-Kannada","J-Journals"};
        for (int r = 0; r < rowScores.length; r++) {
            String bar = "█".repeat((int)(rowScores[r] * 2));
            System.out.printf("    %s: %s %.1f%n", labels[r], bar, rowScores[r]);
        }
    }
}
