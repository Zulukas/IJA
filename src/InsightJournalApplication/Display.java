package InsightJournalApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Bryce
 */
public class Display {
    Map<String, Entry> entries = new TreeMap<>();
    
    
    public Display(Map<String, Entry> entries) {
        this.entries = entries;
    }
    /***************************************************************************
    * Used to display contents of entries map. Currently not used
    ***************************************************************************/
    public final void display() {
        System.out.println("Journal Display:");
        List<String> keys = new ArrayList<>(entries.keySet());
        
        for (String key : keys) {
            System.out.println(entries.get(key).display() + "\n");
        }
    }
    
    
    /***************************************************************************
     * DisplayBookReferences goes through the map of entries and collects dates
     * of entries that contain each book in the Standard Works
     **************************************************************************/
    public final void displayBookReferences() {
        //will contain <bookName, list of dates >
        HashMap<String, List<String>> bookMap = new HashMap<>();  

        System.out.println("Scripture References:");

        for (Map.Entry<String, Entry> entry : entries.entrySet()) {
            String date = entry.getKey();                           
            Entry temp = entry.getValue();                          

            List<String> booksContained = temp.booksContained();  

            for (String bookName : booksContained) {              
                List<String> dates;                               

                if (bookMap.get(bookName) == null) {
                    dates = new ArrayList<>();
                } else {
                    dates = bookMap.get(bookName);
                }
                // insure not duplicate dates
                if (!dates.contains(date)) {                        
                    dates.add(date);                                
                }

                bookMap.put(bookName, dates);
            }
        }

        displayMap(bookMap);
    }

    /***************************************************************************
     * DisplayTopicReferences goes through the map of entries and collects dates
     * of entries that contain each topic in the topics map.
     **************************************************************************/
    public final void displayTopicReferences() {
        HashMap<String, List<String>> topicMap = new HashMap<>();

        System.out.println("Topic References:");

        for (Map.Entry<String, Entry> entry : entries.entrySet()) {
            String date = entry.getKey();
            Entry temp = entry.getValue();

            List<String> topicsContained = temp.topicsContained();

            for (String topic : topicsContained) {
                List<String> dates;

                if (topicMap.get(topic) == null) {
                    dates = new ArrayList<>();
                } else {
                    dates = topicMap.get(topic);
                }
                
                if (!dates.contains(date)) {
                    dates.add(date);
                }
                
                topicMap.put(topic, dates);
            }
        }
        
        displayMap(topicMap);
    }
    
    /***************************************************************************
     * DisplayMap is used to sort and print out contents of a map
     **************************************************************************/
    private final void displayMap(HashMap<String, List<String>> input) {
        ArrayList<String> keys = new ArrayList<>(input.keySet());   
        Collections.sort(keys);                                     

        for (String key : keys) {                                  
            System.out.println(key + ":");

            List<String> dates = input.get(key);                    
            Collections.sort(dates);                               

            for (String date : dates) {                          
                System.out.println("\t" + date);
            }
        }
    }    
}
    

