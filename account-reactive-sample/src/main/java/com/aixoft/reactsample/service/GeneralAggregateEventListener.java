package com.aixoft.reactsample.service;

import com.aixoft.escassandra.annotation.EventListener;
import com.aixoft.escassandra.annotation.SubscribeAll;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.reactsample.event.AccountCreated;
import com.aixoft.reactsample.event.EmailModified;
import com.aixoft.reactsample.event.LoyalPointsAdded;
import com.aixoft.reactsample.event.SnapshotCreated;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@EventListener
public class GeneralAggregateEventListener {

    @SubscribeAll
    public void handleAccountCreated(AccountCreated accountCreated, EventVersion version, UUID aggregateId) {
        log.info("{} - handleAccountCreated for event {}, {} from {}",
                GeneralAggregateEventListener.class.getTypeName(),
                accountCreated,
                version,
                aggregateId
        );
    }

    @SubscribeAll
    public void handleEmailModified(EmailModified emailModified, EventVersion version, UUID aggregateId) {
        log.info("{} - handleEmailModified for event {}, {} from {}",
                GeneralAggregateEventListener.class.getTypeName(),
                emailModified,
                version,
                aggregateId
        );
    }

    @SubscribeAll
    public void handleLoyalPointsAdded(LoyalPointsAdded loyalPointsAdded, EventVersion version, UUID aggregateId) {
        log.info("{} - handleLoyalPointsAdded for event {}, {} from {}",
                GeneralAggregateEventListener.class.getTypeName(),
                loyalPointsAdded,
                version,
                aggregateId
        );
    }

    @SubscribeAll
    public void handleSnapshotCreated(SnapshotCreated snapshotCreated, EventVersion version, UUID aggregateId) {
        log.info("{} - handleSnapshotCreated for event {}, {} from {}",
                GeneralAggregateEventListener.class.getTypeName(),
                snapshotCreated,
                version,
                aggregateId
        );
    }

}
