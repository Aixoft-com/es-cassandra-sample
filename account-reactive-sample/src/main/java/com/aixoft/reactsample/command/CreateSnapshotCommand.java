package com.aixoft.reactsample.command;

import com.aixoft.escassandra.model.SnapshotCommand;
import com.aixoft.escassandra.model.SnapshotEvent;
import com.aixoft.reactsample.aggregate.AccountInformation;
import com.aixoft.reactsample.event.SnapshotCreated;
import lombok.Value;

@Value
public class CreateSnapshotCommand implements SnapshotCommand<AccountInformation> {
    private String userName;
    private String email;
    private int loyalPoints;

    @Override
    public SnapshotEvent<AccountInformation> toEvent() {
        return new SnapshotCreated(userName, email, loyalPoints);
    }
}
