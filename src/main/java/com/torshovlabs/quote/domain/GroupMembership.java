package com.torshovlabs.quote.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "group_membership",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id"}))
public class GroupMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "queue_number", nullable = false)
    private Integer queueNumber;

    @Column(name = "can_quote")
    private Boolean canQuote = false;

    @Column(name = "last_quote_time")
    private LocalDateTime lastQuoteTime;
}