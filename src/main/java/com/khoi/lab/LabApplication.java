package com.khoi.lab;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.khoi.lab.dao.AccountDAO;

@SpringBootApplication
public class LabApplication {
	private final AccountDAO accountDAO;

	/**
	 * DAO Initiator
	 * 
	 * @param accountDAO
	 */
	public LabApplication(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	public static void main(String[] args) {
		SpringApplication.run(LabApplication.class, args);
	}

	/**
	 * Command line runner.
	 * 
	 * @return
	 */
	@Bean
	CommandLineRunner commandLineRunner() {
		return _ -> {
			System.out.println("| Application Ready!");
			accountDAO.initiate();
		};
	}
}
