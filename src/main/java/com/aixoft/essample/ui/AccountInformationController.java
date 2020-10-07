package com.aixoft.essample.ui;

import com.aixoft.escassandra.model.EventVersion;
import com.aixoft.escassandra.service.AggregateStore;
import com.aixoft.essample.aggregate.AccountInformation;
import com.aixoft.essample.command.AddLoyalPointsCommand;
import com.aixoft.essample.command.ChangeEmailCommand;
import com.aixoft.essample.command.CreateAccountCommand;
import com.aixoft.essample.command.CreateSnapshotCommand;
import com.aixoft.essample.exception.UnexpectedEventVersionException;
import com.aixoft.essample.ui.dto.AddLoyalPointsDto;
import com.aixoft.essample.ui.dto.ChangeEmailDto;
import com.aixoft.essample.ui.dto.CreateAccountDto;
import com.aixoft.essample.ui.dto.CreateSnapshotDto;
import com.aixoft.essample.util.EventVersionUtil;
import com.datastax.driver.core.utils.UUIDs;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/account")
@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountInformationController {

    AggregateStore aggregateStore;

    @PostMapping
    public void createAccount(@RequestBody CreateAccountDto createAccountDto) {

        AccountInformation accountInformation = new AccountInformation(UUIDs.timeBased());

        if(accountInformation != null) {
            accountInformation.handleCommand(new CreateAccountCommand(createAccountDto.getUserName(), createAccountDto.getEmail()));
            accountInformation.handleCommand(new AddLoyalPointsCommand(createAccountDto.getLoyalPoints()));
            aggregateStore.save(accountInformation);
        }
    }

    @PostMapping("/loyalPoints")
    public void addLoyalPoints(@RequestBody AddLoyalPointsDto addLoyalPointsDto) {

        AccountInformation accountInformation = aggregateStore.findById(addLoyalPointsDto.getAccountInformationId(), AccountInformation.class);

        if(accountInformation != null) {
            accountInformation.handleCommand(new AddLoyalPointsCommand(addLoyalPointsDto.getLoyalPoints()));
            aggregateStore.save(accountInformation);
        }
    }

    @PutMapping("/email")
    public void changeEmail(@RequestBody ChangeEmailDto changeEmailDto) throws Exception {

        AccountInformation accountInformation;
        EventVersion expectedEventVersion;

        if(changeEmailDto.getExpectedVersion() != null) {
            expectedEventVersion = EventVersionUtil.parseEventVersion(changeEmailDto.getExpectedVersion());
            accountInformation = aggregateStore.findById(changeEmailDto.getAccountInformationId(), expectedEventVersion.getSnapshotNumber(), AccountInformation.class);
        } else {
            expectedEventVersion = null;
            accountInformation = aggregateStore.findById(changeEmailDto.getAccountInformationId(), AccountInformation.class);
        }

        if(accountInformation != null) {
            if(expectedEventVersion!= null && !expectedEventVersion.equals(accountInformation.getCommittedVersion())) {
                throw new UnexpectedEventVersionException(String.format("Current event version is %s.%s",
                        accountInformation.getCommittedVersion().getSnapshotNumber(),
                        accountInformation.getCommittedVersion().getEventNumber()));
            }

            accountInformation.handleCommand(new ChangeEmailCommand(changeEmailDto.getEmail()));
            aggregateStore.save(accountInformation);
        }
    }

    @PostMapping("/snapshot")
    public void createSnapshot(@RequestBody CreateSnapshotDto createSnapshotDto) {

        AccountInformation accountInformation = aggregateStore.findById(createSnapshotDto.getAccountInformationId(), AccountInformation.class);

        if(accountInformation != null) {
            accountInformation.handleCommand(new CreateSnapshotCommand());
            aggregateStore.save(accountInformation);
        }
    }
}