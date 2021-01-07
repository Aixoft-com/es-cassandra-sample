package com.aixoft.essample.command;

import com.aixoft.escassandra.model.SnapshotCommand;
import com.aixoft.escassandra.model.SnapshotEvent;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.event.SnapshotCreated;
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
