package com.torshovlabs.quote.transport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TUser {
    private String username;

    public TUser(String username) {
        this.username = username;
    }
}