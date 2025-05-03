package com.torshovlabs.quote.transport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TUser {
    private String username;
    private String deviceIdentifier; // Unique device identifier
}
