package com.smartcampus.utils;

import com.smartcampus.models.Book;

import java.util.*;

public class KannadaTransliterator {

    private static class TrieNode {

        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
        List<String> isbns  = new ArrayList<>();
    }

    private final TrieNode root;

    
    private static final Map<String, String> TRANSLITERATION_MAP = new LinkedHashMap<>();

    static {

        TRANSLITERATION_MAP.put("aa", "ಆ");  TRANSLITERATION_MAP.put("a", "ಅ");
        TRANSLITERATION_MAP.put("ee", "ಈ");  TRANSLITERATION_MAP.put("i", "ಇ");
        TRANSLITERATION_MAP.put("oo", "ಊ");  TRANSLITERATION_MAP.put("u", "ಉ");
        TRANSLITERATION_MAP.put("e", "ಎ");   TRANSLITERATION_MAP.put("o", "ಒ");

        TRANSLITERATION_MAP.put("ka", "ಕ");  TRANSLITERATION_MAP.put("ga", "ಗ");
        TRANSLITERATION_MAP.put("cha", "ಚ"); TRANSLITERATION_MAP.put("ja", "ಜ");
        TRANSLITERATION_MAP.put("ta", "ತ");  TRANSLITERATION_MAP.put("da", "ದ");
        TRANSLITERATION_MAP.put("na", "ನ");  TRANSLITERATION_MAP.put("pa", "ಪ");
        TRANSLITERATION_MAP.put("ba", "ಬ");  TRANSLITERATION_MAP.put("ma", "ಮ");
        TRANSLITERATION_MAP.put("ya", "ಯ");  TRANSLITERATION_MAP.put("ra", "ರ");
        TRANSLITERATION_MAP.put("la", "ಲ");  TRANSLITERATION_MAP.put("va", "ವ");
        TRANSLITERATION_MAP.put("sha", "ಶ"); TRANSLITERATION_MAP.put("sa", "ಸ");
        TRANSLITERATION_MAP.put("ha", "ಹ");  TRANSLITERATION_MAP.put("lla", "ಳ");
        TRANSLITERATION_MAP.put("ksha", "ಕ್ಷ"); TRANSLITERATION_MAP.put("jna", "ಜ್ಞ");

        TRANSLITERATION_MAP.put("kannada", "ಕನ್ನಡ");
        TRANSLITERATION_MAP.put("sahitya", "ಸಾಹಿತ್ಯ");
        TRANSLITERATION_MAP.put("pustaka", "ಪುಸ್ತಕ");
        TRANSLITERATION_MAP.put("vijnana", "ವಿಜ್ಞಾನ");
        TRANSLITERATION_MAP.put("ganita", "ಗಣಿತ");
        TRANSLITERATION_MAP.put("kavya", "ಕಾವ್ಯ");
        TRANSLITERATION_MAP.put("itihaasa", "ಇತಿಹಾಸ");
        TRANSLITERATION_MAP.put("vigyana", "ವಿಜ್ಞಾನ");
    }

    public KannadaTransliterator() {
        this.root = new TrieNode();
    }

    
    public void insertTitle(String kannadaTitle, String isbn) {
        if (kannadaTitle == null || kannadaTitle.isEmpty()) return;
        TrieNode cur = root;
        for (char c : kannadaTitle.toCharArray()) {
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

    
    public String transliterateToKannada(String latinInput) {
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
                                   Math.min(dp[i-1][j],
                                            dp[i][j-1]));
                }
            }
        }
        return dp[m][n];
    }

    
    public List<Book> searchKannada(String query, List<Book> allBooks, int maxDist) {
        if (query == null || query.isEmpty()) return Collections.emptyList();

        String kannadaQuery = transliterateToKannada(query);
        System.out.printf("  [KANNADA] Input: \"%s\" → Transliterated: \"%s\"%n", query, kannadaQuery);

        List<String> trieISBNs = searchPrefix(kannadaQuery);
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

            String kannadaTitle = book.getKannadaTitle();
            if (kannadaTitle != null && !kannadaTitle.isEmpty()) {
                int dist = levenshteinDistance(kannadaQuery, kannadaTitle);
                if (dist <= maxDist && !addedISBNs.contains(book.getIsbn())) {
                    results.add(book);
                    addedISBNs.add(book.getIsbn());
                }
            }

            if (book.getLanguage() != null && book.getLanguage().equalsIgnoreCase("Kannada")) {
                int dist = levenshteinDistance(query.toLowerCase(), book.getTitle().toLowerCase());
                if (dist <= maxDist && !addedISBNs.contains(book.getIsbn())) {
                    results.add(book);
                    addedISBNs.add(book.getIsbn());
                }
            }
        }

        return results;
    }

    
    public void indexBooks(List<Book> books) {
        for (Book b : books) {
            if (b.getKannadaTitle() != null && !b.getKannadaTitle().isEmpty()) {
                insertTitle(b.getKannadaTitle(), b.getIsbn());
            }
        }
        System.out.println("  [TRIE] Indexed " + books.stream()
                .filter(b -> b.getKannadaTitle() != null && !b.getKannadaTitle().isEmpty())
                .count() + " Kannada titles");
    }
}
