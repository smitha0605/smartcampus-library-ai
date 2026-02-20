package com.smartcampus.utils;

import com.smartcampus.models.Book;

import java.util.*;

/**
 * HindiTransliterator — mirrors KannadaTransliterator for Hindi (Devanagari).
 *
 * DSA Structures used:
 *   #11 TrieNode (HashMap children) — O(L) Hindi prefix search
 *   #12 int[][] DP table            — Levenshtein edit distance for fuzzy match
 *
 * How it works:
 *   1. Latin phonetic input (e.g. "vigyan") is mapped to Devanagari (विज्ञान)
 *      via a static transliteration map.
 *   2. The transliterated string is looked up in a Trie built from all Hindi
 *      book titles at startup.
 *   3. If Trie returns no results, Levenshtein fuzzy matching scans the catalog.
 */
public class HindiTransliterator {

    // ── Trie ─────────────────────────────────────────────────────────────────

    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
        List<String> isbns = new ArrayList<>();
    }

    private final TrieNode root;

    // ── Transliteration Map (Latin phonetic → Devanagari) ────────────────────

    private static final Map<String, String> TRANSLITERATION_MAP = new LinkedHashMap<>();

    static {
        // Vowels
        TRANSLITERATION_MAP.put("aa", "आ");   TRANSLITERATION_MAP.put("a",  "अ");
        TRANSLITERATION_MAP.put("ii", "ई");   TRANSLITERATION_MAP.put("i",  "इ");
        TRANSLITERATION_MAP.put("uu", "ऊ");   TRANSLITERATION_MAP.put("u",  "उ");
        TRANSLITERATION_MAP.put("e",  "ए");   TRANSLITERATION_MAP.put("o",  "ओ");
        TRANSLITERATION_MAP.put("ai", "ऐ");   TRANSLITERATION_MAP.put("au", "औ");

        // Consonants
        TRANSLITERATION_MAP.put("ka",  "क");  TRANSLITERATION_MAP.put("kha", "ख");
        TRANSLITERATION_MAP.put("ga",  "ग");  TRANSLITERATION_MAP.put("gha", "घ");
        TRANSLITERATION_MAP.put("cha", "च");  TRANSLITERATION_MAP.put("chha","छ");
        TRANSLITERATION_MAP.put("ja",  "ज");  TRANSLITERATION_MAP.put("jha", "झ");
        TRANSLITERATION_MAP.put("ta",  "त");  TRANSLITERATION_MAP.put("tha", "थ");
        TRANSLITERATION_MAP.put("da",  "द");  TRANSLITERATION_MAP.put("dha", "ध");
        TRANSLITERATION_MAP.put("na",  "न");  TRANSLITERATION_MAP.put("pa",  "प");
        TRANSLITERATION_MAP.put("pha", "फ");  TRANSLITERATION_MAP.put("ba",  "ब");
        TRANSLITERATION_MAP.put("bha", "भ");  TRANSLITERATION_MAP.put("ma",  "म");
        TRANSLITERATION_MAP.put("ya",  "य");  TRANSLITERATION_MAP.put("ra",  "र");
        TRANSLITERATION_MAP.put("la",  "ल");  TRANSLITERATION_MAP.put("va",  "व");
        TRANSLITERATION_MAP.put("sha", "श");  TRANSLITERATION_MAP.put("sa",  "स");
        TRANSLITERATION_MAP.put("ha",  "ह");

        // Common Hindi words (whole-word shortcuts)
        TRANSLITERATION_MAP.put("hindi",    "हिंदी");
        TRANSLITERATION_MAP.put("sahitya",  "साहित्य");
        TRANSLITERATION_MAP.put("vigyan",   "विज्ञान");
        TRANSLITERATION_MAP.put("ganit",    "गणित");
        TRANSLITERATION_MAP.put("kavya",    "काव्य");
        TRANSLITERATION_MAP.put("itihas",   "इतिहास");
        TRANSLITERATION_MAP.put("kahani",   "कहानी");
        TRANSLITERATION_MAP.put("pustaka",  "पुस्तक");
        TRANSLITERATION_MAP.put("bhasha",   "भाषा");
        TRANSLITERATION_MAP.put("gyan",     "ज्ञान");
        TRANSLITERATION_MAP.put("dharma",   "धर्म");
        TRANSLITERATION_MAP.put("sanskriti","संस्कृति");
    }

    public HindiTransliterator() {
        this.root = new TrieNode();
    }

    // ── Trie Operations ───────────────────────────────────────────────────────

    public void insertTitle(String hindiTitle, String isbn) {
        if (hindiTitle == null || hindiTitle.isEmpty()) return;
        TrieNode cur = root;
        for (char c : hindiTitle.toCharArray()) {
            cur.children.putIfAbsent(c, new TrieNode());
            cur = cur.children.get(c);
        }
        cur.isEndOfWord = true;
        cur.isbns.add(isbn);
    }

    public List<String> searchPrefix(String prefix) {
        TrieNode cur = root;
        for (char c : prefix.toCharArray()) {
            cur = cur.children.get(c);
            if (cur == null) return Collections.emptyList();
        }
        List<String> results = new ArrayList<>();
        collectAllISBNs(cur, results);
        return results;
    }

    private void collectAllISBNs(TrieNode node, List<String> results) {
        if (node.isEndOfWord) results.addAll(node.isbns);
        for (TrieNode child : node.children.values()) {
            collectAllISBNs(child, results);
        }
    }

    // ── Transliteration ───────────────────────────────────────────────────────

    public String transliterateToHindi(String latinInput) {
        if (latinInput == null) return "";
        String lower = latinInput.toLowerCase().trim();

        if (TRANSLITERATION_MAP.containsKey(lower)) {
            return TRANSLITERATION_MAP.get(lower);
        }

        String[] words = lower.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(TRANSLITERATION_MAP.getOrDefault(word, word));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    // ── Levenshtein DP ────────────────────────────────────────────────────────

    public int levenshteinDistance(String s1, String s2) {
        int m = s1.length(), n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i-1][j-1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i-1][j-1],
                                   Math.min(dp[i-1][j], dp[i][j-1]));
                }
            }
        }
        return dp[m][n];
    }

    // ── Main Search ───────────────────────────────────────────────────────────

    public List<Book> searchHindi(String query, List<Book> allBooks, int maxDist) {
        if (query == null || query.isEmpty()) return Collections.emptyList();

        String hindiQuery = transliterateToHindi(query);
        System.out.printf("  [HINDI] Input: \"%s\" → Transliterated: \"%s\"%n", query, hindiQuery);

        List<String> trieISBNs = searchPrefix(hindiQuery);
        if (trieISBNs.isEmpty()) {
            trieISBNs = searchPrefix(query);
        }

        List<Book> results = new ArrayList<>();
        Set<String> addedISBNs = new HashSet<>(trieISBNs);

        for (Book book : allBooks) {
            if (trieISBNs.contains(book.getIsbn())) {
                results.add(book);
                continue;
            }

            String hindiTitle = book.getHindiTitle();
            if (hindiTitle != null && !hindiTitle.isEmpty()) {
                int dist = levenshteinDistance(hindiQuery, hindiTitle);
                if (dist <= maxDist && !addedISBNs.contains(book.getIsbn())) {
                    results.add(book);
                    addedISBNs.add(book.getIsbn());
                }
            }

            if (book.getLanguage() != null && book.getLanguage().equalsIgnoreCase("Hindi")) {
                int dist = levenshteinDistance(query.toLowerCase(), book.getTitle().toLowerCase());
                if (dist <= maxDist && !addedISBNs.contains(book.getIsbn())) {
                    results.add(book);
                    addedISBNs.add(book.getIsbn());
                }
            }
        }

        return results;
    }

    // ── Index Builder ─────────────────────────────────────────────────────────

    public void indexBooks(List<Book> books) {
        for (Book b : books) {
            if (b.getHindiTitle() != null && !b.getHindiTitle().isEmpty()) {
                insertTitle(b.getHindiTitle(), b.getIsbn());
            }
        }
        System.out.println("  [TRIE] Indexed " + books.stream()
                .filter(b -> b.getHindiTitle() != null && !b.getHindiTitle().isEmpty())
                .count() + " Hindi titles");
    }
}
