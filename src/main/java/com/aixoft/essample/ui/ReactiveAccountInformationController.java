package com.aixoft.essample.ui;

import com.aixoft.escassandra.aggregate.AggregateRoot;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.escassandra.service.ReactiveAggregateStore;
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
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequestMapping("/react-api/account")
@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReactiveAccountInformationController {

    ReactiveAggregateStore aggregateStore;

    /**
     * Example presents how to create new aggregate in reactive way.
     * It is possible to handle create command followed by another commands even if aggregate was not stored in the database.
     * In the following example two commands are handled:
     * 1st creates aggregate,
     * 2nd adds loyal points
     *
     * @param createAccountDto
     * @return Mono with the id of newly created aggregate and current version of the aggregate.
     */
    @PostMapping
    public Mono<ResponseCreateAccountDto> createAccount(@RequestBody CreateAccountDto createAccountDto) {

        UUID id = Uuids.timeBased();
        return Mono.just(new AccountInformation(id))
            .doOnNext(accountInformation -> accountInformation.handleCommand(new CreateAccountCommand(createAccountDto.getUserName(), createAccountDto.getEmail())))
            .doOnNext(accountInformation -> accountInformation.handleCommand(new AddLoyalPointsCommand(createAccountDto.getLoyalPoints())))
            .flatMap(aggregateStore::save)
            .map(eventVersion -> new ResponseCreateAccountDto(id, String.format("%d.%d", eventVersion.getMajor(), eventVersion.getMinor())));
    }

    /**
     * Example presents how to apply single command on aggregate.
     * Snapshot version is not provided in findById method,
     * so aggregate will be loaded from scratch (starting from 1st event).
     * @param addLoyalPointsDto
     * @return Mono with current version of the aggregate.
     */
    @PostMapping("/loyalPoints")
    public Mono<ResponseEventVersionDto> addLoyalPoints(@RequestBody AddLoyalPointsDto addLoyalPointsDto) {

        return aggregateStore.loadById(addLoyalPointsDto.getId(), AccountInformation.class)
            .doOnNext(aggregate -> aggregate.handleCommand(new AddLoyalPointsCommand(addLoyalPointsDto.getLoyalPoints())))
            .flatMap(aggregateStore::save)
            .map(ResponseEventVersionDto::fromEventVersion);
    }

    /**
     * Example presents how to apply single command for expected aggregate version.
     * Snapshot version is provided in findById method,
     * so aggregate will be loaded since provided snapshot version.
     * Methods verifies if aggregate has expected version and response with error otherwise.
     * (version is being also verified on save method)
     * @param changeEmailDto
     * @return Mono with current version of the aggregate.
     */
    @PutMapping("/email")
    public Mono<ResponseEventVersionDto> changeEmail(@RequestBody ChangeEmailDto changeEmailDto)  {
        EventVersion expectedEventVersion = EventVersionUtil.parseEventVersion(changeEmailDto.getExpectedVersion());

        return aggregateStore.loadById(changeEmailDto.getId(), expectedEventVersion.getMajor(), AccountInformation.class)
                .doOnNext(aggregate -> assertAccountInformationVersion(expectedEventVersion, aggregate))
                .doOnNext(aggregate -> aggregate.handleCommand(new ChangeEmailCommand(changeEmailDto.getEmail())))
                .flatMap(aggregateStore::save)
                .map(ResponseEventVersionDto::fromEventVersion);
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
    public Mono<ResponseEventVersionDto> createSnapshot(@RequestBody CreateSnapshotDto createSnapshotDto) {
         return aggregateStore.loadById(createSnapshotDto.getId(), AccountInformation.class)
                .doOnNext(aggregate -> aggregate.handleCommand(new CreateSnapshotCommand()))
                .flatMap(aggregateStore::save)
                .map(ResponseEventVersionDto::fromEventVersion);
    }
}