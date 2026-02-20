package com.smartcampus;

import com.smartcampus.core.*;
import com.smartcampus.models.*;
import com.smartcampus.utils.*;

import java.time.LocalDate;
import java.util.*;

public class SmartCampusLibrary {

    private static SmartLibrary     library;
    private static Recommender      recommender;
    private static DemandForecaster forecaster;
    private static TheftAnalytics   theftAnalytics;
    private static StudyMatcher     studyMatcher;
    private static CarbonTracker    carbonTracker;
    private static KannadaTransliterator kannadaSearch;
    private static HindiTransliterator   hindiSearch;
    private static QRNavigator      qrNavigator;
    private static Scanner          scanner;

    public static void main(String[] args) {
        initSystem();
        seedDemoData();
        printBanner();
        runMainMenu();
    }

    private static void initSystem() {
        library        = new SmartLibrary();
        recommender    = new Recommender(library);
        forecaster     = new DemandForecaster(library);
        theftAnalytics = new TheftAnalytics(library);
        studyMatcher   = new StudyMatcher();
        carbonTracker  = new CarbonTracker();
        kannadaSearch  = new KannadaTransliterator();
        hindiSearch    = new HindiTransliterator();
        qrNavigator    = new QRNavigator(library);
        scanner        = new Scanner(System.in);
    }

    private static void seedDemoData() {
        System.out.println("  [INIT] Loading Stacked Shelves...");

        Book[] books = {
            new Book("978-0262033848", "Introduction to Algorithms (CLRS)",    "Cormen et al.",      "Algorithms",       "English", "C1", 5),
            new Book("978-0201633610", "Design Patterns: GoF",                  "Gang of Four",       "Software Design",  "English", "C3", 3),
            new Book("978-1449335182", "Learning Python",                        "Mark Lutz",          "Programming",      "English", "C2", 4),
            new Book("978-0134685991", "Effective Java",                         "Joshua Bloch",       "Java",             "English", "C2", 6),
            new Book("978-0201485677", "The Mythical Man-Month",                 "Fred Brooks",        "Software Eng",     "English", "D4", 2),
            new Book("978-1491950357", "Python for Data Analysis",               "Wes McKinney",       "Data Science",     "English", "C5", 4),
            new Book("978-0262046305", "Deep Learning",                          "Goodfellow et al.",  "Machine Learning", "English", "E1", 3),
            new Book("978-1617295249", "Grokking Algorithms",                    "Aditya Bhargava",    "Algorithms",       "English", "C1", 7),
            new Book("978-0596517748", "JavaScript: The Good Parts",             "Douglas Crockford",  "Web Dev",          "English", "C4", 3),
            new Book("978-0132350884", "Clean Code",                             "Robert C. Martin",   "Software Eng",     "English", "D3", 5),
            new Book("978-9387283312", "Data Structures in C",                   "Reema Thareja",      "Data Structures",  "English", "C1", 8),
            new Book("978-8173715600", "ಕನ್ನಡ ಸಾಹಿತ್ಯ ಚರಿತ್ರೆ",               "R.S. Mugali",        "Kannada Literature","Kannada","I2", 2),
            new Book("978-8171302215", "ಕುವೆಂಪು ಕಾವ್ಯ ಸಂಗ್ರಹ",               "Kuvempu",            "Kannada Poetry",   "Kannada", "I3", 3),
            new Book("978-0321125217", "Domain-Driven Design",                   "Eric Evans",         "Software Design",  "English", "D5", 2),
            new Book("978-0596009205", "Head First Design Patterns",             "Freeman & Robson",   "Software Design",  "English", "D3", 4),
            new Book("978-8126918525", "हिंदी साहित्य का इतिहास",              "Ramchandra Shukla",  "Hindi Literature", "Hindi",   "J1", 2),
            new Book("978-8171820191", "गोदान",                                  "Munshi Premchand",   "Hindi Fiction",    "Hindi",   "J2", 3),
            new Book("978-8126715480", "विज्ञान की दुनिया",                     "Arvind Gupta",       "Hindi Science",    "Hindi",   "J3", 4),
        };

        for (Book b : books) {

            b.setTimesIssued(new Random(b.getIsbn().hashCode()).nextInt(50) + 5);
            b.setRating(3.5 + new Random(b.getTitle().hashCode()).nextDouble() * 1.5);
        }

        books[11].setKannadaTitle("ಕನ್ನಡ ಸಾಹಿತ್ಯ ಚರಿತ್ರೆ");
        books[12].setKannadaTitle("ಕುವೆಂಪು ಕಾವ್ಯ");

        books[15].setHindiTitle("हिंदी साहित्य का इतिहास");
        books[16].setHindiTitle("गोदान");
        books[17].setHindiTitle("विज्ञान की दुनिया");

        for (Book b : books) library.addBook(b);

        kannadaSearch.indexBooks(library.getCatalog());
        hindiSearch.indexBooks(library.getCatalog());

        library.simulateTheftData();

        User[] users = {
            new User("U001", "Aarav Sharma",    "aarav@smartcampus.edu",   User.Role.STUDENT, User.Department.CSE,      5),
            new User("U002", "Priya Nair",      "priya@smartcampus.edu",   User.Role.STUDENT, User.Department.MTECH_AI, 2),
            new User("U003", "Rohit Desai",     "rohit@smartcampus.edu",   User.Role.STUDENT, User.Department.CSE,      3),
            new User("U004", "Meena Krishnan",  "meena@smartcampus.edu",   User.Role.STUDENT, User.Department.ECE,      4),
            new User("U005", "Arjun Reddy",     "arjun@smartcampus.edu",   User.Role.STUDENT, User.Department.CSE,      5),
            new User("U006", "Prof. Venkat",    "venkat@smartcampus.edu",  User.Role.FACULTY, User.Department.CSE,      0),
        };

        users[0].rateBook("978-0262033848", 5.0); users[0].addBorrowHistory("978-0262033848");
        users[0].rateBook("978-1617295249", 4.5); users[0].addBorrowHistory("978-1617295249");
        users[0].rateBook("978-0134685991", 4.0); users[0].addBorrowHistory("978-0134685991");
        users[0].setReadingStreak(15); users[0].setTotalBooksRead(12);
        users[0].setStudySubjects(Arrays.asList("Algorithms", "Data Structures", "Java"));

        users[1].rateBook("978-0262046305", 5.0); users[1].addBorrowHistory("978-0262046305");
        users[1].rateBook("978-1491950357", 4.0); users[1].addBorrowHistory("978-1491950357");
        users[1].rateBook("978-0262033848", 4.5); users[1].addBorrowHistory("978-0262033848");
        users[1].setReadingStreak(8);  users[1].setTotalBooksRead(7);
        users[1].setStudySubjects(Arrays.asList("Machine Learning", "Python", "Data Science"));

        users[2].rateBook("978-0201633610", 4.0); users[2].addBorrowHistory("978-0201633610");
        users[2].rateBook("978-0132350884", 4.5); users[2].addBorrowHistory("978-0132350884");
        users[2].rateBook("978-0134685991", 5.0); users[2].addBorrowHistory("978-0134685991");
        users[2].setReadingStreak(22); users[2].setTotalBooksRead(18);
        users[2].setStudySubjects(Arrays.asList("Java", "Software Design", "Algorithms"));

        users[3].rateBook("978-1491950357", 3.5); users[3].addBorrowHistory("978-1491950357");
        users[3].rateBook("978-0596009205", 4.0); users[3].addBorrowHistory("978-0596009205");
        users[3].setReadingStreak(5);  users[3].setTotalBooksRead(4);
        users[3].setStudySubjects(Arrays.asList("Data Science", "Python"));

        users[4].rateBook("978-0262033848", 4.0); users[4].addBorrowHistory("978-0262033848");
        users[4].rateBook("978-9387283312", 5.0); users[4].addBorrowHistory("978-9387283312");
        users[4].rateBook("978-0321125217", 4.0); users[4].addBorrowHistory("978-0321125217");
        users[4].setReadingStreak(30); users[4].setTotalBooksRead(25);
        users[4].setStudySubjects(Arrays.asList("Data Structures", "Algorithms", "Software Design"));

        users[5].rateBook("978-0262033848", 5.0); users[5].addBorrowHistory("978-0262033848");
        users[5].rateBook("978-0321125217", 5.0); users[5].addBorrowHistory("978-0321125217");
        users[5].setReadingStreak(45); users[5].setTotalBooksRead(50);
        users[5].setStudySubjects(Arrays.asList("Algorithms", "Software Design", "Java"));

        for (User u : users) library.registerUser(u);

        System.out.println("  [INIT] " + library.getCatalog().size() + " books | "
                + library.getUsersByID().size() + " users loaded");
        System.out.println("  [INIT] System ready!\n");
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("     Stacked Shelves v2026 -- DSA-Powered Library AI");
        System.out.println("     12 DSA Structures");
        System.out.println("     Console-Based | 12 DSA Structures | AI Engine");
        System.out.println("  ============================================================");
        System.out.println();
    }

    private static void runMainMenu() {
        while (true) {
            printMenu();
            System.out.print("  Enter choice: ");
            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1"  -> handleRecommendations();
                    case "2"  -> handlePeakHours();
                    case "3"  -> handleTheftHeatmap();
                    case "4"  -> handleStudyGroups();
                    case "5"  -> handleCarbonFootprint();
                    case "6"  -> handleKannadaSearch();
                    case "h", "H" -> handleHindiSearch();
                    case "7"  -> handleExamAutoReserve();
                    case "8"  -> handleBudgetOptimizer();
                    case "9"  -> handleStreakLeaderboard();
                    case "0"  -> handleQRNavigator();
                    case "s", "S" -> handleBookSearch();
                    case "i", "I" -> handleIssueBook();
                    case "r", "R" -> handleReturnBook();
                    case "d", "D" -> handleDSADemo();
                    case "q", "Q" -> {
                        System.out.println("\n  Signing out. Happy reading!\n");
                        return;
                    }
                    default -> System.out.println("  [X] Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("  [!] Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("  +--------------------------------------------+");
        System.out.println("  |       MAIN MENU -- Stacked Shelves         |");
        System.out.println("  +--------------------------------------------+");
        System.out.println("  |  [1]  AI Book Recommendations (Cosine)     |");
        System.out.println("  |  [2]  Predict Peak Hours (PriorityQueue)   |");
        System.out.println("  |  [3]  Theft Heatmap (2D Array + ASCII)     |");
        System.out.println("  |  [4]  Find Study Buddies (Graph Matching)  |");
        System.out.println("  |  [5]  Carbon Footprint Scanner             |");
        System.out.println("  |  [6]  Kannada Book Search (Trie + Fuzzy)   |");
        System.out.println("  |  H    Hindi Book Search   (Trie + Fuzzy)   |");
        System.out.println("  |  [7]  Exam Rush Auto-Reserve (Queue)       |");
        System.out.println("  |  [8]  Budget Book Optimizer (Greedy)       |");
        System.out.println("  |  [9]  Reading Streak Leaderboard           |");
        System.out.println("  |  [0]  QR Shelf Navigator (BFS)             |");
        System.out.println("  +--------------------------------------------+");
        System.out.println("  |  S   Search Books   I   Issue Book         |");
        System.out.println("  |  R   Return Book    D   DSA Complexity Demo|");
        System.out.println("  |  Q   Quit                                  |");
        System.out.println("  +--------------------------------------------+");
    }

    
    private static void handleRecommendations() {
        System.out.println("\n  --- [1] AI BOOK RECOMMENDATIONS (Collaborative Filtering) ---");
        System.out.println("  Available users: U001-U006");
        System.out.print("  Enter User ID: ");
        String uid = scanner.nextLine().trim();

        User user = library.findUser(uid);
        if (user == null) {
            System.out.println("  [X] User not found. Try U001-U006.");
            return;
        }

        System.out.printf("%n  Generating recommendations for %s (%s)...%n", user.getName(), user.getDepartment());
        System.out.println("  Algorithm: Cosine Similarity on sparse user-book rating matrix");
        System.out.println("  Time Complexity: O(u × b) | Space: O(sparse ratings)");

        List<Book> recs = recommender.getRecommendations(user, 5);
        List<User> similar = recommender.findSimilarUsers(user, 3);

        System.out.println("\n  Most similar users:");
        for (User s : similar) {
            double sim = recommender.cosineSimilarity(user.getBookRatings(), s.getBookRatings());
            System.out.printf("    %-20s  similarity: %.2f%n", s.getName(), sim);
        }

        System.out.println("\n  Top " + recs.size() + " Recommended Books:");
        if (recs.isEmpty()) {
            System.out.println("    (Rate more books to get personalized recommendations!)");
        } else {
            for (int i = 0; i < recs.size(); i++) {
                System.out.printf("    %d. %s%n", i + 1, recs.get(i));
            }
        }
    }

    
    private static void handlePeakHours() {
        System.out.println("\n  --- [2] PEAK HOUR PREDICTOR ---");
        System.out.println("  Algorithm: Time-series scoring with PriorityQueue (max-heap)");
        System.out.println("  Time Complexity: O(h log h) where h = 24 hourly slots");

        LocalDate today = LocalDate.now();
        LocalDate examDate = today.plusDays(10);

        System.out.println("  Date: " + today + " | Upcoming Exam: " + examDate);
        System.out.println("\n  Top 5 Predicted Peak Hours:");

        List<DemandForecaster.TimeSlot> peaks = forecaster.getTopPeakHours(today, examDate, 5);
        for (int i = 0; i < peaks.size(); i++) {
            String bar = "█".repeat((int)(peaks.get(i).demand * 3));
            System.out.printf("    %d. %-10s %s (%.1f)%n",
                    i + 1, peaks.get(i).label, bar, peaks.get(i).demand);
        }

        forecaster.printExamRushCalendar();
    }

    
    private static void handleTheftHeatmap() {
        System.out.println("\n  --- [3] THEFT HEATMAP ---");
        System.out.println("  Algorithm: 2D spatial analysis on 10×10 int[][] grid");
        System.out.println("  Time Complexity: O(1) per incident | O(100) for full display");

        library.printTheftHeatmap();
        theftAnalytics.printRiskReport();
    }

    
    private static void handleStudyGroups() {
        System.out.println("\n  --- [4] STUDY GROUP MATCHER (Graph Bipartite Matching) ---");
        System.out.println("  Algorithm: Greedy bipartite matching on compatibility graph");
        System.out.println("  Edge weight: Jaccard(subjects) + semester + department bonuses");
        System.out.println("  Time Complexity: O(V²) graph build + O(E log E) matching");

        List<User> allUsers = new ArrayList<>(library.getUsersByID().values());
        studyMatcher.buildGraph(allUsers);
        studyMatcher.printGraph();

        List<StudyGroup> groups = studyMatcher.matchStudyGroups(3);
        System.out.println("\n  📋 Matched Study Groups:");
        for (StudyGroup g : groups) {
            System.out.print("  " + g);
        }
    }

    
    private static void handleCarbonFootprint() {
        System.out.println("\n  --- [5] CARBON FOOTPRINT SCANNER ---");
        System.out.println("  Formula: (pages × 0.002) + transport_factor + production_factor");
        System.out.println("  Memoization: HashMap cache for O(1) repeated lookups");

        List<Book> sampleBooks = library.getCatalog().subList(0, Math.min(8, library.getCatalog().size()));
        carbonTracker.printCarbonReport(sampleBooks);

        Book target = library.findByISBN("978-0262033848");
        if (target != null) {
            List<Book> alts = carbonTracker.getEcoAlternatives(target, library.getCatalog());
            System.out.println("\n  🌿 Eco-friendly alternatives to '" + target.getTitle() + "':");
            if (alts.isEmpty()) {
                System.out.println("    (No lower-carbon books in same genre)");
            } else {
                alts.stream().limit(3).forEach(b ->
                    System.out.printf("    - %-40s %.2f kg CO₂%n", b.getTitle(), carbonTracker.calculateBookCarbon(b)));
            }
        }
    }

    
    private static void handleKannadaSearch() {
        System.out.println("\n  --- [6] KANNADA BOOK SEARCH (Trie + Levenshtein) ---");
        System.out.println("  DSA Structure: TrieNode for O(L) prefix lookup");
        System.out.println("  Fuzzy: Levenshtein DP — O(m×n) edit distance");
        System.out.println("\n  Try: 'kannada', 'sahitya', 'kavya', or ಕನ್ನಡ");
        System.out.print("  Enter Kannada query (Latin phonetic or Kannada Unicode): ");
        String query = scanner.nextLine().trim();

        List<Book> results = kannadaSearch.searchKannada(query, library.getCatalog(), 3);
        System.out.println("\n  Search Results (" + results.size() + " found):");
        if (results.isEmpty()) {
            System.out.println("    No books found. Try: 'kannada', 'sahitya', 'pustaka'");
        } else {
            results.forEach(b -> System.out.println("    • " + b));
        }
    }

    private static void handleHindiSearch() {
        System.out.println("\n  --- [H] HINDI BOOK SEARCH (Trie + Levenshtein) ---");
        System.out.println("  DSA Structure: TrieNode for O(L) prefix lookup");
        System.out.println("  Fuzzy: Levenshtein DP — O(m×n) edit distance");
        System.out.println("\n  Try: 'hindi', 'sahitya', 'vigyan', 'kahani', or हिंदी");
        System.out.print("  Enter Hindi query (Latin phonetic or Hindi Unicode): ");
        String query = scanner.nextLine().trim();

        List<Book> results = hindiSearch.searchHindi(query, library.getCatalog(), 3);
        System.out.println("\n  Search Results (" + results.size() + " found):");
        if (results.isEmpty()) {
            System.out.println("    No books found. Try: 'hindi', 'sahitya', 'vigyan', 'kahani'");
        } else {
            results.forEach(b -> System.out.println("    • " + b));
        }
    }

    
    private static void handleExamAutoReserve() {
        System.out.println("\n  --- [7] EXAM RUSH AUTO-RESERVE ---");
        System.out.println("  Algorithm: PriorityQueue sorts by timesIssued (demand proxy)");
        System.out.println("  Time Complexity: O(b log b) for priority queue rebuild");
        forecaster.autoReserveExamBooks("Semester 5 — Nov 2026", 5);
    }

    
    private static void handleBudgetOptimizer() {
        System.out.println("\n  --- [8] BUDGET BOOK OPTIMIZER (Greedy Knapsack) ---");
        System.out.println("  Algorithm: Fractional-knapsack-inspired greedy");
        System.out.println("  Sort by: rating / price ratio (descending)");
        System.out.println("  Time Complexity: O(n log n) sort + O(n) selection");

        System.out.print("  Enter library budget (₹): ");
        double budget;
        try {
            budget = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            budget = 5000;
            System.out.println("  Using default: ₹5000");
        }

        List<Book> selected = library.optimizeBudgetBooks(budget);
        double totalCost = selected.stream().mapToDouble(b -> 200 + b.getTimesIssued() * 10.0).sum();

        System.out.printf("%n  Optimal purchase within ₹%.0f budget:%n", budget);
        for (Book b : selected) {
            double price = 200 + b.getTimesIssued() * 10.0;
            System.out.printf("    %-40s ₹%.0f | Rating: %.1f%n",
                    b.getTitle().length() > 40 ? b.getTitle().substring(0, 37) + "..." : b.getTitle(),
                    price, b.getRating());
        }
        System.out.printf("  Total: ₹%.0f | Books selected: %d%n", totalCost, selected.size());
    }

    
    private static void handleStreakLeaderboard() {
        System.out.println("\n  --- [9] READING STREAK LEADERBOARD ---");
        System.out.println("  Algorithm: Comparator sort O(u log u)");
        System.out.println("  Sort key: readingStreak DESC, totalBooksRead DESC");

        List<User> leaderboard = library.getStreakLeaderboard();
        System.out.println("\n  Reading Champions:");
        System.out.println("  " + "─".repeat(60));
        System.out.printf("  %-4s %-20s %-12s %6s %8s%n", "Rank", "Name", "Dept", "Streak", "Books");
        System.out.println("  " + "─".repeat(60));

        String[] medals = {"[1]","[2]","[3]","[4]","[5]","[6]"};
        for (int i = 0; i < leaderboard.size(); i++) {
            User u = leaderboard.get(i);
            String medal = i < medals.length ? medals[i] : "   ";
            System.out.printf("  %s  %-20s %-12s %5d   %5d books%n",
                    medal, u.getName(), u.getDepartment(),
                    u.getReadingStreak(), u.getTotalBooksRead());
        }
    }

    
    private static void handleQRNavigator() {
        System.out.println("\n  --- [0] QR SHELF NAVIGATOR (BFS) ---");
        System.out.println("  DSA: BFS on 10×10 grid graph");
        System.out.println("  Time Complexity: O(V + E) = O(200) for 10×10 grid");
        System.out.println("  Sections: A1–J10 (Row A-J = genre, Col 1-10 = shelf)");
        System.out.print("  From section (e.g., A1): ");
        String from = scanner.nextLine().trim().toUpperCase();
        System.out.print("  To section (e.g., J10): ");
        String to = scanner.nextLine().trim().toUpperCase();
        qrNavigator.navigate(from, to);
    }

    
    private static void handleBookSearch() {
        System.out.println("\n  --- BOOK SEARCH ---");
        System.out.println("  1. By ISBN (O(1))  2. By Title (O(n))  3. By Genre (O(n))");
        System.out.print("  Choice: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                System.out.print("  ISBN: ");
                String isbn = scanner.nextLine().trim();
                Book book = library.findByISBN(isbn);
                System.out.println(book != null ? "  Found: " + book : "  Not found.");
            }
            case "2" -> {
                System.out.print("  Title query: ");
                List<Book> results = library.searchByTitle(scanner.nextLine().trim());
                results.forEach(b -> System.out.println("  • " + b));
            }
            case "3" -> {
                System.out.print("  Genre: ");
                List<Book> results = library.searchByGenre(scanner.nextLine().trim());
                results.forEach(b -> System.out.println("  • " + b));
            }
        }
    }

    
    private static void handleIssueBook() {
        System.out.println("\n  --- ISSUE BOOK ---");
        System.out.print("  User ID (e.g., U001): ");
        String uid = scanner.nextLine().trim();
        System.out.print("  ISBN: ");
        String isbn = scanner.nextLine().trim();
        Transaction txn = library.issueBook(uid, isbn);
        if (txn != null) {
            System.out.println("  [OK] Transaction: " + txn);
        } else {
            System.out.println("  [X] Could not issue book. Check user ID and ISBN.");
        }
    }

    
    private static void handleReturnBook() {
        System.out.println("\n  --- RETURN BOOK ---");
        System.out.print("  Transaction ID (e.g., TXN-1): ");
        String txnId = scanner.nextLine().trim();
        boolean success = library.returnBook(txnId);
        System.out.println(success ? "  [OK] Book returned successfully!" : "  [X] Transaction not found.");
    }

    
    private static void handleDSADemo() {
        System.out.println("\n  --- [D] DSA COMPLEXITY REFERENCE ---");
        System.out.println("  +--------------------------------------------------------------+");
        System.out.printf("  | %-30s %-15s %-10s |%n", "Operation", "Time", "Space");
        System.out.println("  +--------------------------------------------------------------+");
        String[][] complexities = {
            {"ISBN Lookup (HashMap)",      "O(1) avg",    "O(n)"},
            {"Add Book (HashMap+List)",    "O(1)",        "O(1)"},
            {"Title Search (ArrayList)",   "O(n)",        "O(k)"},
            {"Recommendations (Cosine)",   "O(u × b)",    "O(nm)"},
            {"Peak Prediction (PQ)",       "O(h log h)",  "O(h)"},
            {"Theft Heatmap (2D Array)",   "O(1) update", "O(100)"},
            {"Study Match (Graph+PQ)",     "O(V²+E logE)","O(V²)"},
            {"Kannada Trie Search",        "O(L)",        "O(AΣ×L)"},
            {"Levenshtein Fuzzy",          "O(m × n)",    "O(m×n)"},
            {"BFS Navigation",             "O(V + E)",    "O(V)"},
            {"Budget Optimizer (Greedy)",  "O(n log n)",  "O(n)"},
            {"Leaderboard Sort",           "O(u log u)",  "O(u)"},
        };
        for (String[] row : complexities) {
            System.out.printf("  | %-30s %-15s %-10s |%n", row[0], row[1], row[2]);
        }
        System.out.println("  +--------------------------------------------------------------+");
        System.out.println("  DSA Structures: HashMap × 2, ArrayList, LinkedList,");
        System.out.println("  Queue, Stack, PriorityQueue, int[][], Adjacency List,");
        System.out.println("  TrieNode, DP Table (Levenshtein), Sparse Rating Matrix");
        System.out.println("  Total: 12+ structures [OK]");
    }
}
