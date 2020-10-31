package com.aixoft.essample;

import com.aixoft.escassandra.annotation.EnableCassandraEventSourcing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCassandraEventSourcing(aggregatePackages = "com.aixoft.essample", eventPackages = "com.aixoft.essample.event")
public class EsSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsSampleApplication.class, args);
	}

}
