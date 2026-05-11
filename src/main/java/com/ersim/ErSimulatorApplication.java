package com.ersim;

import com.ersim.gui.DashboardFrame;
import com.ersim.service.TriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.SwingUtilities;
import java.util.Arrays;

/**
 * Main entry point for the ER Triage Simulator Spring Boot application.
 *
 * The TriageService thread pool and patient-arrival thread are bootstrapped
 * via @PostConstruct on TriageService.init().
 */
@SpringBootApplication
public class ErSimulatorApplication {

    @Autowired
    private TriageService triageService;

    public static void main(String[] args) {
        SpringApplication.run(ErSimulatorApplication.class, args);
    }

    @Bean
    public CommandLineRunner guiLauncher() {
        return args -> {
            if (Arrays.asList(args).contains("--gui")) {
                SwingUtilities.invokeLater(() -> {
                    DashboardFrame frame = new DashboardFrame(triageService);
                    frame.startRefresh();
                });
            }
        };
    }
}
