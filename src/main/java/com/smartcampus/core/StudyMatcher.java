package com.smartcampus.core;

import com.smartcampus.models.StudyGroup;
import com.smartcampus.models.User;

import java.util.*;

public class StudyMatcher {

    
    private final Map<String, List<int[]>> adjacencyList;

    
    private final List<User> userIndex;

    public StudyMatcher() {
        this.adjacencyList = new HashMap<>();
        this.userIndex     = new ArrayList<>();
    }

    
    public void buildGraph(List<User> users) {
        userIndex.clear();
        userIndex.addAll(users);
        adjacencyList.clear();

        for (int i = 0; i < users.size(); i++) {
            String uid = users.get(i).getUserId();
            adjacencyList.put(uid, new ArrayList<>());
        }

        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                double score = computeCompatibility(users.get(i), users.get(j));
                if (score > 0.3) {
                    int scoreInt = (int)(score * 100);
                    adjacencyList.get(users.get(i).getUserId())
                            .add(new int[]{j, scoreInt});
                    adjacencyList.get(users.get(j).getUserId())
                            .add(new int[]{i, scoreInt});
                }
            }
        }
    }

    
    public double computeCompatibility(User a, User b) {
        double score = 0.0;

        Set<String> subjectsA = new HashSet<>(a.getStudySubjects());
        Set<String> subjectsB = new HashSet<>(b.getStudySubjects());

        Set<String> intersection = new HashSet<>(subjectsA);
        intersection.retainAll(subjectsB);

        Set<String> union = new HashSet<>(subjectsA);
        union.addAll(subjectsB);

        if (!union.isEmpty()) {
            score += 0.5 * (double) intersection.size() / union.size();
        }

        if (a.getSemester() == b.getSemester()) score += 0.2;

        if (a.getDepartment() == b.getDepartment()) score += 0.2;

        boolean aIsTech = a.getDepartment() == User.Department.CSE
                       || a.getDepartment() == User.Department.MTECH_AI;
        boolean bIsTech = b.getDepartment() == User.Department.CSE
                       || b.getDepartment() == User.Department.MTECH_AI;
        if (aIsTech && bIsTech) score += 0.1;

        return Math.min(score, 1.0);
    }

    
    public List<StudyGroup> matchStudyGroups(int groupSize) {
        if (userIndex.isEmpty()) return Collections.emptyList();

        PriorityQueue<int[]> edgeHeap = new PriorityQueue<>(
                (a, b) -> Integer.compare(b[0], a[0])
        );

        for (int i = 0; i < userIndex.size(); i++) {
            for (int[] edge : adjacencyList.getOrDefault(userIndex.get(i).getUserId(), List.of())) {
                if (edge[0] > i) {
                    edgeHeap.offer(new int[]{edge[1], i, edge[0]});
                }
            }
        }

        boolean[] assigned     = new boolean[userIndex.size()];
        List<StudyGroup> groups = new ArrayList<>();
        int groupCounter        = 1;

        while (!edgeHeap.isEmpty()) {
            int[] edge = edgeHeap.poll();
            int idxA = edge[1], idxB = edge[2];

            if (!assigned[idxA] || !assigned[idxB]) {

                StudyGroup group = findOrCreateGroup(groups, userIndex.get(idxA), groupSize, groupCounter);
                if (group.getMembers().isEmpty()) groupCounter++;

                if (!assigned[idxA]) {
                    group.addMember(userIndex.get(idxA));
                    assigned[idxA] = true;
                }
                if (!assigned[idxB] && !group.isFull()) {
                    group.addMember(userIndex.get(idxB));
                    assigned[idxB] = true;
                }

                double avgScore = edge[0] / 100.0;
                group.setCompatibilityScore(
                        (group.getCompatibilityScore() + avgScore) / 2.0
                );
            }
        }

        for (int i = 0; i < userIndex.size(); i++) {
            if (!assigned[i]) {
                String subject = userIndex.get(i).getStudySubjects().isEmpty()
                        ? "General" : userIndex.get(i).getStudySubjects().get(0);
                StudyGroup solo = new StudyGroup("G" + groupCounter++, subject, groupSize);
                solo.addMember(userIndex.get(i));
                solo.setCompatibilityScore(0.5);
                groups.add(solo);
            }
        }

        return groups;
    }

    private StudyGroup findOrCreateGroup(List<StudyGroup> groups, User user, int maxSize, int id) {

        for (StudyGroup g : groups) {
            if (!g.isFull()) return g;
        }

        String subject = user.getStudySubjects().isEmpty() ? "General"
                : user.getStudySubjects().get(0);
        StudyGroup newGroup = new StudyGroup("G" + id, subject, maxSize);
        groups.add(newGroup);
        return newGroup;
    }

    
    public void printGraph() {
        System.out.println("\n   Study Compatibility Graph (Adjacency List):");
        System.out.println("  " + "-".repeat(50));
        for (int i = 0; i < userIndex.size(); i++) {
            User u = userIndex.get(i);
            List<int[]> edges = adjacencyList.getOrDefault(u.getUserId(), List.of());
            System.out.printf("  %s (%s)", u.getName(), u.getDepartment());
            if (edges.isEmpty()) {
                System.out.println(" → [no compatible peers]");
            } else {
                System.out.print(" → ");
                for (int[] e : edges) {
                    System.out.printf("%s(%.0f%%) ", userIndex.get(e[0]).getName(), e[1] * 1.0);
                }
                System.out.println();
            }
        }
    }
}
