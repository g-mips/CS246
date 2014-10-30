package Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Stores data about an XML file and how to save and read and parse a XML file.
 * @author Grant
 */
public class XML {
    private Document document;
    private File path;
    
    /**
     * Constructs a XML file from the File given.
     * @param path 
     */
    public XML(File path) {
        this.path = path;
    }
    
    /**
     * This will write the information in the text variable to a XML document
     * in the specific format specified.
     * @throws javax.xml.transform.TransformerException If the transformation process
     *                                                  fails.
     */
    public void save() throws TransformerException {
        Source source = new DOMSource(document);
        Result result = new StreamResult(path);
            
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(source, result);
    }
    
    /**
     * This knows how to read a XML document and store the information properly.
     * @return true if read correctly.
     * @throws org.xml.sax.SAXException parsing error 
     * @throws java.io.IOException building error
     * @throws javax.xml.parsers.ParserConfigurationException parsing error 
     */
    public boolean readFile() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        document = db.parse(path.getAbsolutePath());
        return true;
    }
    
    /**
     * This is used to be able to gather all the necessary data from the xml
     * file provided and put them in entries.
     * @return returns the list of entries found in the xml file.
     */
    public List parseXML() {
        // Get node list of all the entry nodes within the document.
        List<Entry> entryList = new ArrayList<>();
        document.getDocumentElement().normalize();
        Node journal = document.getDocumentElement();
        NodeList entryNodes = document.getElementsByTagName("entry");
        
        // Gather the information from each entry node and add them to the list.
        for (int i = 0; i < entryNodes.getLength(); ++i) {
            Entry newEntry = new Entry();
            
            NamedNodeMap attributes = entryNodes.item(i).getAttributes();
            
            if (attributes != null && attributes.getNamedItem("date") != null) {
                newEntry.setDate(attributes.getNamedItem("date").getNodeValue());
            }
            
            NodeList nodes = entryNodes.item(i).getChildNodes();
            newEntry = parseNodes(nodes, newEntry);
            entryList.add(newEntry);
        }
        
        return entryList;
    }
    
    /**
     * PARSE_NODES:
     *     This will parse the nodes that are given to find the scripture, topic,
     *   and content nodes.
     * @param nodes the NodeList that will be looped through.
     * @param newEntry the entry that will be edited
     * @return return the newEntry
     */
    private Entry parseNodes(NodeList nodes, Entry newEntry) {
        String content = "";
        
        // Loop through all the nodes and find scriptures, topics, and content
        for (int j = 0; j < nodes.getLength(); ++j) {
            switch (nodes.item(j).getNodeName()) {
                case "scripture":
                    NamedNodeMap attributes = nodes.item(j).getAttributes();
                    Scripture scripture = createScriptureFromNode(attributes);
                    newEntry.addScripture(scripture);
                    break;
                case "topic":
                    newEntry.addTopic(nodes.item(j).getTextContent());
                    break;
                case "content":
                    content = nodes.item(j).getTextContent().trim(); 
                    
                    // Wraps the text nicely
                    int length = content.length() / 120;
                    for (int len = 0; len <= length; ++len) {
                        int index = content.indexOf(" ", (120*(len+1)));
                        if (index >= 0) {
                            content = content.substring(0, index) + "\n" + content.substring(index+1, content.length());
                        }
                    }
                    
                    newEntry.setText(content);
                    break;
            }
        }
        
        return newEntry;
    }
    
    /**
     * From the node given, uses the attributes of that node to create
     * a scripture node.
     * @param attributes
     * @return 
     */
    private Scripture createScriptureFromNode(NamedNodeMap attributes) {
        String book       = "";
        String chapter    = "";
        String startVerse = "";
        String endVerse   = "";
                    
        if (attributes != null) {
            if (attributes.getNamedItem("book") != null) {
                book = attributes.getNamedItem("book").getNodeValue();
            } 
            if (attributes.getNamedItem("chapter") != null) {
                chapter = attributes.getNamedItem("chapter").getNodeValue();
            } 
            if (attributes.getNamedItem("startverse") != null) {
                startVerse = attributes.getNamedItem("startverse").getNodeValue();
            } 
            if (attributes.getNamedItem("endverse") != null) {
                endVerse = attributes.getNamedItem("endverse").getNodeValue();
            }
        }
                
        Scripture scripture = new Scripture(book, chapter, startVerse, endVerse);
        
        return scripture;
    }
    
    /**
     * Returns the document that represents the XML file
     * @return document
     */
    public Document getDocument() {
        return document;
    }
    
    /**
     * Sets the document that represents the XML file
     * @param doc the new document
     */
    public void setDocument(Document doc) {
        this.document = doc;
    }
}
