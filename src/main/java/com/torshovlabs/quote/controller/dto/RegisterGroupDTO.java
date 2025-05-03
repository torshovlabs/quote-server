package com.torshovlabs.quote.controller.dto;

import com.torshovlabs.quote.transport.TUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterGroupDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 15, message = "Username must be between 3 and 50 characters")
    private String groupName;

    @NotNull
    private TUser user;

}
