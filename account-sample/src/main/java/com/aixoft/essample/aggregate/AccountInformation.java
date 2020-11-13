package com.aixoft.essample.aggregate;

import com.aixoft.escassandra.aggregate.AggregateRoot;
import com.aixoft.escassandra.annotation.Aggregate;
import com.aixoft.escassandra.annotation.Subscribe;
import com.aixoft.escassandra.annotation.SubscribeAll;
import com.aixoft.essample.command.AddLoyalPointsCommand;
import com.aixoft.essample.command.ChangeEmailCommand;
import com.aixoft.essample.command.CreateAccountCommand;
import com.aixoft.essample.command.CreateSnapshotCommand;
import com.aixoft.essample.event.AccountCreated;
import com.aixoft.essample.event.EmailModified;
import com.aixoft.essample.event.LoyalPointsAdded;
import com.aixoft.essample.event.SnapshotCreated;
import lombok.Getter;

import java.util.UUID;

@Getter
@Aggregate(tableName = "account_information")
public class AccountInformation extends AggregateRoot {

    private String userName;
    private String email;
    private int loyalPoints;

    public AccountInformation(UUID id) {
        super(id);
    }

    @Subscribe
    public void apply(AccountCreated accountCreated) {
        userName = accountCreated.getUserName();
        email = accountCreated.getEmail();
    }

    @Subscribe
    public void apply(EmailModified emailModified) {
        email = emailModified.getEmail();
    }

    @Subscribe
    public void apply(LoyalPointsAdded loyalPointsAdded) {
        loyalPoints += loyalPointsAdded.getLoyalPointsCount();
    }

    @Subscribe
    public void apply(SnapshotCreated snapshotCreated) {
        userName = snapshotCreated.getUserName();
        email = snapshotCreated.getEmail();
        loyalPoints = snapshotCreated.getLoyalPointsCount();
    }

    public void handleCommand(CreateAccountCommand createAccountCommand) {
        publish(new AccountCreated(createAccountCommand.getUserName(), createAccountCommand.getEmail()));
    }

    public void handleCommand(ChangeEmailCommand changeEmailCommand) {
        publish(new EmailModified(changeEmailCommand.getEmail()));
    }

    public void handleCommand(AddLoyalPointsCommand addLoyalPointsCommand) {
        publish(new LoyalPointsAdded(addLoyalPointsCommand.getLoyalPointsCount()));
    }

    public void handleCommand(CreateSnapshotCommand createSnapshotCommand) {
        publish(new SnapshotCreated(userName, email, loyalPoints));
    }
}
