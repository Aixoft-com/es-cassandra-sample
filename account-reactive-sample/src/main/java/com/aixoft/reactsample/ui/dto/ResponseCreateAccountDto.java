package com.aixoft.reactsample.ui.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class ResponseCreateAccountDto {
    UUID id;
    String version;
}
