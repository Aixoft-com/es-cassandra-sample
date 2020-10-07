package com.aixoft.essample.ui.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class CreateSnapshotDto {
    UUID accountInformationId;
    String expectedVersion;
}
