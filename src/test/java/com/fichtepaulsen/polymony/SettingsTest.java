package com.fichtepaulsen.polymony;

import org.junit.Test;
import static org.junit.Assert.*;

public class SettingsTest {

    public SettingsTest() {
    }

    @Test
    public void testSettings() {
        Settings.createInstance();

        assertNotNull(Settings.getInstance());

        Settings.destroyInstance();
    }
}
