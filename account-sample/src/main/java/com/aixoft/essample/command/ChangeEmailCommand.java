package com.aixoft.essample.command;

import com.aixoft.escassandra.aggregate.Aggregate;
import com.aixoft.escassandra.model.Command;
import com.aixoft.escassandra.model.Event;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.event.EmailModified;
import com.aixoft.essample.exception.UnexpectedEventVersionException;
import lombok.Value;

import java.util.List;

@Value
public class ChangeEmailCommand implements Command<AccountInformation> {
    String email;
    EventVersion expectedVersion;

    /**
     * Example: processing chain is stopped by exception thrown on validation failure
     * (version of aggregate is not as expected).
     */
    @Override
    public boolean validate(Aggregate<AccountInformation> aggregate) {
        if(aggregate.getCommittedVersion() == null) {
            throw new UnexpectedEventVersionException("Aggregate not found for given snapshot version");
        }

        if(expectedVersion != null && !expectedVersion.equals(aggregate.getCommittedVersion())) {
            throw new UnexpectedEventVersionException(String.format("Current event version is %s.%s",
                    aggregate.getCommittedVersion().getMajor(),
                    aggregate.getCommittedVersion().getMinor()));
        }

        return true;
    }

    @Override
    public List<Event<AccountInformation>> toEvents() {
        return List.of(new EmailModified(email));
    }
}
