package com.aixoft.reactsample.service;

import com.aixoft.escassandra.aggregate.AggregateRoot;
import com.aixoft.escassandra.service.EventHandler;
import com.aixoft.escassandra.annotation.Subscribe;
import com.aixoft.escassandra.service.EventRouter;
import com.aixoft.reactsample.aggregate.AccountInformation;
import com.aixoft.reactsample.event.AccountCreated;
import com.aixoft.reactsample.event.EmailModified;
import com.aixoft.reactsample.event.LoyalPointsAdded;
import com.aixoft.reactsample.event.SnapshotCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeneralAggregateEventListener implements EventHandler {
    public GeneralAggregateEventListener(EventRouter eventRouter) {
        eventRouter.registerEventHandler(this);
    }

    @Subscribe
    private void handleAccountCreated(AccountCreated accountCreated, AggregateRoot aggregateRoot) {
//        log.info("{} - handleAccountCreated for event {} from {}",
//                GeneralAggregateEventListener.class.getTypeName(),
//                accountCreated,
//                aggregateRoot
//        );
    }

    @Subscribe
    private void handleEmailModified(EmailModified emailModified, AggregateRoot aggregateRoot) {
//        log.info("{} - handleEmailModified for event {} from {}",
//                GeneralAggregateEventListener.class.getTypeName(),
//                emailModified,
//                aggregateRoot
//        );
    }

    @Subscribe
    private void handleLoyalPointsAdded(LoyalPointsAdded loyalPointsAdded, AccountInformation aggregateRoot) {
//        log.info("{} - handleLoyalPointsAdded for event {} from {}, totalCount = {}",
//                GeneralAggregateEventListener.class.getTypeName(),
//                loyalPointsAdded,
//                aggregateRoot,
//                aggregateRoot.getLoyalPoints()
//        );
    }

    @Subscribe
    private void handleSnapshotCreated(SnapshotCreated snapshotCreated, AccountInformation aggregateRoot) {
//        log.info("{} - handleSnapshotCreated for event {} from {}, userName={}, email={}, points={}",
//                GeneralAggregateEventListener.class.getTypeName(),
//                snapshotCreated,
//                aggregateRoot,
//                aggregateRoot.getUserName(),
//                aggregateRoot.getEmail(),
//                aggregateRoot.getLoyalPoints()
//        );
    }

}
