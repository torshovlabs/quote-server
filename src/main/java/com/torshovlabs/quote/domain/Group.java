package com.torshovlabs.quote.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
<<<<<<< HEAD

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
=======
import java.util.Set;
>>>>>>> 4bc6e0ccffbc7441bfb742572c4695b64089aa6d

@Getter
@Setter
@NoArgsConstructor
@Entity
<<<<<<< HEAD
@Table(name = "user_group") // Changed from "group" to avoid MySQL reserved keyword
public class Group {

    @Id
    private String id;
=======
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
>>>>>>> 4bc6e0ccffbc7441bfb742572c4695b64089aa6d

    @Column(nullable = false)
    private String name;

<<<<<<< HEAD
    @ManyToMany
    @JoinTable(
            name = "user_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "current_publisher_id")
    private User currentPublisher;

    @Column(name = "last_rotation_date")
    private LocalDate lastRotationDate;

    // Constructor with name
    public Group(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    // Helper method to add a member to the group
    public void addMember(User user) {
        this.members.add(user);
        user.getGroups().add(this);
    }

    // Helper method to remove a member from the group
    public void removeMember(User user) {
        this.members.remove(user);
        user.getGroups().remove(this);
    }

    // Method to rotate queue and update current publisher
    public void rotatePublisherQueue() {
        // Logic to determine the next publisher based on your queue algorithm
        if (members != null && !members.isEmpty()) {
            // Simple rotation logic - you can replace with your queue-based approach
            Set<User> eligibleUsers = new HashSet<>(members);
            eligibleUsers.remove(currentPublisher);

            if (!eligibleUsers.isEmpty()) {
                // For simplicity, pick the first eligible user
                // In a real implementation, you'd implement proper queue logic
                currentPublisher = eligibleUsers.iterator().next();
            }
        }
        lastRotationDate = LocalDate.now();
    }

    // Pre-persist hook to set ID if not already set
    @PrePersist
    public void ensureId() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        return id != null && id.equals(group.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", memberCount=" + (members != null ? members.size() : 0) +
                ", currentPublisher=" + (currentPublisher != null ? currentPublisher.getId() : null) +
                ", lastRotationDate=" + lastRotationDate +
                '}';
    }
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GroupMembership> groupMemberships;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Quote> quotes;

>>>>>>> 4bc6e0ccffbc7441bfb742572c4695b64089aa6d
}