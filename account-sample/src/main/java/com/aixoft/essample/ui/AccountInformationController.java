package com.aixoft.essample.ui;

import com.aixoft.escassandra.aggregate.AggregateRoot;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.escassandra.service.AggregateStore;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.command.AddLoyalPointsCommand;
import com.aixoft.essample.command.ChangeEmailCommand;
import com.aixoft.essample.command.CreateAccountCommand;
import com.aixoft.essample.command.CreateSnapshotCommand;
import com.aixoft.essample.exception.UnexpectedEventVersionException;
import com.aixoft.essample.util.EventVersionUtil;
import com.aixoft.reactsample.ui.dto.AddLoyalPointsDto;
import com.aixoft.reactsample.ui.dto.ChangeEmailDto;
import com.aixoft.reactsample.ui.dto.ResponseCreateAccountDto;
import com.aixoft.reactsample.ui.dto.ResponseEventVersionDto;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/account")
@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountInformationController {

    AggregateStore aggregateStore;

    /**
     * Example presents how to create new aggregate.
     * It is possible to handle create command followed by another commands even if aggregate was not stored in the database.
     * In the following example two commands are handled:
     * 1st creates aggregate,
     * 2nd adds loyal points
     *
     * @param createAccountDto
     * @return Dto with id of newly created aggregate and current version of the aggregate.
     */
    @PostMapping
    public ResponseCreateAccountDto createAccount(@RequestBody com.aixoft.reactsample.ui.dto.CreateAccountDto createAccountDto) {

        UUID id = Uuids.timeBased();

        AccountInformation accountInformation = new AccountInformation(id);

        accountInformation.handleCommand(new CreateAccountCommand(createAccountDto.getUserName(), createAccountDto.getEmail()));
        accountInformation.handleCommand(new AddLoyalPointsCommand(createAccountDto.getLoyalPoints()));

        ResponseCreateAccountDto responseCreateAccountDto = null;
        if(aggregateStore.save(accountInformation)) {
            responseCreateAccountDto = new ResponseCreateAccountDto(id,
                    String.format("%d.%d",
                            accountInformation.getCommittedVersion().getMajor(),
                            accountInformation.getCommittedVersion().getMinor()));
        }

        return responseCreateAccountDto;
    }

    /**
     * Example presents how to apply single command on aggregate.
     * Snapshot version is not provided in loadById method,
     * so aggregate will be loaded from scratch (starting from 1st event).
     * @param addLoyalPointsDto
     * @return Dto with current version of the aggregate.
     */
    @PostMapping("/loyalPoints")
    public ResponseEventVersionDto addLoyalPoints(@RequestBody AddLoyalPointsDto addLoyalPointsDto) {

        AccountInformation accountInformation = aggregateStore.loadById(addLoyalPointsDto.getId(), AccountInformation.class);

        ResponseEventVersionDto responseEventVersionDto = null;

        if(accountInformation != null) {
            accountInformation.handleCommand(new AddLoyalPointsCommand(addLoyalPointsDto.getLoyalPoints()));
            if(aggregateStore.save(accountInformation)) {
                responseEventVersionDto = ResponseEventVersionDto.fromEventVersion(accountInformation.getCommittedVersion());
            }
        }

        return responseEventVersionDto;
    }

    /**
     * Example presents how to apply single command for expected aggregate version.
     * Snapshot version is provided in loadById method,
     * so aggregate will be loaded since provided snapshot version.
     * Methods verifies if aggregate has expected version and response with error otherwise.
     * (version is being also verified on save method)
     * @param changeEmailDto
     * @return Dto with current version of the aggregate.
     */
    @PutMapping("/email")
    public ResponseEventVersionDto changeEmail(@RequestBody ChangeEmailDto changeEmailDto) {

        AccountInformation accountInformation;
        EventVersion expectedEventVersion;

        ResponseEventVersionDto responseEventVersionDto = null;

        if(changeEmailDto.getExpectedVersion() != null) {
            expectedEventVersion = EventVersionUtil.parseEventVersion(changeEmailDto.getExpectedVersion());
            accountInformation = aggregateStore.loadById(changeEmailDto.getId(), expectedEventVersion.getMajor(), AccountInformation.class);

            if(accountInformation != null) {
                assertAccountInformationVersion(accountInformation.getCommittedVersion(), accountInformation);

                accountInformation.handleCommand(new ChangeEmailCommand(changeEmailDto.getEmail()));
                if(aggregateStore.save(accountInformation)) {
                    responseEventVersionDto = ResponseEventVersionDto.fromEventVersion(accountInformation.getCommittedVersion());
                }
            }
        }

        return responseEventVersionDto;
    }

    private void assertAccountInformationVersion(EventVersion expectedVersion, AggregateRoot aggregateRoot) throws UnexpectedEventVersionException {
        if(aggregateRoot.getCommittedVersion() == null) {
            throw new UnexpectedEventVersionException("Aggregate not found for given snapshot version");
        }

        if(expectedVersion != null && !expectedVersion.equals(aggregateRoot.getCommittedVersion())) {
            throw new UnexpectedEventVersionException(String.format("Current event version is %s.%s",
                    aggregateRoot.getCommittedVersion().getMajor(),
                    aggregateRoot.getCommittedVersion().getMinor()));
        }
    }

    /**
     * Example presents how to create aggregate snapshot.
     * Snapshot command results in snapshot event being stored in database.
     * @param createSnapshotDto
     * @return Mono with current version of the aggregate.
     */
    @PostMapping("/snapshot")
    public ResponseEventVersionDto createSnapshot(@RequestBody com.aixoft.reactsample.ui.dto.CreateSnapshotDto createSnapshotDto) {

        AccountInformation accountInformation = aggregateStore.loadById(createSnapshotDto.getId(), AccountInformation.class);

        ResponseEventVersionDto responseEventVersionDto = null;

        if(accountInformation != null) {
            accountInformation.handleCommand(new CreateSnapshotCommand());
            if(aggregateStore.save(accountInformation)) {
                responseEventVersionDto = ResponseEventVersionDto.fromEventVersion(accountInformation.getCommittedVersion());
            }
        }

        return responseEventVersionDto;
    }
}
