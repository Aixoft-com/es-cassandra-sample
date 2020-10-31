package com.aixoft.reactsample.ui.dto;

import lombok.Value;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Value
public class ChangeEmailDto {
    @NotEmpty
    UUID id;
    @NotEmpty
    String expectedVersion;
    @NotEmpty
    String email;
}
