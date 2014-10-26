/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InsightJournalApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kevin
 */
public class TopicFinder {

    /**
     * @return the topicsMap
     */
    public static Map<String, List<String>> getTopicsMap() {
        return topicsMap;
    }
    private List<String> topics = new ArrayList<>();
    private String text;
    private static Map<String, List<String>> topicsMap;
    
    final private String error = "CLASS: TopicFinder: Please instantiate a TopicFinder object with \"TopicFinder(Map<String>, List<String>> mapObjectName)\" in order to use this class!";
    
    /***************************************************************************
     * Default constructor for TopicFinder
     **************************************************************************/
    public TopicFinder() { 
        topics = null;
        text = "";
        
        if (topicsMap == null) {
            System.out.println(error);
        }
    }
    
    /***************************************************************************
     * A constructor which sets the static topic map.
     **************************************************************************/
    public TopicFinder (Map<String, List<String>> topicsMap) {
        this.topicsMap = topicsMap;
    }
    
    /***************************************************************************
     * A constructor which allows you to pass in text and build the topic list
     **************************************************************************/
    public TopicFinder(String text) {
        if (topicsMap == null) {
            System.out.println(error);
        } else {                    
            this.text = text;
            findTopics();
        }
    }
    
    /***************************************************************************
     * finds the topics in a string of text and sets it to the 
     **************************************************************************/
    private void findTopics() {
        setText(getText().replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " "));
        setText(text.toLowerCase());

        for (Map.Entry<String, List<String>> entry : getTopicsMap().entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            
            for (String topic : value) {
                if (text.contains(topic)) {
                    if (!topics.contains(key)) {
                        topics.add(key);
                    }
                }
            }
        }

        System.out.println("Topics found:\n" + topics);
    }
    
    

    /**
     * @return the topics
     */
    public List<String> getTopics() {
        return topics;
    }

    /**
     * @param topics the topics to set
     */
    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }
}
