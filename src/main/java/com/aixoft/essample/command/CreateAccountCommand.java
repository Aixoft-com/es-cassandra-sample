package com.aixoft.essample.command;

import lombok.Value;

@Value
public class CreateAccountCommand {
    private String userName;
    private String email;
}
