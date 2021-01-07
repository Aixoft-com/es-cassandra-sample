package com.aixoft.essample.service;

import com.aixoft.escassandra.annotation.SubscribeAll;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.escassandra.service.EventListener;
import com.aixoft.escassandra.service.EventRouter;
import com.aixoft.essample.event.AccountCreated;
import com.aixoft.essample.event.EmailModified;
import com.aixoft.essample.event.LoyalPointsAdded;
import com.aixoft.essample.event.SnapshotCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class GeneralAggregateEventListener implements EventListener {
    public GeneralAggregateEventListener(EventRouter eventRouter) {
        eventRouter.registerEventHandler(this);
    }

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
