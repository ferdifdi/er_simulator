package com.ersim;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test that verifies the Spring application context loads.
 *
 * TODO #Ferdi/#Sruthi: this test will be the first sanity check that all
 *               beans wire together correctly. No implementation needed
 *               beyond context loading.
 */
@SpringBootTest
class ErSimulatorApplicationTests {

    @Test
    void contextLoads() {
        // intentionally empty: passes if Spring context boots cleanly
    }
}
