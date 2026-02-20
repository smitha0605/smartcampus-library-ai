# 📚 Stacked Shelves
**Console-Based | Java 17 | 12 DSA Structures**

> A production-grade library management system built to demonstrate mastery of Data Structures & Algorithms. Features AI-powered recommendations, Kannada language support, graph-based study group matching, and real-time analytics — all implemented from scratch in core Java.



## 🎯Highlights

| Feature | Algorithm | DSA Structure | Complexity |
|---|---|---|---|
| Book Recommendations | Collaborative Filtering (Cosine Similarity) | Sparse HashMap rating matrix | O(u × b) |
| Peak Hour Prediction | Time-series scoring | PriorityQueue (max-heap) | O(h log h) |
| Theft Heatmap | 2D Spatial Analysis | int\[10\]\[10\] | O(1) per incident |
| Study Group Matching | Greedy Bipartite-inspired | Adjacency List Graph | O(V² + E log E) |
| Kannada Book Search | Prefix + Fuzzy | Trie + Levenshtein DP | O(L) / O(m×n) |
| QR Shelf Navigation | BFS Pathfinding | Queue on grid graph | O(V + E) |
| Budget Optimizer | Fractional Knapsack-inspired Greedy | Sorted ArrayList | O(n log n) |
| Carbon Footprint | Weighted scoring + memoization | HashMap cache | O(1) cached |


## 📊 All 12 DSA Structures

```
#1  HashMap<String, Book>         — O(1) ISBN lookup
#2  ArrayList<Book>               — Dynamic catalog
#3  LinkedList<User>              — FIFO recent users (O(1) add/remove)
#4  Queue<Transaction>            — Waitlist (FIFO)
#5  Stack<Transaction>            — Borrow history (LIFO)
#6  PriorityQueue<Book/TimeSlot>  — Demand & time-slot ranking
#7  HashMap<String, User>         — O(1) user lookup
#8  int[10][10]                   — 2D theft heatmap (spatial analysis)
#9  HashMap<isbn, rating> sparse  — User-book cosine similarity matrix
#10 Adjacency List (Map + List)   — Study group compatibility graph
#11 TrieNode (HashMap children)   — O(L) Kannada prefix search
#12 int[][] DP table              — Levenshtein edit distance
```

## 🛠️ Project Structure

```
stacked-shelves/
├── src/main/java/com/smartcampus/
│   ├── models/
│   │   ├── Book.java           — Core entity (Comparable for PriorityQueue)
│   │   ├── User.java           — Sparse rating map for collab filtering
│   │   ├── Transaction.java    — Fine calculation, status tracking
│   │   └── StudyGroup.java     — Output of graph matching
│   ├── core/
│   │   ├── SmartLibrary.java   — Main DSA engine (8 structures)
│   │   ├── Recommender.java    — Cosine similarity collaborative filtering
│   │   ├── DemandForecaster.java — PriorityQueue peak prediction
│   │   ├── TheftAnalytics.java — 2D heatmap analysis
│   │   ├── StudyMatcher.java   — Graph bipartite matching
│   │   └── CarbonTracker.java  — Weighted scoring + memoization
│   ├── utils/
│   │   ├── FileManager.java    — Gson JSON persistence
│   │   ├── KannadaTransliterator.java — Trie + Levenshtein
│   │   └── QRNavigator.java    — BFS visualization
│   └── SmartCampusLibrary.java — Main menu & feature handlers
├── src/test/java/
│   └── SmartCampusLibraryTest.java — 26 JUnit 5 tests
├── .github/workflows/ci.yml   — GitHub Actions CI/CD
├── pom.xml                    — Maven build
├── run.sh                     — One-click run script
└── README.md
```


## 🚀 Quick Start

**Prerequisites:** Java 17+, Maven 3.8+

```bash
# Clone
git clone https://github.com/smitha0605/stacked-shelves.git
cd stacked-shelves

# Run all tests
mvn test

# Build & run
chmod +x run.sh && ./run.sh

# Or directly
mvn clean package -q
java -jar target/stacked-shelves.jar
```

## 📱 Menu Overview

```
  1️⃣   AI Book Recommendations (Cosine Similarity)
  2️⃣   Predict Peak Hours (PriorityQueue time-series)
  3️⃣   Theft Heatmap (2D Array + ASCII visualization)
  4️⃣   Find Study Buddies (Graph Bipartite Matching)
  5️⃣   Carbon Footprint Scanner (Weighted + Memoization)
  6️⃣   Kannada Book Search (Trie + Levenshtein Fuzzy)
  7️⃣   Exam Rush Auto-Reserve (PriorityQueue)
  8️⃣   Budget Book Optimizer (Greedy Knapsack)
  9️⃣   Reading Streak Leaderboard (Comparator sort)
  0️⃣   QR Shelf Navigator (BFS on grid graph)
  S    Search Books (HashMap O(1) / ArrayList O(n))
  I    Issue Book (Queue + Stack)
  R    Return Book (Stack search + Queue dequeue)
  D    DSA Complexity Reference
```



## 🧪 Testing

26 JUnit 5 tests covering all DSA structures and edge cases:

```bash
mvn test
```

Test categories:
- HashMap O(1) correctness & duplicate handling
- ArrayList dynamic growth & linear search
- Queue/Stack FIFO/LIFO borrow & waitlist behavior
- PriorityQueue top-demand ordering
- 2D array spatial indexing & bounds checking
- Cosine similarity (identical, orthogonal, empty vectors)
- Trie prefix search & missing prefix
- Levenshtein distance (classic cases, empty strings)
- Transliteration Latin → Kannada Unicode
- BFS path correctness & invalid input
- Fine calculation (overdue, on-time)
- Budget optimizer budget constraint satisfaction
- Study matcher compatibility score range



## 🌟 SmartCampus Campus Features

- **Kannada Language Support** — Trie-indexed Kannada book titles with Latin phonetic transliteration (e.g., type "kannada" → finds ಕನ್ನಡ books)
- **Exam Rush Prediction** — SmartCampus semester pattern calendar (Apr/May, Nov/Dec peaks)
- **Library Section Grid** — A1–J10 shelf layout matching SmartCampus library zones
- 

---

## 📈 Big-O Summary

| Operation | Time | Space |
|---|---|---|
| ISBN Lookup | O(1) avg | O(n) |
| Add Book | O(1) | O(1) |
| Title Search | O(n) | O(k) |
| Recommendations | O(u × b) | O(nm) |
| Peak Prediction | O(h log h) | O(h) |
| Theft Heatmap Update | O(1) | O(100) fixed |
| Study Group Build | O(V²) | O(V²) |
| Kannada Trie Search | O(L) | O(Σ × L × N) |
| Levenshtein Fuzzy | O(m × n) | O(m × n) |
| BFS Navigation | O(V + E) | O(V) |
| Budget Greedy | O(n log n) | O(n) |
| Leaderboard Sort | O(u log u) | O(u) |



**Tech Stack:** Core Java 17 | Maven | JUnit 5 | Gson | GitHub Actions

Designed and implemented using Java with a focus on real-world DSA applications.
