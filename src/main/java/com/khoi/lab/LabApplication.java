package com.khoi.lab;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.khoi.lab.dao.AccountDAO;
import com.khoi.lab.dao.BlogDAO;
import com.khoi.lab.dao.DonationDAO;

@SpringBootApplication
@EnableScheduling
public class LabApplication {
	private final AccountDAO accountDAO;
	private final DonationDAO donationDAO;
	private final BlogDAO blogDAO;

	/**
	 * DAO Initiator
	 * 
	 * @param accountDAO
	 */
	public LabApplication(AccountDAO accountDAO, DonationDAO donationDAO, BlogDAO blogDAO) {
		this.accountDAO = accountDAO;
		this.donationDAO = donationDAO;
		this.blogDAO = blogDAO;
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
			donationDAO.initiate();
			blogDAO.initiate();
		};
	}
}
