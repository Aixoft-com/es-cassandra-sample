package com.aixoft.reactsample.command;

import lombok.Value;

@Value
public class CreateAccountCommand {
    private String userName;
    private String email;
}
