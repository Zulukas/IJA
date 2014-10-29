package InsightJournalApplication;

import java.io.IOException;
import java.util.Properties;

/*******************************************************************************
* The properties handler class deals with the properties file.     
******************************************************************************/
public class PropertiesHandler {
    public static String scripture;
    public static String terms;
    public static String defaultFile;
    
    public void getPropValues() throws IOException {
        Properties prop = new Properties();
        //...\\cs246_milestone3\\config.properties";
        String propFileName = "config.properties";

        try {
        prop.load(getClass().getResourceAsStream(propFileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        scripture = prop.getProperty("scripture");
        terms = prop.getProperty("terms");                
        defaultFile = prop.getProperty("defaultFile");
    }
}
