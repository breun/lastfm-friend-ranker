package nl.breun.lastfmranker;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration
{
    private static class SingletonHolder
    {
        public static final Configuration INSTANCE = new Configuration();
    }

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    private static final String CONFIGURATION_FILE = "configuration.properties";

    private static final String PROPERTY_API_KEY = "api_key";

    private Properties properties = new Properties();

    private Configuration()
    {
        loadDataFromPropertiesFile();
    }

    public String getLastfmApiKey()
    {
        return properties.getProperty(PROPERTY_API_KEY);
    }

    /**
     * Reloads the configuration properties from the configuration file.
     */
    public void refresh()
    {
        loadDataFromPropertiesFile();
    }

    private void loadDataFromPropertiesFile()
    {
        try
        {
            Reader reader = new FileReader(CONFIGURATION_FILE);
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
            LOGGER.log(Level.SEVERE, "Error reading api_key from " + CONFIGURATION_FILE, ex);
        }
    }

    public static Configuration getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
}
