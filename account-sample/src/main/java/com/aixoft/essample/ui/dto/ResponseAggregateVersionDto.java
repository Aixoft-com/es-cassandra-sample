package com.aixoft.essample.ui.dto;

import com.aixoft.escassandra.aggregate.AggregateRoot;
import lombok.Value;

import java.util.UUID;

@Value
public class ResponseAggregateVersionDto {
    UUID id;
    String version;

    public static ResponseAggregateVersionDto fromAggregate(AggregateRoot aggregateRoot) {
        return new ResponseAggregateVersionDto(
                aggregateRoot.getId(),
                String.format("%d.%d",
                        aggregateRoot.getCommittedVersion().getMajor(),
                        aggregateRoot.getCommittedVersion().getMinor())
        );
    }
}
