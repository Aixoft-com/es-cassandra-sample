package com.aixoft.reactsample;

import com.aixoft.escassandra.annotation.EnableCassandraEventSourcing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCassandraEventSourcing(aggregatePackages = "com.aixoft.reactsample", eventPackages = "com.aixoft.reactsample.event")
public class EsReactiveSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsReactiveSampleApplication.class, args);
	}

}
