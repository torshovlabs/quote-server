package com.torshovlabs.quote.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quote")
public class Quote {

    @jakarta.persistence.Id
    String quote;


    String author;
}
