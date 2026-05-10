package com.ersim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the ER Triage Simulator Spring Boot application.
 *
 * The TriageService thread pool and patient-arrival thread are bootstrapped
 * via @PostConstruct on TriageService.init().
 *
 * TODO #Sruthi: optionally launch the Swing dashboard from a CommandLineRunner
 *               when a "--gui" flag is passed.
 */
@SpringBootApplication
public class ErSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErSimulatorApplication.class, args);
    }
}
