package nl.breun.lastfmranker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Configuration
{
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    private static final String SYSTEM_PROPERTY_OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";
    private static final String CONFIGURATION_FILENAME = "configuration.properties";
    private static final String PROPERTY_LASTFM_API_KEY = "lastfm_api_key";

    private Properties properties = new Properties();

    private Configuration()
    {
        loadProperties();
    }

    public String getLastfmApiKey()
    {
        return properties.getProperty(PROPERTY_LASTFM_API_KEY);
    }

    private void loadProperties()
    {
        final File configurationDirectory = findConfigurationDirectory();
        final File configurationFile = new File(configurationDirectory, CONFIGURATION_FILENAME);

        try
        {
            Reader reader = new FileReader(configurationFile);
            try
            {
                properties.load(reader);
            }
            finally
            {
                reader.close();
            }
        }
        catch (IOException ex)
        {
            LOGGER.log(Level.SEVERE, "Error reading configuration from " + configurationFile, ex);
        }
    }

    private File findConfigurationDirectory()
    {
        File configurationDirectory = new File(".");

        final String openshiftDataDirectory = System.getenv(SYSTEM_PROPERTY_OPENSHIFT_DATA_DIR);
        if (openshiftDataDirectory != null)
        {
            configurationDirectory = new File(openshiftDataDirectory);
        }

        return configurationDirectory;
    }

    public static Configuration getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        public static final Configuration INSTANCE = new Configuration();
    }
}
