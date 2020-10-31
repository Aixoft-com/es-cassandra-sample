package com.aixoft.reactsample.ui.dto;

import lombok.Value;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Value
public class AddLoyalPointsDto {
    @NotEmpty
    UUID id;
    int loyalPoints;
}
