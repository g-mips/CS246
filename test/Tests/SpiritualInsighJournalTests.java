/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import Document.Entry;
import Document.Scripture;
import Document.XML;
import JournalMain.Finder;
import JournalMain.Journal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.testng.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author Grant
 */
public class SpiritualInsighJournalTests {
    
    public SpiritualInsighJournalTests() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void testFinderScriptureValidity() {
        try {
            Finder finder = new Finder();
            Scripture scripture = new Scripture("Genesis", "50");
            
            Assert.assertTrue(finder.isValidScrip(scripture));
            
            scripture = new Scripture("Enos", "2");
            
            Assert.assertFalse(finder.isValidScrip(scripture));
            
            scripture = new Scripture("Jacob", "20");
            
            Assert.assertFalse(finder.isValidScrip(scripture));
        } catch (IOException ex) {
            Logger.getLogger(SpiritualInsighJournalTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testFinderTopicValidity() {
        try {
            Finder finder = new Finder();
            
            String topic = "Faith";
            
            Assert.assertTrue(finder.isValidTopic(topic));
            
            topic = "Repentance";
            
            Assert.assertTrue(finder.isValidTopic(topic));
            
            topic = "love";
            
            Assert.assertFalse(finder.isValidTopic(topic));
        } catch (IOException ex) {
            Logger.getLogger(SpiritualInsighJournalTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testFinderParseScrips() {
        try {
            Finder finder = new Finder();
            Entry entry = new Entry();
            
            entry.setText("Hi my name is Grant Merrill");
            finder.parseForScriptures(entry);
            Assert.assertEquals(entry.getScriptureList().isEmpty(), true);
            
            entry.removeAllScriptures();
            entry.setText("hi 2 Nephi 22:3 and Enos 9 and gen 50");
            finder.parseForScriptures(entry);
            Assert.assertEquals(entry.getScriptureList().size(), 2);
        } catch (IOException ex) {
            Logger.getLogger(SpiritualInsighJournalTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testFinderParseTopics() {
        try {
            Finder finder = new Finder();
            Entry entry = new Entry();
            
            entry.setText("stiffnecked and now humility and if you humble");
            finder.parseForTopics(entry);
            Assert.assertEquals(entry.getTopicsList().size(), 2);
            
            entry.removeAllTopics();
            entry.setText("holy ghost, covenant, Jesus, hope");
            finder.parseForTopics(entry);
            Assert.assertEquals(entry.getTopicsList().size(), 4);
            
            entry.removeAllTopics();
            entry.setText("");
            finder.parseForTopics(entry);
            Assert.assertEquals(entry.getTopicsList().size(), 0);
        } catch (IOException ex) {
            Logger.getLogger(SpiritualInsighJournalTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testXMLSavingAndReading() {
        try {
            Journal journal = new Journal();
            XML file = new XML(new File("C:\\Users\\Public\\test.xml"));
            
            journal.setXmlFile(file);
            List<Entry> entries = new ArrayList<>();
            
            for (int i = 0; i < 10; ++i) {
                Entry entry = new Entry();
                entry.addScripture(new Scripture("Genesis", "50"));
                entry.addTopic("Faith");
                entry.setDate("2014-10-30");
                entries.add(entry);
            }
            
            journal.setEntries(entries);
            journal.saveXML();
            journal.readXML();
            Assert.assertEquals(journal.getXmlFile().getDocument().getFirstChild().getNodeName(),"journal");
            Assert.assertEquals(journal.getXmlFile().getDocument().getFirstChild().getChildNodes().
                    item(0).getChildNodes().item(0).getAttributes().getNamedItem("book").getNodeValue(), "Genesis");
        } catch (IOException | TransformerException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(SpiritualInsighJournalTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
