package com.smartcampus.models;

import java.util.ArrayList;
import java.util.List;

public class StudyGroup {

    private String groupId;
    private String subject;
    private List<User> members;
    private int maxSize;
    private double compatibilityScore;

    public StudyGroup(String groupId, String subject, int maxSize) {
        this.groupId = groupId;
        this.subject = subject;
        this.maxSize = maxSize;
        this.members = new ArrayList<>();
        this.compatibilityScore = 0.0;
    }

    public void addMember(User user) {
        if (members.size() < maxSize) {
            members.add(user);
        }
    }

    public boolean isFull() { return members.size() >= maxSize; }

    public String getGroupId() { return groupId; }
    public String getSubject() { return subject; }
    public List<User> getMembers() { return members; }
    public int getMaxSize() { return maxSize; }
    public double getCompatibilityScore() { return compatibilityScore; }
    public void setCompatibilityScore(double score) { this.compatibilityScore = score; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("StudyGroup[%s | Subject: %s | Members: %d/%d | Score: %.0f%%]\n",
                groupId, subject, members.size(), maxSize, compatibilityScore * 100));
        for (User u : members) {
            sb.append("  - ").append(u.getName()).append(" (").append(u.getDepartment()).append(")\n");
        }
        return sb.toString();
    }
}
