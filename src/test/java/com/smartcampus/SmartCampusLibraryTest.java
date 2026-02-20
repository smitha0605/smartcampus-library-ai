package com.smartcampus;

import com.smartcampus.core.*;
import com.smartcampus.models.*;
import com.smartcampus.utils.KannadaTransliterator;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SmartCampusLibraryTest {

    private SmartLibrary library;
    private Recommender recommender;
    private DemandForecaster forecaster;
    private StudyMatcher studyMatcher;
    private KannadaTransliterator trie;

    @BeforeEach
    void setUp() {
        library      = new SmartLibrary();
        recommender  = new Recommender(library);
        forecaster   = new DemandForecaster(library);
        studyMatcher = new StudyMatcher();
        trie         = new KannadaTransliterator();
    }

    @Test @Order(1)
    @DisplayName("HashMap: O(1) ISBN lookup returns correct book")
    void testHashMapLookup() {
        Book book = new Book("978-0001", "Test Book", "Author", "CS", "English", "A1", 3);
        library.addBook(book);
        Book found = library.findByISBN("978-0001");
        assertNotNull(found);
        assertEquals("Test Book", found.getTitle());
    }

    @Test @Order(2)
    @DisplayName("HashMap: Duplicate ISBN rejected")
    void testDuplicateISBN() {
        Book b1 = new Book("978-0001", "Book One", "A", "CS", "English", "A1", 2);
        Book b2 = new Book("978-0001", "Book Two", "B", "CS", "English", "A2", 1);
        assertTrue(library.addBook(b1));
        assertFalse(library.addBook(b2));
    }

    @Test @Order(3)
    @DisplayName("HashMap: Non-existent ISBN returns null")
    void testMissingISBN() {
        assertNull(library.findByISBN("978-NOTEXIST"));
    }

    @Test @Order(4)
    @DisplayName("Forecaster: Returns 24 time slots")
    void testForecasterSlotCount() {
        java.util.PriorityQueue<DemandForecaster.TimeSlot> slots =
                forecaster.predictPeakHours(java.time.LocalDate.now(), null);
        assertEquals(24, slots.size());
    
    }

    @Test @Order(4)
    @DisplayName("ArrayList: Catalog grows dynamically")
    void testCatalogGrowth() {
        for (int i = 0; i < 100; i++) {
            library.addBook(new Book("978-" + String.format("%04d", i),
                    "Book " + i, "Author", "CS", "English", "A1", 1));
        }
        assertEquals(100, library.getCatalog().size());
    }

    @Test @Order(5)
    @DisplayName("ArrayList: Title search returns matching books")
    void testTitleSearch() {
        library.addBook(new Book("978-0001", "Introduction to Algorithms", "Cormen", "CS", "English", "A1", 3));
        library.addBook(new Book("978-0002", "Algorithm Design", "Kleinberg", "CS", "English", "A2", 2));
        library.addBook(new Book("978-0003", "Clean Code", "Martin", "SE", "English", "B1", 4));

        List<Book> results = library.searchByTitle("Algorithm");
        assertEquals(2, results.size());
    }

    @Test @Order(6)
    @DisplayName("Stack: Borrow history records transaction")
    void testBorrowHistory() {
        Book book = new Book("978-0001", "Test", "A", "CS", "English", "A1", 2);
        library.addBook(book);
        User user = new User("U001", "Aarav", "a@b.com", User.Role.STUDENT, User.Department.CSE, 5);
        library.registerUser(user);

        Transaction txn = library.issueBook("U001", "978-0001");
        assertNotNull(txn);
        assertEquals("U001", txn.getUserId());
        assertEquals("978-0001", txn.getIsbn());
        assertEquals(Transaction.Status.ISSUED, txn.getStatus());
    }

    @Test @Order(7)
    @DisplayName("Queue: Waitlist used when no copies available")
    void testWaitlist() {
        Book book = new Book("978-0001", "Rare Book", "A", "CS", "English", "A1", 1);
        library.addBook(book);

        User u1 = new User("U001", "User1", "a@b.com", User.Role.STUDENT, User.Department.CSE, 1);
        User u2 = new User("U002", "User2", "b@b.com", User.Role.STUDENT, User.Department.CSE, 2);
        library.registerUser(u1);
        library.registerUser(u2);

        library.issueBook("U001", "978-0001");
        Transaction waitTxn = library.issueBook("U002", "978-0001");
        assertNotNull(waitTxn);
        assertEquals(Transaction.Status.RESERVED, waitTxn.getStatus());
        assertEquals(1, library.getWaitlist().size());
    }

    @Test @Order(8)
    @DisplayName("PriorityQueue: Top demanded books ranked by timesIssued")
    void testTopDemandedBooks() {
        Book b1 = new Book("978-0001", "Low Demand",  "A", "CS", "English", "A1", 5);
        Book b2 = new Book("978-0002", "High Demand", "B", "CS", "English", "A2", 5);
        b1.setTimesIssued(5);
        b2.setTimesIssued(100);
        library.addBook(b1);
        library.addBook(b2);

        List<Book> top = library.getTopDemandedBooks(1);
        assertEquals("High Demand", top.get(0).getTitle());
    }

    @Test @Order(9)
    @DisplayName("2D Array: Theft incident recorded at correct cell")
    void testTheftHeatmap() {
        library.reportTheft("A1");
        library.reportTheft("A1");
        library.reportTheft("C5");
        assertEquals(2, library.getTheftHeatmap()[0][0]);
        assertEquals(1, library.getTheftHeatmap()[2][4]);
    }

    @Test @Order(10)
    @DisplayName("2D Array: Invalid section ignored gracefully")
    void testInvalidSection() {
        assertDoesNotThrow(() -> library.reportTheft("Z99"));
        assertDoesNotThrow(() -> library.reportTheft(null));
        assertDoesNotThrow(() -> library.reportTheft(""));
    }

    @Test @Order(11)
    @DisplayName("Cosine: Identical vectors return similarity 1.0")
    void testCosineSameVectors() {
        Map<String, Double> v1 = Map.of("A", 3.0, "B", 4.0);
        Map<String, Double> v2 = Map.of("A", 3.0, "B", 4.0);
        double sim = recommender.cosineSimilarity(v1, v2);
        assertEquals(1.0, sim, 0.001);
    }

    @Test @Order(12)
    @DisplayName("Cosine: Orthogonal vectors return similarity 0.0")
    void testCosineOrthogonal() {
        Map<String, Double> v1 = Map.of("A", 1.0);
        Map<String, Double> v2 = Map.of("B", 1.0);
        double sim = recommender.cosineSimilarity(v1, v2);
        assertEquals(0.0, sim, 0.001);
    }

    @Test @Order(13)
    @DisplayName("Cosine: Empty vectors return 0.0 safely")
    void testCosineEmpty() {
        double sim = recommender.cosineSimilarity(new HashMap<>(), Map.of("A", 1.0));
        assertEquals(0.0, sim);
    }

    @Test @Order(14)
    @DisplayName("Trie: Prefix search returns correct ISBNs")
    void testTriePrefixSearch() {
        trie.insertTitle("ಕನ್ನಡ ಸಾಹಿತ್ಯ", "978-K001");
        trie.insertTitle("ಕನ್ನಡ ಕಾವ್ಯ", "978-K002");
        trie.insertTitle("ವಿಜ್ಞಾನ", "978-V001");

        List<String> results = trie.searchPrefix("ಕನ್ನಡ");
        assertEquals(2, results.size());
        assertTrue(results.contains("978-K001"));
        assertTrue(results.contains("978-K002"));
    }

    @Test @Order(15)
    @DisplayName("Trie: Missing prefix returns empty list")
    void testTrieMissingPrefix() {
        trie.insertTitle("ಕನ್ನಡ", "978-K001");
        List<String> results = trie.searchPrefix("xxxxxx");
        assertTrue(results.isEmpty());
    }

    @Test @Order(16)
    @DisplayName("Levenshtein: Distance between same strings is 0")
    void testLevenshteinZero() {
        assertEquals(0, trie.levenshteinDistance("hello", "hello"));
    }

    @Test @Order(17)
    @DisplayName("Levenshtein: Distance kitten→sitting is 3")
    void testLevenshteinClassic() {
        assertEquals(3, trie.levenshteinDistance("kitten", "sitting"));
    }

    @Test @Order(18)
    @DisplayName("Levenshtein: Empty string distance equals other length")
    void testLevenshteinEmpty() {
        assertEquals(5, trie.levenshteinDistance("", "hello"));
        assertEquals(5, trie.levenshteinDistance("hello", ""));
    }

    @Test @Order(19)
    @DisplayName("Transliterator: 'kannada' maps to Kannada Unicode")
    void testTransliteration() {
        String result = trie.transliterateToKannada("kannada");
        assertEquals("ಕನ್ನಡ", result);
    }

    @Test @Order(20)
    @DisplayName("BFS: Path from A1 to A2 is length 2")
    void testBFSAdjacentSections() {
        List<String> path = library.bfsShelfPath("A1", "A2");
        assertEquals(2, path.size());
        assertEquals("A1", path.get(0));
        assertEquals("A2", path.get(1));
    }

    @Test @Order(21)
    @DisplayName("BFS: Invalid sections return empty path")
    void testBFSInvalid() {
        List<String> path = library.bfsShelfPath("Z99", "A1");
        assertTrue(path.isEmpty());
    }

    @Test @Order(22)
    @DisplayName("Transaction: Fine calculated correctly for overdue book")
    void testFineCalculation() {
        Transaction txn = new Transaction("TXN-001", "U001", "978-0001",
                LocalDate.now().minusDays(20));
        txn.setReturnDate(LocalDate.now());
        txn.setStatus(Transaction.Status.RETURNED);
        double fine = txn.calculateFine();

        assertEquals(12.0, fine, 0.001);
    }

    @Test @Order(23)
    @DisplayName("Transaction: No fine if returned on time")
    void testNoFine() {
        Transaction txn = new Transaction("TXN-002", "U001", "978-0001", LocalDate.now());
        txn.setReturnDate(LocalDate.now().plusDays(10));
        txn.setStatus(Transaction.Status.RETURNED);
        assertEquals(0.0, txn.calculateFine());
    }

    @Test @Order(24)
    @DisplayName("LinkedList: Recent users tracked after borrow")
    void testRecentUsersTracked() {
        Book book = new Book("978-0001", "Book", "A", "CS", "English", "A1", 5);
        library.addBook(book);
        User user = new User("U001", "Aarav", "a@b.com", User.Role.STUDENT, User.Department.CSE, 3);
        library.registerUser(user);

        library.issueBook("U001", "978-0001");
        assertEquals("U001", library.getRecentUsers().getFirst().getUserId());
    }

    @Test @Order(25)
    @DisplayName("StudyMatcher: Compatibility is between 0.0 and 1.0")
    void testCompatibilityRange() {
        User a = new User("U001", "A", "a@b.com", User.Role.STUDENT, User.Department.CSE, 3);
        User b = new User("U002", "B", "b@b.com", User.Role.STUDENT, User.Department.CSE, 3);
        a.setStudySubjects(Arrays.asList("Algorithms", "Java"));
        b.setStudySubjects(Arrays.asList("Algorithms", "Python"));

        double score = studyMatcher.computeCompatibility(a, b);
        assertTrue(score >= 0.0 && score <= 1.0);
    }

    @Test @Order(26)
    @DisplayName("Budget Optimizer: Never exceeds budget")
    void testBudgetNotExceeded() {
        for (int i = 0; i < 10; i++) {
            Book b = new Book("978-000" + i, "Book " + i, "A", "CS", "English", "A1", 2);
            b.setTimesIssued(i * 5);
            b.setRating(3.0 + i * 0.1);
            library.addBook(b);
        }
        double budget = 1500;
        List<Book> selected = library.optimizeBudgetBooks(budget);
        double total = selected.stream().mapToDouble(b -> 200 + b.getTimesIssued() * 10.0).sum();
        assertTrue(total <= budget, "Total cost " + total + " exceeds budget " + budget);
    }
}
