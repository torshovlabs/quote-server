package com.torshovlabs.quote.controller.dto;

import com.torshovlabs.quote.transport.TUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuoteDTO {

    @NotBlank(message = "Quote text is required")
    private String quoteText;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "User is required")
    private TUser user;

    @NotNull(message = "Group ID is required")
    private Long groupId;
}