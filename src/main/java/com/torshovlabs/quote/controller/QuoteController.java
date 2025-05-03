package com.torshovlabs.quote.controller;

import com.torshovlabs.quote.controller.dto.CreateQuoteDTO;
import com.torshovlabs.quote.domain.Quote;
import com.torshovlabs.quote.service.QuoteService;
import com.torshovlabs.quote.util.exceptions.GroupNotFoundException;
import com.torshovlabs.quote.util.exceptions.NotAllowedToPostException;
import com.torshovlabs.quote.util.exceptions.QuoteAlreadyExistsException;
import com.torshovlabs.quote.util.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/quote")
@RestController
public class QuoteController {

    private final QuoteService quoteService;

    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @RequestMapping(value = {"/create", "/create.json"}, method = POST)
    public ResponseEntity<?> createQuote(@Valid @RequestBody CreateQuoteDTO createQuoteDTO) {
        try {
            Quote quote = quoteService.createQuote(
                    createQuoteDTO.getQuoteText(),
                    createQuoteDTO.getAuthor(),
                    createQuoteDTO.getUser().getUsername(),
                    createQuoteDTO.getGroupId()
            );

            return ResponseEntity.ok("Quote created successfully: \"" +
                    (quote.getQuote().length() > 30 ?
                            quote.getQuote().substring(0, 30) + "..." :
                            quote.getQuote()) +
                    "\" by " + quote.getAuthor());
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (QuoteAlreadyExistsException | NotAllowedToPostException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/group/{groupId}", method = GET)
    public ResponseEntity<?> getQuotesForGroup(@PathVariable Long groupId, @RequestParam String username) {
        try {
            List<Quote> quotes = quoteService.getQuotesForGroup(username, groupId);

            // Transform to a simplified format for the response
            List<Map<String, Object>> result = quotes.stream().map(q -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", q.getId());
                map.put("quote", q.getQuote());
                map.put("author", q.getAuthor());
                map.put("postedBy", q.getUser().getName());
                map.put("postedAt", q.getCreationDate());
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (NotAllowedToPostException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}