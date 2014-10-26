package InsightJournalApplication;

import java.util.ArrayList;
import java.util.List;

/*******************************************************************************
* The Entry class stores the date of the entry, its content, as well as scriptures
* and topics contained in its content.
******************************************************************************/
class Entry {

    private String date;
    private List<Scripture> scriptureList = new ArrayList<>();
    private List<String> topicList = new ArrayList<>();
    private String content;
    
    // CONSTRUCTOR
    Entry() {
    }

    /***************************************************************************
     * Constructor which passes in the date and content of the entry and gets
     * the topics and scripture references found within
     **************************************************************************/
    Entry(String date, String content) {
        this.date = date;
        this.content = content;
        
        ScriptureFinder sf = new ScriptureFinder(content);
        scriptureList = sf.getScriptures();
        TopicFinder tf = new TopicFinder(content);
        topicList = tf.getTopics();
        
    }

    // GETTERS
    public String getContent() {return content;}
    public List<Scripture> getScriptureList() {return scriptureList;}
    public String getDate() {return date;}
    public List<String> getTopicList() {return topicList;}

    // SETTERS
    public void setContent(String newContent) {content = newContent;}
    public void setScriptureList(List<Scripture> newScriptureList) {
        scriptureList = newScriptureList;
    }
    public void setDate(String newDate) {date = newDate;}
    public void setTopicList(List<String> newTopicList) {
        topicList = newTopicList;
    }

    public void addTopic(String newTopic) {
        topicList.add(newTopic);
    }

    public void addScripture(Scripture newScripture) {
        scriptureList.add(newScripture);
    }

    /*******************************************************************************
    * A simple display that spits out everything in the entry.
    ******************************************************************************/
    public String display() {
        String rString = "Entry Display:\n" + "Date : " + date + "\n";
        for (Scripture scripture : scriptureList) {
            rString = rString + scripture.display();
        }
        for (String topic : topicList) {
            rString = rString + topic + " ";
        }
        rString = rString + "\n" + content + "\n";
        return rString;
    }
    
    /***************************************************************************
     * Builds a list containing all the books referenced in this particular
     * entry. 
     **************************************************************************/
    public List<String> booksContained() {
        List<String> list = new ArrayList<>();
        
        for (Scripture scrip : scriptureList){
            String bookName = scrip.getBook();
            
            if (!list.contains(bookName)) {
                list.add(bookName);
            }
        }     
        return list;        
    }

    /***************************************************************************
     * Builds a list containing all the topics mentioned in this particular
     * entry.
     **************************************************************************/
    public List<String> topicsContained() {
        List<String> list = new ArrayList<>();
        
        for (String topic : topicList){           
            if (!list.contains(topic)) {
                list.add(topic);
            }
        }   
        return list;        
    }
}
