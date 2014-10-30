package JournalMain;

import Document.Entry;
import Document.Scripture;
import Document.TextFile;
import Document.XML;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * CLASS JOURNAL:
 * 
 * @author Grant
 */
public class Journal {
    private XML xmlFile;
    private List <Entry> entries;
    private TextFile textDoc;
    private Map<String, List<String>> foundScriptures;
    private Map<String, List<String>> foundTopics;
    
    // Have yet to implement
    private boolean overDueAlert;
    private final Finder finder;
    
    public Journal() throws IOException {
        foundScriptures = new HashMap<>();
        foundTopics     = new HashMap<>();
        finder          = new Finder();
        entries         = new ArrayList<>();
    }
        
    /**
     * Once a text document has been imported, use this method be
     * add the entries from the text document to the entries list. Useful
     * for saving the XML file later on.
     */
    public void addEntriesFromTextDoc () {
        String[] lines = textDoc.getText().split("\n");
        String date    = "";
        String content = "";
        
        entries = new ArrayList<>();
        
        // Add on the our entry list all entries in text document.
        for (int i = 0; i < lines.length; ++i) {
            if (lines[i].equals("-----")) {
                ++i;
                date = lines[i];
                ++i;
                content = "";
                
                while (i < lines.length && !lines[i].equals("-----")) {
                    content += lines[i] + "\n";
                    ++i;
                }
                
                --i;
                
                Entry entry = new Entry();
                entry.setDate(date);
                entry.setText(content);
                entries.add(entry);
            }
        }
    }
    
    /**
     * Displays the entries with scripture and topic references on the console.
     */
    public void displayEntriesInConsole() {
        System.out.println("Journal:\n");
        System.out.println("Scripture References:");
        
        // Scriptures References
        Iterator it = foundScriptures.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey());
            List<String> values = (List)pairs.getValue();
            
            for (String value : values) {
                System.out.println("        " + value);
            }
            it.remove();
        }
        
        System.out.println("\nTopic References:\n");
        
        // Topic References
        it = foundTopics.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey());
            List<String> values = (List)pairs.getValue();
            
            for (String value : values) {
                System.out.println("        " + value);
            }
            it.remove();
        }        
    }
    
    /**
     * Adds an entry with the given date and text.
     * @param date
     * @param text 
     */
    public void addEntry(String date, String text) {
        Entry entry = new Entry();
        entry.setDate(date);
        entry.setText(text);
    }
    
    /**
     * Create a Document object with the entries stored to prepare to save
     * to an XML file.
     * @throws ParserConfigurationException Error while parsing
     * @throws FileNotFoundException Could not find the file
     */
    public void buildXML() throws ParserConfigurationException, FileNotFoundException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
         
        Element root = doc.createElement("journal");
        doc.appendChild(root);
        
        // Add entry, scripture, topic, and content nodes for each entry.
        for(Entry entry : entries) {
            Element entryNode = doc.createElement("entry");
            entryNode.setAttribute("date", entry.getDate());
            root.appendChild(entryNode);
            
            addScriptureToXML(entry, entryNode, doc);
            addTopicToXML(entry, entryNode, doc);
            
            Element content = doc.createElement("content");
            content.setTextContent(entry.getText());
            entryNode.appendChild(content);
        }
        
        // Store document in XML object
        xmlFile.setDocument(doc);
    }
    
    /**
     * Adds a scripture node to the entry node.
     * @param entry
     * @param entryNode
     * @param doc 
     */
    private void addScriptureToXML(Entry entry, Element entryNode, Document doc) {
        for (Scripture scripture : entry.getScriptureList()) {
            Element scriptureNode = doc.createElement("scripture");
            if (!scripture.getStartVerse().equals("")) {
                scriptureNode.setAttribute("startverse", scripture.getStartVerse());
            }
            if (!scripture.getChapter().equals("")) {
                scriptureNode.setAttribute("chapter", scripture.getChapter());
            }
            if (!scripture.getBookName().equals("")) {
                scriptureNode.setAttribute("book", scripture.getBookName());
            }
            if (!scripture.getEndVerse().equals("")) {
                scriptureNode.setAttribute("endverse", scripture.getEndVerse());
            }
            entryNode.appendChild(scriptureNode);
        }
    }
    
    /**
     * Adds a topic node to the entry node.
     * @param entry
     * @param entryNode
     * @param doc 
     */
    private void addTopicToXML(Entry entry, Element entryNode, Document doc) {
        for (String topic : entry.getTopicsList()) {
            Element topicNode = doc.createElement("topic");
            topicNode.setTextContent(topic);
            entryNode.appendChild(topicNode);
        }
    }
    
    /**
     * Reads the XML file, parses it, and validates it.
     * @throws IOException Reading file went wrong.
     * @throws SAXException Error while parsing
     * @throws ParserConfigurationException Error while parsing
     */
    public void readXML() throws IOException, SAXException, ParserConfigurationException {
        xmlFile.readFile();
        entries = xmlFile.parseXML();
        validateXML();
    }
    
    /**
     * Calls the xmlFile's save method.
     * @throws TransformerException Could not transform the XML file.
     * @throws javax.xml.parsers.ParserConfigurationException XML didn't parse correctly
     * @throws java.io.FileNotFoundException XML file to save was not found.
     */
    public void saveXML() throws TransformerException, ParserConfigurationException,
            FileNotFoundException {
        buildXML();
        xmlFile.save();
    }
    
    /**
     * This will check the validity of a XML file by checking the scriptures
     * and topics and put them into a map with the entries that are associated
     * with them.
     * @throws IOException if the scripture or topic is invalid. 
     */
    public void validateXML() throws IOException {
        for (Entry entry : entries) {
            // Validate scriptures and adds the entry to the scripture map.
            for (Scripture scripture : entry.getScriptureList()) {
                if (!finder.isValidScrip(scripture)) {
                    throw new IOException("ERROR: Scripture in XML file is invalid: " +
                            scripture.getFullTitle());
                } else {
                    addEntryToScriptureMap(scripture, entry);
               }
            }
            // Validate topics and adds the entry to the topic map.
            for (String topic : entry.getTopicsList()) {
                if (!finder.isValidTopic(topic)) {
                    throw new IOException("ERROR: Topic in XML file is invalid: " + topic);
                } else {
                    addEntryToTopicMap(topic, entry);
               }
            }
        }
    }
    
    /**
     * Adds an entry to the corresponding scripture.
     * @param scripture
     * @param entry 
     */
    private void addEntryToScriptureMap(Scripture scripture, Entry entry) {
        List<String> temp = null;
                 
        // Store the entry's date with the scripture's book name
        if (foundScriptures.containsKey(scripture.getBookName())) {
            temp = foundScriptures.get(scripture.getBookName());
            temp.add(entry.getDate());
        } else {
            temp = new ArrayList();
            temp.add(entry.getDate());
        }
        foundScriptures.put(scripture.getBookName(), temp); 
    }
    
    /**
     * Adds an entry to the corresponding topic.
     * @param topic
     * @param entry 
     */
    private void addEntryToTopicMap(String topic, Entry entry) {
        List<String> temp;
                    
        // Store the entry's date with the topic found.
        if (foundTopics.containsKey(topic)) {
            temp = foundTopics.get(topic);
            temp.add(entry.getDate());
        } else {
            temp = new ArrayList();
            temp.add(entry.getDate());
        }
        foundTopics.put(topic, temp);
    }
    
    /**
     * Imports a text document into the textDoc.
     * @throws IOException if the file wasn't read correctly.
     */
    public void importTxt() throws IOException {
        textDoc.readFile();
        addEntriesFromTextDoc();
    }
    
    /**
     * Exports a text document from textDoc
     * @throws IOException if the file wasn't saved correctly.
     */
    public void exportTxt() throws IOException {
        textDoc.setEntries(entries);
        textDoc.save();
    }
    
    /**
     * Returns xmlFile
     * @return xmlFile
     */
    public XML getXmlFile() {
        return xmlFile;
    }

    /**
     * Sets xmlFile
     * @param xmlFile 
     */
    public void setXmlFile(XML xmlFile) {
        this.xmlFile = xmlFile;
    }

    /**
     * Returns the list of entries
     * @return entries
     */
    public List<Entry> getEntries() {
        return entries;
    }

    /**
     * Sets the list of entries
     * @param entries 
     */
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
    
    /**
     * Returns the textDoc
     * @return textDoc
     */
    public TextFile getTextDoc() {
        return textDoc;
    }

    /**
     * Sets the textDoc
     * @param textDoc 
     */
    public void setTextDoc(TextFile textDoc) {
        this.textDoc = textDoc;
    }

    /**
     * Returns the finder.
     * @return finder
     */
    public Finder getFinder() {
        return finder;
    }
}