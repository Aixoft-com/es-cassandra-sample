package com.aixoft.essample.ui.dto;

import lombok.Value;

import java.util.UUID;

@Value
public class AddLoyalPointsDto {
    UUID accountInformationId;
    String expectedVersion;
    int loyalPoints;
}
