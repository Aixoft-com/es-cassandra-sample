package com.aixoft.essample.command;

import com.aixoft.escassandra.model.Command;
import com.aixoft.escassandra.model.Event;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.event.AccountCreated;
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
