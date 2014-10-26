package InsightJournalApplication;

import java.io.IOException;
import java.util.Properties;

/*******************************************************************************
* The properties handler class deals with the properties file.     
******************************************************************************/
public class PropertiesHandler {
    public static String scripture;
    public static String terms;
    public static String journal;
    
    public void getPropValues() throws IOException {
        Properties prop = new Properties();
        //...\\cs246_milestone3\\config.properties";
        String propFileName = "config.properties";

        try {
        //InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        prop.load(getClass().getResourceAsStream(propFileName));
        //        if (inputStream == null) {
        //            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        //        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // get the property value and print it out
        scripture = prop.getProperty("scripture");
        terms = prop.getProperty("terms");        
        journal = prop.getProperty("journal");
    }
}
