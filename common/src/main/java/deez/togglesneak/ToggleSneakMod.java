package deez.togglesneak;

import deez.togglesneak.config.TSConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ToggleSneakMod {
    public static final String MOD_ID = "togglesneak";
    public static final Logger LOGGER = LogManager.getLogger("ToggleSneak");

    public static void init(File configDir) {
        TSConfig.load(configDir);
        LOGGER.info("ToggleSneak initialized!");
    }
}
