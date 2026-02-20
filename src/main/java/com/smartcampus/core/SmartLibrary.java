package com.smartcampus.core;

import com.smartcampus.models.Book;
import com.smartcampus.models.Transaction;
import com.smartcampus.models.User;

import java.time.LocalDate;
import java.util.*;

public class SmartLibrary {

    private final HashMap<String, Book> booksByISBN;

    private final ArrayList<Book> catalog;

    private final LinkedList<User> recentUsers;
    private static final int RECENT_USER_LIMIT = 20;

    private final Queue<Transaction> waitlist;

    private final Stack<Transaction> borrowHistory;

    private final PriorityQueue<Book> highDemandQueue;

    private final HashMap<String, User> usersByID;

    private final int[][] theftHeatmap;
    private static final int GRID_ROWS = 10;
    private static final int GRID_COLS = 10;

    private int transactionCounter = 1;

    public SmartLibrary() {
        this.booksByISBN       = new HashMap<>();
        this.catalog           = new ArrayList<>();
        this.recentUsers       = new LinkedList<>();
        this.waitlist          = new LinkedList<>();
        this.borrowHistory     = new Stack<>();
        this.highDemandQueue   = new PriorityQueue<>();
        this.usersByID         = new HashMap<>();
        this.theftHeatmap      = new int[GRID_ROWS][GRID_COLS];
    }

    
    public boolean addBook(Book book) {
        if (book == null || book.getIsbn() == null) return false;
        if (booksByISBN.containsKey(book.getIsbn())) return false;
        booksByISBN.put(book.getIsbn(), book);
        catalog.add(book);
        highDemandQueue.offer(book);
        return true;
    }

    
    public Book findByISBN(String isbn) {
        return booksByISBN.get(isbn);
    }

    
    public List<Book> searchByTitle(String query) {
        List<Book> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Book b : catalog) {
            if (b.getTitle().toLowerCase().contains(lowerQuery)) {
                results.add(b);
            }
        }
        return results;
    }

    
    public List<Book> searchByGenre(String genre) {
        List<Book> results = new ArrayList<>();
        for (Book b : catalog) {
            if (b.getGenre().equalsIgnoreCase(genre)) results.add(b);
        }
        return results;
    }

    
    public boolean removeBook(String isbn) {
        Book book = booksByISBN.remove(isbn);
        if (book == null) return false;
        catalog.remove(book);
        return true;
    }

    
    public boolean registerUser(User user) {
        if (usersByID.containsKey(user.getUserId())) return false;
        usersByID.put(user.getUserId(), user);
        return true;
    }

    
    public User findUser(String userId) {
        return usersByID.get(userId);
    }

    
    private void trackRecentUser(User user) {
        recentUsers.remove(user);
        recentUsers.addFirst(user);
        if (recentUsers.size() > RECENT_USER_LIMIT) {
            recentUsers.removeLast();
        }
    }

    
    public Transaction issueBook(String userId, String isbn) {
        User user = usersByID.get(userId);
        Book book = booksByISBN.get(isbn);

        if (user == null || book == null) return null;
        if (!book.isAvailable()) {

            Transaction waitTxn = new Transaction(
                    "TXN-W-" + transactionCounter++, userId, isbn, LocalDate.now()
            );
            waitTxn.setStatus(Transaction.Status.RESERVED);
            waitlist.offer(waitTxn);
            System.out.println("  [WAITLIST] No copies available. Added to waitlist: " + waitTxn.getTransactionId());
            return waitTxn;
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setTimesIssued(book.getTimesIssued() + 1);

        Transaction txn = new Transaction(
                "TXN-" + transactionCounter++, userId, isbn, LocalDate.now()
        );

        borrowHistory.push(txn);
        user.addBorrowHistory(isbn);
        trackRecentUser(user);

        rebuildHighDemandQueue();

        return txn;
    }

    
    public boolean returnBook(String transactionId) {

        Stack<Transaction> temp = new Stack<>();
        boolean found = false;

        while (!borrowHistory.isEmpty()) {
            Transaction txn = borrowHistory.pop();
            if (txn.getTransactionId().equals(transactionId)) {
                txn.setReturnDate(LocalDate.now());
                txn.setStatus(Transaction.Status.RETURNED);
                double fine = txn.calculateFine();
                System.out.printf("  [RETURN] %s returned. Fine: ₹%.2f%n", transactionId, fine);

                Book book = booksByISBN.get(txn.getIsbn());
                if (book != null) {
                    book.setAvailableCopies(book.getAvailableCopies() + 1);
                }

                processWaitlist(txn.getIsbn());
                found = true;
                temp.push(txn);
                break;
            }
            temp.push(txn);
        }

        while (!temp.isEmpty()) borrowHistory.push(temp.pop());
        return found;
    }

    
    private void processWaitlist(String isbn) {
        if (!waitlist.isEmpty()) {
            Transaction next = waitlist.peek();
            if (next.getIsbn().equals(isbn)) {
                waitlist.poll();
                System.out.println("  [WAITLIST] Notifying user " + next.getUserId() + " — book available!");
            }
        }
    }

    
    public void reportTheft(String section) {
        if (section == null || section.length() < 2) return;
        int row = section.charAt(0) - 'A';
        int col;
        try {
            col = Integer.parseInt(section.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return;
        }
        if (row >= 0 && row < GRID_ROWS && col >= 0 && col < GRID_COLS) {
            theftHeatmap[row][col]++;
        }
    }

    
    public void simulateTheftData() {
        Random rand = new Random(42);
        for (int i = 0; i < 120; i++) {

            int rowBias = (rand.nextInt(3) == 0) ? rand.nextInt(10)
                    : 2 + rand.nextInt(3);
            int col = rand.nextInt(10);
            theftHeatmap[rowBias][col] += rand.nextInt(3) + 1;
        }
    }

    
    public void printTheftHeatmap() {
        System.out.println("\n+══════════════════════════════════════════════════+");
        System.out.println("|       [MAP] LIBRARY THEFT HEATMAP                    |");
        System.out.println("+══════════════════════════════════════════════════+");
        System.out.println("|  Section  1   2   3   4   5   6   7   8   9  10 |");
        System.out.println("+══════════════════════════════════════════════════+");

        char[] rowLabels = {'A','B','C','D','E','F','G','H','I','J'};
        String[] sectionNames = {
                "Gen Fic ", "Science ", "CompSci ", "DataStr ", "Networks",
                "Math    ", "Physics ", "English ", "Kannada ", "Journals"
        };

        for (int r = 0; r < GRID_ROWS; r++) {
            System.out.printf("|  %c %-8s", rowLabels[r], sectionNames[r]);
            for (int c = 0; c < GRID_COLS; c++) {
                int val = theftHeatmap[r][c];
                String cell = val == 0 ? " ·  "
                        : val <= 2  ? " ░  "
                        : val <= 4  ? " ▒  "
                        : val <= 6  ? " ▓  "
                        : " ██ ";
                System.out.print(cell);
            }
            System.out.println("|");
        }
        System.out.println("+══════════════════════════════════════════════════+");
        System.out.println("|  Legend: · = 0  ░ = 1-2  ▒ = 3-4  ▓ = 5-6  ██ = 7+  |");
        System.out.println("+══════════════════════════════════════════════════+");

        String hotspot = findHotspot();
        System.out.println("  [!] Highest-risk section: " + hotspot);
    }

    
    public String findHotspot() {
        int maxVal = 0;
        int maxR = 0, maxC = 0;
        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLS; c++) {
                if (theftHeatmap[r][c] > maxVal) {
                    maxVal = theftHeatmap[r][c];
                    maxR = r; maxC = c;
                }
            }
        }
        char row = (char)('A' + maxR);
        return row + "" + (maxC + 1) + " (incidents: " + maxVal + ")";
    }

    
    private void rebuildHighDemandQueue() {
        highDemandQueue.clear();
        highDemandQueue.addAll(catalog);
    }

    
    public List<Book> getTopDemandedBooks(int topN) {

        PriorityQueue<Book> copy = new PriorityQueue<>(catalog);
        List<Book> result = new ArrayList<>();
        for (int i = 0; i < topN && !copy.isEmpty(); i++) {
            result.add(copy.poll());
        }
        return result;
    }

    
    public List<User> getStreakLeaderboard() {
        List<User> users = new ArrayList<>(usersByID.values());

        users.sort((a, b) -> {
            if (b.getReadingStreak() != a.getReadingStreak())
                return Integer.compare(b.getReadingStreak(), a.getReadingStreak());
            return Integer.compare(b.getTotalBooksRead(), a.getTotalBooksRead());
        });
        return users;
    }

    
    public List<Book> optimizeBudgetBooks(double budget) {

        List<Book> available = new ArrayList<>(catalog);

        available.sort((a, b) -> {
            double priceA = 200 + a.getTimesIssued() * 10.0;
            double priceB = 200 + b.getTimesIssued() * 10.0;
            double ratioA = a.getRating() / priceA;
            double ratioB = b.getRating() / priceB;
            return Double.compare(ratioB, ratioA);
        });

        List<Book> selected = new ArrayList<>();
        double remaining = budget;

        for (Book b : available) {
            double price = 200 + b.getTimesIssued() * 10.0;
            if (price <= remaining) {
                selected.add(b);
                remaining -= price;
            }
        }
        return selected;
    }

    
    public List<String> bfsShelfPath(String fromSection, String toSection) {

        int[] from = parseSection(fromSection);
        int[] to   = parseSection(toSection);
        if (from == null || to == null) return Collections.emptyList();

        boolean[][] visited = new boolean[GRID_ROWS][GRID_COLS];
        int[][][] parent    = new int[GRID_ROWS][GRID_COLS][2];
        for (int[][] row : parent) for (int[] cell : row) Arrays.fill(cell, -1);

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(from);
        visited[from[0]][from[1]] = true;

        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0}};

        boolean found = false;
        outer:
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            for (int[] d : directions) {
                int nr = cur[0] + d[0];
                int nc = cur[1] + d[1];
                if (nr >= 0 && nr < GRID_ROWS && nc >= 0 && nc < GRID_COLS && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    parent[nr][nc] = cur;
                    if (nr == to[0] && nc == to[1]) { found = true; break outer; }
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        if (!found) return Collections.emptyList();

        List<String> path = new ArrayList<>();
        int[] cur = to;
        while (cur[0] != -1) {
            path.add(0, sectionName(cur[0], cur[1]));
            cur = parent[cur[0]][cur[1]];
        }
        return path;
    }

    private int[] parseSection(String section) {
        if (section == null || section.isEmpty()) return null;
        try {
            int row = section.charAt(0) - 'A';
            int col = Integer.parseInt(section.substring(1)) - 1;
            if (row < 0 || row >= GRID_ROWS || col < 0 || col >= GRID_COLS) return null;
            return new int[]{row, col};
        } catch (Exception e) { return null; }
    }

    private String sectionName(int row, int col) {
        return (char)('A' + row) + "" + (col + 1);
    }

    public HashMap<String, Book> getBooksByISBN()   { return booksByISBN; }
    public ArrayList<Book> getCatalog()              { return catalog; }
    public LinkedList<User> getRecentUsers()          { return recentUsers; }
    public Queue<Transaction> getWaitlist()           { return waitlist; }
    public Stack<Transaction> getBorrowHistory()      { return borrowHistory; }
    public PriorityQueue<Book> getHighDemandQueue()  { return highDemandQueue; }
    public HashMap<String, User> getUsersByID()      { return usersByID; }
    public int[][] getTheftHeatmap()                 { return theftHeatmap; }
}
