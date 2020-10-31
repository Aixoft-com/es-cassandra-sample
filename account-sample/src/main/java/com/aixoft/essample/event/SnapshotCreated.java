package com.aixoft.essample.event;

import com.aixoft.escassandra.annotation.DomainEvent;
import com.aixoft.escassandra.model.SnapshotEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@DomainEvent(event = "SnapshotCreated")
public class SnapshotCreated implements SnapshotEvent {
    String userName;
    String email;
    int loyalPointsCount;

    @JsonCreator
    public SnapshotCreated(@JsonProperty("userName") String userName, @JsonProperty("email") String email, @JsonProperty("loyalPointsCount") int loyalPointsCount) {
        this.userName = userName;
        this.email = email;
        this.loyalPointsCount = loyalPointsCount;
    }
}
