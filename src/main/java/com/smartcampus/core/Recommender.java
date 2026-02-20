package com.smartcampus.core;

import com.smartcampus.models.Book;
import com.smartcampus.models.User;

import java.util.*;

public class Recommender {

    private final SmartLibrary library;

    public Recommender(SmartLibrary library) {
        this.library = library;
    }

    
    public List<Book> getRecommendations(User targetUser, int topN) {
        Map<String, Double> targetRatings = targetUser.getBookRatings();
        Set<String> alreadyRead = new HashSet<>(targetUser.getBorrowHistory());

        Map<String, Double> scoreMap = new HashMap<>();
        Map<String, Double> simSum   = new HashMap<>();

        Collection<User> allUsers = library.getUsersByID().values();

        for (User other : allUsers) {
            if (other.getUserId().equals(targetUser.getUserId())) continue;

            double sim = cosineSimilarity(targetRatings, other.getBookRatings());
            if (sim <= 0.0) continue;

            for (Map.Entry<String, Double> entry : other.getBookRatings().entrySet()) {
                String isbn  = entry.getKey();
                double rating = entry.getValue();
                if (!alreadyRead.contains(isbn)) {
                    scoreMap.merge(isbn, sim * rating, Double::sum);
                    simSum.merge(isbn, sim, Double::sum);
                }
            }
        }

        Map<String, Double> normalizedScores = new HashMap<>();
        for (String isbn : scoreMap.keySet()) {
            normalizedScores.put(isbn, scoreMap.get(isbn) / simSum.get(isbn));
        }

        List<Map.Entry<String, Double>> ranked = new ArrayList<>(normalizedScores.entrySet());
        ranked.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Book> recommendations = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, ranked.size()); i++) {
            Book book = library.findByISBN(ranked.get(i).getKey());
            if (book != null) recommendations.add(book);
        }

        return recommendations;
    }

    
    public double cosineSimilarity(Map<String, Double> ratingsA, Map<String, Double> ratingsB) {
        if (ratingsA.isEmpty() || ratingsB.isEmpty()) return 0.0;

        Map<String, Double> smaller = ratingsA.size() <= ratingsB.size() ? ratingsA : ratingsB;
        Map<String, Double> larger  = smaller == ratingsA ? ratingsB : ratingsA;

        double dotProduct  = 0.0;
        double normA       = 0.0;
        double normB       = 0.0;

        for (Map.Entry<String, Double> entry : smaller.entrySet()) {
            Double ratingInLarger = larger.get(entry.getKey());
            if (ratingInLarger != null) {
                dotProduct += entry.getValue() * ratingInLarger;
            }
        }

        for (double r : ratingsA.values()) normA += r * r;
        for (double r : ratingsB.values()) normB += r * r;

        normA = Math.sqrt(normA);
        normB = Math.sqrt(normB);

        return (normA == 0.0 || normB == 0.0) ? 0.0 : dotProduct / (normA * normB);
    }

    
    public List<User> findSimilarUsers(User targetUser, int topN) {
        Map<String, Double> targetRatings = targetUser.getBookRatings();
        List<Map.Entry<User, Double>> simList = new ArrayList<>();

        for (User other : library.getUsersByID().values()) {
            if (other.getUserId().equals(targetUser.getUserId())) continue;
            double sim = cosineSimilarity(targetRatings, other.getBookRatings());
            simList.add(Map.entry(other, sim));
        }

        simList.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<User> result = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, simList.size()); i++) {
            result.add(simList.get(i).getKey());
        }
        return result;
    }
}
