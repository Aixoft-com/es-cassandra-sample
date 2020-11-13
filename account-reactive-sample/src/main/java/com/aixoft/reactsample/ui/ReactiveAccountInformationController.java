package com.aixoft.reactsample.ui;

import com.aixoft.escassandra.aggregate.AggregateRoot;
import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.escassandra.service.ReactiveAggregateStore;
import com.aixoft.reactsample.aggregate.AccountInformation;
import com.aixoft.reactsample.command.AddLoyalPointsCommand;
import com.aixoft.reactsample.command.ChangeEmailCommand;
import com.aixoft.reactsample.command.CreateAccountCommand;
import com.aixoft.reactsample.command.CreateSnapshotCommand;
import com.aixoft.reactsample.exception.UnexpectedEventVersionException;
import com.aixoft.reactsample.ui.dto.*;
import com.aixoft.reactsample.util.EventVersionUtil;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseAggregateVersionDto> createAccount(@RequestBody CreateAccountDto createAccountDto) {

        return Mono.just(new AccountInformation(Uuids.timeBased()))
            .doOnNext(accountInformation -> accountInformation.handleCommand(new CreateAccountCommand(createAccountDto.getUserName(), createAccountDto.getEmail())))
            .doOnNext(accountInformation -> accountInformation.handleCommand(new AddLoyalPointsCommand(createAccountDto.getLoyalPoints())))
            .flatMap(aggregateStore::save)
            .map(ResponseAggregateVersionDto::fromAggregate);
    }

    /**
     * Example presents how to apply single command on aggregate.
     * Snapshot version is not provided in loadById method,
     * so aggregate will be loaded from scratch (starting from 1st event).
     * @param addLoyalPointsDto
     * @return Mono with current version of the aggregate.
     */
    @PostMapping("/loyalPoints")
    public Mono<ResponseAggregateVersionDto> addLoyalPoints(@RequestBody AddLoyalPointsDto addLoyalPointsDto) {

        return aggregateStore.loadById(addLoyalPointsDto.getId(), AccountInformation.class)
            .doOnNext(aggregate -> aggregate.handleCommand(new AddLoyalPointsCommand(addLoyalPointsDto.getLoyalPoints())))
            .flatMap(aggregateStore::save)
            .map(ResponseAggregateVersionDto::fromAggregate);
    }

    /**
     * Example presents how to apply single command for expected aggregate version.
     * Snapshot version is provided in loadById method,
     * so aggregate will be loaded since provided snapshot version.
     * Methods verifies if aggregate has expected version and response with error otherwise.
     * (version is being also verified on save method)
     * @param changeEmailDto
     * @return Mono with current version of the aggregate.
     */
    @PutMapping("/email")
    public Mono<ResponseAggregateVersionDto> changeEmail(@RequestBody ChangeEmailDto changeEmailDto)  {
        EventVersion expectedEventVersion = EventVersionUtil.parseEventVersion(changeEmailDto.getExpectedVersion());

        return aggregateStore.loadById(changeEmailDto.getId(), expectedEventVersion.getMajor(), AccountInformation.class)
                .doOnNext(aggregate -> assertAccountInformationVersion(expectedEventVersion, aggregate))
                .doOnNext(aggregate -> aggregate.handleCommand(new ChangeEmailCommand(changeEmailDto.getEmail())))
                .flatMap(aggregateStore::save)
                .map(ResponseAggregateVersionDto::fromAggregate);
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
    public Mono<ResponseAggregateVersionDto> createSnapshot(@RequestBody CreateSnapshotDto createSnapshotDto) {
         return aggregateStore.loadById(createSnapshotDto.getId(), AccountInformation.class)
                .doOnNext(aggregate -> aggregate.handleCommand(new CreateSnapshotCommand()))
                .flatMap(aggregateStore::save)
                .map(ResponseAggregateVersionDto::fromAggregate);
    }
}