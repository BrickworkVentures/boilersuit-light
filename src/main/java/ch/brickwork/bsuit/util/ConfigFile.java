package ch.brickwork.bsuit.util;

import ch.brickwork.bsuit.globals.IBoilersuitApplicationContext;

import java.io.File;
import java.util.Hashtable;

/**
 * Created by marcel on 4/18/16.
 */
public class ConfigFile {
    public static final String KEYS_NUMBER_OF_RESULT_ROWS = "console.numberOfResultRows";

    private static String[] ALLOWED_KEYS = {KEYS_NUMBER_OF_RESULT_ROWS};

    private static String[] DEFAULT_VALUES = {"99"};

    private final IBoilersuitApplicationContext context;

    private Hashtable<String, String> values;

    private static ConfigFile instance = null;

    public static ConfigFile getInstance(IBoilersuitApplicationContext context) {
        if (instance == null)
            instance = new ConfigFile(context);
        return instance;
    }

    public void refreshConfigFile(String path) {
        setDefaultValues();
        overwriteWithFileValuesIfAvailable(path);
    }

    private ConfigFile(IBoilersuitApplicationContext context) {
        this.context = context;
        setDefaultValues();
        overwriteWithFileValuesIfAvailable(context.getWorkingDirectory());
    }

    public String getValue(String key) {
        return values.get(key);
    }

    public int getInteger(String key) {
        return new Integer(getValue(key));
    }


    private void overwriteWithFileValuesIfAvailable(String path) {
        File configFile = new File(path + "/boilersuit.conf");

        if (configFile.exists()) {
            String configFileContent = FileIOUtils.readCompleteFile(configFile) + "\n";

            for (String line : configFileContent.split("\n")) {
                String[] key_val = line.split("=");
                if (key_val == null) {
                    context.getLog().warn("Error in config file! Ignored");
                    return;
                }
                if (!key_val[0].trim().startsWith("--")) {
                    values.put(key_val[0].trim(), key_val[1].trim());
                    context.getLog().log("Added " + key_val[0] + " = " + key_val[1] + " to config");
                }
            }
        }
    }

    private void setDefaultValues() {
        values = new Hashtable<>();
        for (int i = 0; i < ALLOWED_KEYS.length; i++) {
            values.put(ALLOWED_KEYS[i], DEFAULT_VALUES[i]);
        }
    }
}
