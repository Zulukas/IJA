package InsightJournalApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Bryce
 */
public class IOFileIn {
    Map<String, Entry> map = new TreeMap<>();
    private String filename = "";
    Map<String, List<String>> termsToFind = new HashMap<>();

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }
    public IOFileIn(String file, Map<String, List<String>> terms) {
        this.filename = file;
        this.termsToFind = terms;
    }  
    
    public Map<String, Entry> readFile(){
        
        String[] parts = filename.split("\\.");
        
        if (parts[1].equals("txt")) {
            
            System.out.println("Got a txt file!");
            //readTextInput(filename);
            textReader(filename);
            
            
        } else if (parts[1].equals("xml")) {
            
            
            System.out.println("Got a xml file!");
            readInputFile(filename);
            
        }   else {
            
            System.out.println("ERROR: Invalid file extension.  Please locate valid .xml or .txt file.");
        }
        
        return map;
    }

/***************************************************************************
    * ReadInputFile takes fileIn a filename and sets up the document used to parse
 the XML file.
    ***************************************************************************/
    private void readInputFile(String filename) {
        try {
            File xmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            System.out.println("Loading file \"" + filename + "\"\n");

            Element rootElement = doc.getDocumentElement();
            map = parseJournal(rootElement);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Sorry, but your file was not able to be parsed.");
        }
    }
    
    /***************************************************************************
    * ParseJournal is responsible adding entries to the entry map.
    ***************************************************************************/
    private Map<String, Entry> parseJournal(Element rootElement) {
        Map<String, Entry> entryMap = new HashMap<>();
        NodeList journalNodes = rootElement.getChildNodes();
        
        for (int i = 0; i < journalNodes.getLength(); i++) {
            Node journalNode = journalNodes.item(i);
        
            if (journalNode.getNodeType() == Node.ELEMENT_NODE) {
                Element journalElement = (Element) journalNode;
            
                if (journalElement.getNodeName().equals("entry")) {
                    Entry e = parseEntry(journalElement);
                    entryMap.put(e.getDate(), e);
                } // end entry if
            } // end element if
        } // end for
        
        return entryMap;
    }
    
    /***************************************************************************
    * parseEntry builds entries to be put onto the TreeMap.
    * It locates and data on scriptures and topics contained fileIn the the entry of
 the XML document and treats them accordingly.
    ***************************************************************************/
    private Entry parseEntry(Element rootEntryElement) {
        Entry rEntry = new Entry();
        rEntry.setDate(rootEntryElement.getAttribute("date"));
        NodeList entryNodes = rootEntryElement.getChildNodes();
        
        for (int i = 0; i < entryNodes.getLength(); i++) {
            // Changing all children to child by child basis
            Node entryNode = entryNodes.item(i);
        
            if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
                Element entryElement = (Element) entryNode;
            
                switch (entryElement.getNodeName()) {
                    case "scripture":
                        rEntry.addScripture(parseScripture(entryElement));
                        break;
                    case "topic":
                        String tempTopic = entryElement.getTextContent();
                                                
                        //BEGIN TOPIC CONVERTER
                        for (Map.Entry<String, List<String>> topic : termsToFind.entrySet()) {
                           String masterTopic = topic.getKey();
                            List<String> slaveTopic = topic.getValue();
                            
                            if (slaveTopic.contains(tempTopic.toLowerCase())) {                                
                                rEntry.addTopic(masterTopic);
                           }
                        }                   
                        //END TOPIC CONVERTER                        
                        break;
                    case "content":
                        rEntry.setContent(entryElement.getTextContent().trim().replaceAll("\\n\\s+", "\n"));
                        break;
                }
            }
        }
        return rEntry;
    }
    
    /***************************************************************************
    * ParseScripture fills out a scripture object to include fileIn an entry.
    ***************************************************************************/
    private Scripture parseScripture(Element rootScriptureElement) {
        Scripture rScripture = new Scripture();
        NamedNodeMap attributes = rootScriptureElement.getAttributes();
        
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attributeAtI = attributes.item(i);
            
            switch (attributeAtI.getNodeName()) {
                case "book":
                    rScripture.setBook(attributeAtI.getNodeValue());
                    break;
                case "chapter":
                    rScripture.setChapter(Integer.parseInt(attributeAtI.getNodeValue()));
                    break;
                case "startverse":
                    rScripture.setStartVerse(Integer.parseInt(attributeAtI.getNodeValue()));
                    break;
                case "endverse":
                    rScripture.setEndVerse(Integer.parseInt(attributeAtI.getNodeValue()));
                    break;
            }
        }
        return rScripture;
    }
    
    /***************************************************************************
     * the .txt file extension handler.  
     * 
     * Author: Kevin Andres
     **************************************************************************/
    public void textReader(String file) {
        readTextInput(file);
        //displayMap(map);
    }
    
    /***************************************************************************
     * Simple map display function
     * 
     * Author: Kevin Andres
     **************************************************************************/
    private void displayMap(Map<String, Entry> entryMap) {
        for (Map.Entry<String, Entry> entry : entryMap.entrySet()) {
            String key = entry.getKey();
            Entry value = entry.getValue();
            
            System.out.println(key + ": " + value.getContent());
        }
    }
    
    /***************************************************************************
     * readTextInput takes in a .txt file and builds entries from it.  It calls
     * the scripture and topic finders too!
     * 
     * Author: Kevin Andres
     **************************************************************************/
    private void readTextInput (String fileName) {
        try {
            Entry newEntry;
            
            FileReader fin = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fin);
            
            String currentLine = br.readLine();
            String entryText = "", entryDate = "";
            
            while (currentLine != null) {
                if (currentLine.equals("-----")) {                    
                    entryText = "";                    
                    currentLine = br.readLine();
                    entryDate = currentLine;
                } else {
                    currentLine = br.readLine();
                    
                    if (currentLine == null || currentLine.equals("-----")) {
                        newEntry = new Entry(entryDate, entryText);
                        map.put(entryDate, newEntry);                        
                    } else {
                        if (entryText.equals("")) {
                            entryText += currentLine;
                        } else {
                            entryText += " " + currentLine;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Unable to parse \"" + fileName + "\"");
            e.printStackTrace();
        }
    }
}
