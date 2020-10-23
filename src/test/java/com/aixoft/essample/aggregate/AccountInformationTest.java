package com.aixoft.essample.aggregate;

import com.aixoft.escassandra.service.AggregateStore;
import com.aixoft.essample.EsSampleApplication;
import com.aixoft.essample.command.AddLoyalPointsCommand;
import com.aixoft.essample.command.ChangeEmailCommand;
import com.aixoft.essample.command.CreateAccountCommand;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;


@Slf4j
@SpringBootTest(classes = EsSampleApplication.class)
class AccountInformationTest {
    private static final int Iterations = 100;
    private static final int ExecutionsInSingleIteration = 100;

    @Autowired
    AggregateStore aggregateStore;

    @Test
    void handleCommandPerformance() {
        UUID id = UUID.fromString("8e11c770-188c-11eb-b02a-173a433a4009");

        AccountInformation accountInformation = new AccountInformation(id);
        accountInformation.handleCommand(new CreateAccountCommand("username", "email@gmail.com"));

        aggregateStore.save(accountInformation);

        Instant totalStart = Instant.now();
        Instant start;

        accountInformation = aggregateStore.loadById(id, AccountInformation.class);

        accountInformation.handleCommand(new ChangeEmailCommand("newemail@gmail.com"));
        accountInformation.handleCommand(new AddLoyalPointsCommand(100));

        for(int j = 0; j<Iterations; j++) {

            start = Instant.now();

            for(int i = 0; i < ExecutionsInSingleIteration; i++) {
                AccountInformation accountInformation2 = aggregateStore.loadById(id, AccountInformation.class);

                accountInformation2.handleCommand(new ChangeEmailCommand("newemail@gmail.com"));
                accountInformation2.handleCommand(new AddLoyalPointsCommand(100));

                aggregateStore.save(accountInformation2);
            }

            log.info("Iteration {}: {}", j, Instant.now().toEpochMilli() - start.toEpochMilli());
        }

        System.out.println("Total: " + (Instant.now().toEpochMilli() - totalStart.toEpochMilli()));

    }
}