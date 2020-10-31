package com.aixoft.reactsample.ui.dto;

import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
public class CreateAccountDto {
    @NotEmpty
    String userName;
    @NotEmpty
    String email;
    int loyalPoints;
}
