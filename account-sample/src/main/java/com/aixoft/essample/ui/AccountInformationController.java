package com.aixoft.essample.ui;

import com.aixoft.escassandra.aggregate.Aggregate;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.escassandra.service.AggregateStore;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.command.AddLoyalPointsCommand;
import com.aixoft.essample.command.ChangeEmailCommand;
import com.aixoft.essample.command.CreateAccountCommand;
import com.aixoft.essample.command.CreateSnapshotCommand;
import com.aixoft.essample.exception.UnexpectedEventVersionException;
import com.aixoft.essample.ui.dto.*;
import com.aixoft.essample.util.EventVersionUtil;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public Optional<ResponseAggregateVersionDto> createAccount(@RequestBody CreateAccountDto createAccountDto) {
        Aggregate<AccountInformation> aggregate = Aggregate.create(Uuids.timeBased());

        aggregate.handleCommand(new CreateAccountCommand(createAccountDto.getUserName(), createAccountDto.getEmail()));
        aggregate.handleCommand(new AddLoyalPointsCommand(createAccountDto.getLoyalPoints()));

        return aggregateStore.save(aggregate)
                .map(ResponseAggregateVersionDto::fromAggregate);
    }

    /**
     * Command validation which ignores event if the validation is failed.
     * <p>
     * Example presents how to apply single command on aggregate.
     * Snapshot version is not provided in loadById method,
     * so aggregate will be loaded from scratch (starting from 1st event).
     * @param addLoyalPointsDto
     * @return Dto with current version of the aggregate.
     */
    @PostMapping("/loyalPoints")
    public Optional<ResponseAggregateVersionDto> addLoyalPoints(@RequestBody AddLoyalPointsDto addLoyalPointsDto) {

        return aggregateStore.loadById(addLoyalPointsDto.getId(), AccountInformation.class)
            .flatMap(accountInformation -> {
                accountInformation.handleCommand(new AddLoyalPointsCommand(addLoyalPointsDto.getLoyalPoints()));

                return aggregateStore.save(accountInformation);
            })
            .map(ResponseAggregateVersionDto::fromAggregate);
    }

    /**
     * Command validation which breaks execution chain if the validation is failed.
     * <p>
     * Example presents how to apply single command for expected aggregate version.
     * Snapshot version is provided in loadById method,
     * so aggregate will be loaded since provided snapshot version.
     * Command verifies if aggregate has expected version and response with error otherwise breaking execution chain.
     * (version is being also verified on save method)
     * @param changeEmailDto
     * @return Dto with current version of the aggregate.
     */
    @PutMapping("/email")
    public ResponseAggregateVersionDto changeEmail(@RequestBody ChangeEmailDto changeEmailDto) {

        EventVersion expectedEventVersion = EventVersionUtil.parseEventVersion(changeEmailDto.getExpectedVersion());

        return aggregateStore.loadById(changeEmailDto.getId(), expectedEventVersion.getMajor(), AccountInformation.class)
                .flatMap(aggregate -> {
                    aggregate.handleCommand(new ChangeEmailCommand(changeEmailDto.getEmail(), expectedEventVersion));

                    return aggregateStore.save(aggregate);
                })
                .map(ResponseAggregateVersionDto::fromAggregate)
                .orElseThrow(() -> new UnexpectedEventVersionException("Aggregate not found or invalid version provided"));
    }

    /**
     * Example presents how to create aggregate snapshot.
     * Snapshot command results in snapshot event being stored in database.
     * @param createSnapshotDto
     * @return Mono with current version of the aggregate.
     */
    @PostMapping("/snapshot")
    public Optional<ResponseAggregateVersionDto> createSnapshot(@RequestBody CreateSnapshotDto createSnapshotDto) {

        return aggregateStore.loadById(createSnapshotDto.getId(), AccountInformation.class)
                .flatMap(aggregate -> {
                    aggregate.handleSnapshotCommand(new CreateSnapshotCommand(
                            aggregate.getData().getUserName(),
                            aggregate.getData().getEmail(),
                            aggregate.getData().getLoyalPoints()));

                    return aggregateStore.save(aggregate);
                })
                .map(ResponseAggregateVersionDto::fromAggregate);
    }
}
