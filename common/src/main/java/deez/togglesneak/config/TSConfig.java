package deez.togglesneak.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TSConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;
    private static TSConfig instance;

    public boolean optionToggleSprint = true;
    public boolean optionToggleSneak = true;
    public boolean optionShowHUDText = true;
    public boolean optionEnableFlyBoost = false;
    public double optionFlyBoostAmount = 4.0;
    public int optionThreshold = 5;
    public int optionHUDTextPosX = 1;
    public int optionHUDTextPosY = 1;

    public static void load(File path) {
        configFile = new File(path, "togglesneak.json");
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                instance = GSON.fromJson(reader, TSConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                instance = new TSConfig();
            }
        } else {
            instance = new TSConfig();
            save();
        }
    }

    public static void save() {
        if (instance == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static TSConfig getInstance() {
        if (instance == null) instance = new TSConfig();
        return instance;
    }
}
