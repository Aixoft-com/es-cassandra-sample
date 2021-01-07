package com.aixoft.reactsample.event;

import com.aixoft.escassandra.annotation.DomainEvent;
import com.aixoft.escassandra.model.AggregateUpdater;
import com.aixoft.escassandra.model.Event;
import com.aixoft.reactsample.aggregate.AccountInformation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
@DomainEvent(event = "LoyalPointsAdded")
public class LoyalPointsAdded implements Event<AccountInformation> {
    int loyalPointsCount;

    @JsonCreator
    public LoyalPointsAdded(@JsonProperty("loyalPointsCount") int loyalPointsCount) {
        this.loyalPointsCount = loyalPointsCount;
    }

    @Override
    public AggregateUpdater<AccountInformation> createUpdater() {
        return obj -> obj.withLoyalPoints(obj.getLoyalPoints() + loyalPointsCount);
    }
}
