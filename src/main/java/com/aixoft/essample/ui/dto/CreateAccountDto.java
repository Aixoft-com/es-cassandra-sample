package com.aixoft.essample.ui.dto;

import lombok.Value;

@Value
public class CreateAccountDto {
    String userName;
    String email;
    int loyalPoints;
}
