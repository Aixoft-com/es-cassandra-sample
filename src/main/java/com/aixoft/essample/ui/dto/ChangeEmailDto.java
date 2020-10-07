package com.aixoft.essample.ui.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class ChangeEmailDto {
    UUID accountInformationId;
    String expectedVersion;
    String email;
}
