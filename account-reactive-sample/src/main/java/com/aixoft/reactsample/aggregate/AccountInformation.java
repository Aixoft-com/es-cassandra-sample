package com.aixoft.reactsample.aggregate;

import com.aixoft.escassandra.aggregate.AggregateRoot;
import com.aixoft.escassandra.annotation.Aggregate;
import com.aixoft.escassandra.annotation.Subscribe;
import com.aixoft.reactsample.command.AddLoyalPointsCommand;
import com.aixoft.reactsample.command.ChangeEmailCommand;
import com.aixoft.reactsample.command.CreateAccountCommand;
import com.aixoft.reactsample.command.CreateSnapshotCommand;
import com.aixoft.reactsample.event.AccountCreated;
import com.aixoft.reactsample.event.EmailModified;
import com.aixoft.reactsample.event.LoyalPointsAdded;
import com.aixoft.reactsample.event.SnapshotCreated;
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
