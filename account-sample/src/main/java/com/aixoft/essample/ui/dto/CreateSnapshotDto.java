package com.aixoft.essample.ui.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Value

public class CreateSnapshotDto {

    @JsonCreator
    public CreateSnapshotDto(@JsonProperty("id") UUID id) {
        this.id = id;
    }

    @NotEmpty
    UUID id;
}
