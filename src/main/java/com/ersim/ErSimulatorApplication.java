package com.ersim;

import com.ersim.gui.DashboardFrame;
import com.ersim.service.TriageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.SwingUtilities;
import java.util.Arrays;

/**
 * Main entry point for the ER Triage Simulator Spring Boot application.
 *
 * The TriageService thread pool and patient-arrival thread are bootstrapped
 * via @PostConstruct on TriageService.init().
 *
 * Pass --gui on the command line to launch the Swing monitoring dashboard.
 */
@SpringBootApplication
public class ErSimulatorApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(ErSimulatorApplication.class, args);

        if (Arrays.asList(args).contains("--gui")) {
            TriageService triageService = ctx.getBean(TriageService.class);
            SwingUtilities.invokeLater(() -> {
                DashboardFrame frame = new DashboardFrame(triageService);
                frame.startRefresh();
            });
        }
    }
}
