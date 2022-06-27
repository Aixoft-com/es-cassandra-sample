package com.aixoft.essample.command;

import com.aixoft.escassandra.aggregate.Aggregate;
import com.aixoft.escassandra.model.Event;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.escassandra.model.command.VersionedCommand;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.event.EmailModified;
import lombok.Value;

import java.util.List;

@Value
/**
 * Example: processing chain is stopped by exception thrown on validation failure
 * (version of aggregate is not as expected).
 */
public class ChangeEmailCommand extends VersionedCommand<AccountInformation> {
    String email;
    EventVersion expectedVersion;

    @Override
    public boolean postValidate(Aggregate<AccountInformation> aggregate) {
        // Email format can be validated here.
        return true;
    }

    @Override
    public List<Event<AccountInformation>> toEvents() {
        return List.of(new EmailModified(email));
    }
}
