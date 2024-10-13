package com.bitc.bitcclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.bitc.bitcclub.repository")
@EntityScan("com.bitc.bitcclub.model")
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class BitcClubApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitcClubApplication.class, args);
	}

}
