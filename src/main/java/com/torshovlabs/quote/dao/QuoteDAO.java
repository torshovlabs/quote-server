package com.torshovlabs.quote.dao;

import com.torshovlabs.quote.domain.Quote;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface QuoteDAO {

    Quote save(Quote quote);

    Optional<Quote> findById(Long id);

    List<Quote> findByUser(String userId);

    List<Quote> findByGroup(Long groupId);

    List<Quote> findByUserAndGroup(String userId, Long groupId);

    void deleteById(Long id);

    boolean existsByUserAndGroupAndCreationDateAfter(String userId, Long groupId, java.time.LocalDateTime date);

    LocalDateTime findMostRecentQuoteTimeForGroup(Long groupId);
}