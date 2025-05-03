package com.torshovlabs.quote.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_user", indexes = {
        @Index(name = "app_user_phone_number_idx", columnList = "phone_number")
})
public class User {

    @Id
    @Column(length = 15)
    private String id;  // name as id here


    // Add relationship with Group
    @ManyToMany(mappedBy = "members")
    private Set<Group> groups = new HashSet<>();

    // Add relationship for quotes authored by this user
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Quote> authoredQuotes = new HashSet<>();

    // Add field for queue position in groups
    @ElementCollection
    @CollectionTable(
            name = "user_queue_positions",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @MapKeyJoinColumn(name = "group_id")
    @Column(name = "queue_position")
    private Map<Group, Integer> queuePositions = new HashMap<>();

    // Constructor
    public User(String id, String phoneNumber) {
        this.id = id;
    }

    // Helper method to check if user can publish quote today in a group
    public boolean canPublishQuote(Group group) {
        return group.getCurrentPublisher() != null &&
                group.getCurrentPublisher().getId().equals(this.id);
    }

    // Helper method to get queue position in a specific group
    public Integer getQueuePositionInGroup(Group group) {
        return queuePositions.getOrDefault(group, null);
    }

    // Helper method to set queue position in a specific group
    public void setQueuePositionInGroup(Group group, Integer position) {
        queuePositions.put(group, position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", groupCount=" + (groups != null ? groups.size() : 0) +
                '}';
    }
}