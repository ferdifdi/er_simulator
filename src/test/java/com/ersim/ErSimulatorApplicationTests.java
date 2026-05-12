package com.ersim;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that verifies the Spring application context loads.
 * Sanity check that all beans wire together correctly.
 */
@SpringBootTest(properties = {
        "ersim.rooms.count=0",
        "ersim.simulation.autostart=false"
})
class ErSimulatorApplicationTests {

    @Test
    void contextLoads() {
        // intentionally empty: passes if Spring context boots cleanly
    }
}
