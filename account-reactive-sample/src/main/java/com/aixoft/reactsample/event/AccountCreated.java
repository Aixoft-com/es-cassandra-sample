package com.aixoft.reactsample.event;

import com.aixoft.escassandra.annotation.DomainEvent;
import com.aixoft.escassandra.model.AggregateUpdater;
import com.aixoft.escassandra.model.Event;
import com.aixoft.reactsample.aggregate.AccountInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@DomainEvent(event = "AccountCreated")
public class AccountCreated implements Event<AccountInformation> {
    String userName;
    String email;

    @JsonCreator
    public AccountCreated(@JsonProperty("userName") String userName, @JsonProperty("email") String email) {
        this.userName = userName;
        this.email = email;
    }

    @Override
    public AggregateUpdater<AccountInformation> createUpdater() {
        return obj -> new AccountInformation(userName, email, 0);
    }
}
