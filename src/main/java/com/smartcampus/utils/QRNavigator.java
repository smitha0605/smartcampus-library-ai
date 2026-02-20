package com.smartcampus.utils;

import com.smartcampus.core.SmartLibrary;
import java.util.List;

public class QRNavigator {

    private final SmartLibrary library;

    public QRNavigator(SmartLibrary library) {
        this.library = library;
    }

    
    public void navigate(String fromSection, String toSection) {
        System.out.println("\n  [NAV]  QR SHELF NAVIGATOR (BFS Pathfinding)");
        System.out.println("  From: Section " + fromSection + "  →  To: Section " + toSection);

        List<String> path = library.bfsShelfPath(fromSection, toSection);

        if (path.isEmpty()) {
            System.out.println("  [ERROR] Could not find path. Check section codes (e.g., A1, J10)");
            return;
        }

        System.out.println("  Shortest path (" + (path.size() - 1) + " steps):");
        System.out.print("  ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) System.out.print(" → ");
        }
        System.out.println();
        System.out.println("  Estimated walk: ~" + ((path.size() - 1) * 5) + " seconds");

        System.out.println("\n  [MAP] Grid Map (path highlighted with *):");
        printGridWithPath(path);
    }

    private void printGridWithPath(List<String> path) {
        boolean[][] onPath = new boolean[10][10];
        for (String section : path) {
            if (section.length() < 2) continue;
            int r = section.charAt(0) - 'A';
            try {
                int c = Integer.parseInt(section.substring(1)) - 1;
                if (r >= 0 && r < 10 && c >= 0 && c < 10) onPath[r][c] = true;
            } catch (NumberFormatException ignored) {}
        }

        System.out.print("     ");
        for (int c = 1; c <= 10; c++) System.out.printf(" %2d", c);
        System.out.println();

        for (int r = 0; r < 10; r++) {
            System.out.printf("  %c  ", (char)('A' + r));
            for (int c = 0; c < 10; c++) {
                System.out.print(onPath[r][c] ? "  *" : "  .");
            }
            System.out.println();
        }
    }
}
