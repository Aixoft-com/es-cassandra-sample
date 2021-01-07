package com.aixoft.essample.command;

import com.aixoft.escassandra.aggregate.Aggregate;
import com.aixoft.escassandra.model.Command;
import com.aixoft.escassandra.model.Event;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.event.LoyalPointsAdded;
import lombok.Value;

import java.util.List;

@Value
public class AddLoyalPointsCommand implements Command<AccountInformation> {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 200;
    int loyalPointsCount;

    /**
     * Example: Event will be added to aggregate if total number of loyal point will be positive and less then MAX_VALUE.
     */
    @Override
    public boolean validate(Aggregate<AccountInformation> aggregate) {
        return loyalPointsCount > MIN_VALUE && loyalPointsCount < MAX_VALUE;
    }

    @Override
    public List<Event<AccountInformation>> toEvents() {
        return List.of(new LoyalPointsAdded(loyalPointsCount));
    }
}
