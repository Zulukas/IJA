package InsightJournalApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*******************************************************************************
 * The Journal class serves as the primary class of the program. It stores the 
 map containing all the journal entries keyed by date, a list searchable topics
 and a map of all the books fileIn the Standard Works combine with their respective
 maximum chapters.
 ******************************************************************************/
public class Journal {

    private Map<String, Entry> entries = new TreeMap<>();
    private String inputFileName;
    private String outputTxtFileName;
    private String outputXmlFileName;
    //private final GUI gui = new GUI();
    public static List<String> booksToFind = new ArrayList<>();
    public static Map<String, List<String>> termsToFind = new HashMap<>();
    
    /***************************************************************************
     * Main insures that a filename of a journal is passed fileIn from the command 
     * line and instantiates a new copy of journal.
     **************************************************************************/
    private static void main(String[] args) throws FileNotFoundException {
    
        if (args.length == 3) {
            
            Journal j = new Journal();

        } else {
            System.out.println("Please provide a filename.");
        }
    }
    
    /***************************************************************************
     * Journal constructor takes a filename as a parameter and handles all the 
     * function calls needed to read fileIn properties, read fileIn the XML journal, and
     * to display scriptures and topics found.
     **************************************************************************/
    public Journal() {       
        try {
            PropertiesHandler prop = new PropertiesHandler();
            prop.getPropValues();
            
            readScriptureFile(PropertiesHandler.scripture);
            readTermsFile(PropertiesHandler.terms);
            //TopicFinder tf = new TopicFinder(termsToFind);
        } catch (Exception ex) {
            Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /***************************************************************************
     * This constructor allows you to create a journal object by passing in a 
     * filename 
     **************************************************************************/
    public Journal(String filename) {       
        try {            
            PropertiesHandler prop = new PropertiesHandler();
            prop.getPropValues();
            
            readScriptureFile(PropertiesHandler.scripture);
            readTermsFile(PropertiesHandler.terms);
            //TopicFinder tf = new TopicFinder(termsToFind);
                        
            loadFile(filename);
        } catch (Exception ex) {
            Logger.getLogger(Journal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /***************************************************************************
     * Load the file being passed in through the parameters
     **************************************************************************/
    public void loadFile(String fileName) {
        Importer readIn = new Importer(fileName, termsToFind);
        setEntries(readIn.readFile());
    }
    
    /***************************************************************************
     * Save the file from the parameter input.
     **************************************************************************/
    public void saveFiles(String input) {
        try {
            String[] split = input.split("\\.");
            
            if (split[1].equals("xml")) {
                writeXMLDocument(input);
            } else if (split[1].equals("txt")) {
                writeTextDocument(input);
            } else {
                System.err.println("INVALID FILE EXTENTION - MUST BE .XML or .TXT");
            }
        } catch (Exception e) {
            System.err.println("Unable to save file!");
        }
    }
    
    public void addToEntries(String date, Entry input) {
        getEntries().put(date, input);
    }
    
    /***************************************************************************
    * Creates a list of Books from the fileIn the properties file.
    ***************************************************************************/
    private void readScriptureFile(String scriptureFile) throws IOException {
        
        try {
            FileReader fileIn = new FileReader(scriptureFile);
            BufferedReader reader = new BufferedReader(fileIn);

            String currentLine = reader.readLine();

            while (currentLine != null) {
                String book = currentLine.split(":")[0].trim(); // discarding max chapter
                //System.out.println(book);
                booksToFind.add(book);
                currentLine = reader.readLine();

            }

            //System.out.println(booksToFind);
            
            ScriptureFinder sf = new ScriptureFinder(booksToFind);
            
            reader.close();             
        } catch (Exception e) {
            System.out.println("Unable to open \"" + scriptureFile + "\"");
        }
    }

    /***************************************************************************
    * Builds the map of topics from the file fileIn the properties file.
    ***************************************************************************/
    private void readTermsFile(String termsFile) throws IOException {
        try {
            FileReader fileIn = new FileReader(termsFile);
            BufferedReader reader = new BufferedReader(fileIn);
            String currentLine = reader.readLine();

            List<String> termLines = new ArrayList<>();
            List<String> synonyms = new ArrayList<>();

            while (currentLine != null) {
                termLines.add(currentLine);
                currentLine = reader.readLine();
            }

            for (String termLine : termLines) {
                String[] termParts = termLine.split(":");
                String termKey = termParts[0];
                String[] temps = termParts[1].split(",");

                for (String temp : temps) {
                    synonyms.add(temp);
                }

                termsToFind.put(termKey, synonyms);
                synonyms = new ArrayList<>();
            }
            reader.close(); 
            
            System.out.println("Making a topic finder!");
            TopicFinder tf = new TopicFinder(termsToFind);
            System.out.println("Done with topic finder!");
        } catch (Exception e) {
            System.err.println("Unable to open \"" + termsFile + "\"");
        }
    }
    
    /***************************************************************************
     * Write the XML document to the filename being passed in through the 
     * parameter.
     **************************************************************************/
    private void writeXMLDocument(String filename) throws Exception{
        System.out.println("Building document");
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document doc = builder.newDocument();
        
        // Root element
        Element rootElement = doc.createElement("journal");
        doc.appendChild(rootElement);
        
        List<String> entryKeys = new ArrayList<>(getEntries().keySet());
        for (String entryKey : entryKeys) {
            // The entry we are working with
            Entry currentEntry = getEntries().get(entryKey);
            
            // Entry element
            Element entryEle = doc.createElement("entry");
            rootElement.appendChild(entryEle);
            
            // Add Entry Date
            Attr attr = doc.createAttribute("date");
            attr.setValue(entryKey);
            entryEle.setAttributeNode(attr);
            
            // Scripture Time
            List<Scripture> scriptures = currentEntry.getScriptureList();
            for (Scripture scripture : scriptures) {
                // Scripture element
                Element scriptureEle = doc.createElement("scripture");
                entryEle.appendChild(scriptureEle);
                
                // Add Scripture's Book
                Attr book = doc.createAttribute("book");
                book.setValue(scripture.getBook());
                scriptureEle.setAttributeNode(book);
                // Add Scripture's Chapter
                Attr chapter = doc.createAttribute("chapter");
                chapter.setValue(Integer.toString(scripture.getChapter()));
                scriptureEle.setAttributeNode(chapter);
                // Add Scripture's Book
                Attr startverse = doc.createAttribute("startverse");
                startverse.setValue(Integer.toString(scripture.getStartVerse()));
                scriptureEle.setAttributeNode(startverse);
                // Add Scripture's Book
                Attr endverse = doc.createAttribute("endverse");
                endverse.setValue(Integer.toString(scripture.getEndVerse()));
                scriptureEle.setAttributeNode(endverse);
            }
            // Topic time
            List<String> topics = currentEntry.getTopicList();
            for (String topic : topics) {
                // Topic element
                Element topicEle = doc.createElement("topic");
		topicEle.appendChild(doc.createTextNode(topic));
                entryEle.appendChild(topicEle);
            }
            
            // Content
            Element contentEle = doc.createElement("content");
            contentEle.appendChild(doc.createTextNode(currentEntry.getContent()));
            entryEle.appendChild(contentEle);
        }
        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filename));
        
        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);
        
        transformer.transform(source, result);
        
        System.out.println("File saved!");
    }

    /***************************************************************************
     * writeTextDocument takes the entries stored in our tree and converts it to
     * a simple .txt file.  Does not save references to topics or scriptures
     * 
     * Author: Kevin Andres
     **************************************************************************/
    private void writeTextDocument(String fileName) {
        BufferedWriter bw;
        
        try {
            System.out.println("Writing entries to text file: " + fileName);
            File fout = new File(fileName);
            bw = new BufferedWriter(new FileWriter(fout));
            
            int counter = 0;
            
            for (Map.Entry<String, Entry> entry : getEntries().entrySet()) {
                String key = entry.getKey();
                Entry value = entry.getValue();
                
                //System.out.println(key + ": " + value.getContent());
                
                if (counter == 0) {
                    bw.write("-----\n");
                    counter++;
                } else
                    bw.write("\n-----\n");
                bw.write(key + "\n\n");
                bw.write(value.getContent() + "\n");
                
            }
            
            System.out.println("Successfully wrote the text file!");
            
            bw.close();
        } catch (Exception e) {
            System.err.println("Unable to write \"WrittenJournal.txt\"");
        }
    }

    /**
     * @return the entries
     */
    public Map<String, Entry> getEntries() {
        return entries;
    }

    /**
     * @param entries the entries to set
     */
    public void setEntries(Map<String, Entry> entries) {
        this.entries = entries;
    }

    /**
     * @param outputXmlFileName the outputXmlFileName to set
     */
    public void setOutputXmlFileName(String outputXmlFileName) {
        this.outputXmlFileName = outputXmlFileName;
    }
}