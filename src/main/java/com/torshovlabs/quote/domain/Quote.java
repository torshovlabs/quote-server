package com.torshovlabs.quote.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quote")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    // Add a simple constructor with essential fields
    public Quote(String content, User author, Group group) {
        this.content = content;
        this.author = author;
        this.group = group;
        this.publishDate = LocalDate.now();
    }

    // Add equals and hashCode methods based on the ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quote quote = (Quote) o;

        return id != null && id.equals(quote.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", author=" + (author != null ? author.getId() : null) +
                ", group=" + (group != null ? group.getId() : null) +
                ", publishDate=" + publishDate +
                '}';
    }
}