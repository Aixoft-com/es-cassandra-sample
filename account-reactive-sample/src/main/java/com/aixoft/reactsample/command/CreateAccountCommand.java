package com.aixoft.reactsample.command;

import com.aixoft.escassandra.model.Command;
import com.aixoft.escassandra.model.Event;
import com.aixoft.reactsample.aggregate.AccountInformation;
import com.aixoft.reactsample.event.AccountCreated;
import lombok.Value;

import java.util.List;

@Value
public class CreateAccountCommand implements Command<AccountInformation> {
    private String userName;
    private String email;

    @Override
    public List<Event<AccountInformation>> toEvents() {
        return List.of(new AccountCreated(userName, email));
    }
}
