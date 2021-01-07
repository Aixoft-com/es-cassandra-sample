package com.aixoft.reactsample.event;

import com.aixoft.escassandra.annotation.DomainEvent;
import com.aixoft.escassandra.model.AggregateUpdater;
import com.aixoft.escassandra.model.Event;
import com.aixoft.reactsample.aggregate.AccountInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@DomainEvent(event = "EmailModified")
public class EmailModified implements Event<AccountInformation> {
    String email;

    @JsonCreator
    public EmailModified(@JsonProperty("email") String email) {
        this.email = email;
    }

    @Override
    public AggregateUpdater<AccountInformation> createUpdater() {
        return obj -> obj.withEmail(email);
    }
}
