package JournalMain;

import Document.Entry;
import Document.Scripture;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is used to find scriptures and topics within an Entry. It also
 * has tests to see if the String given to it is a valid scripture or topic.
 * @author Grant
 */
public class Finder {
    private List<Scripture> scriptures = new ArrayList<>();
    private Map<String, List<String>> topics = new HashMap<>();
    private Map<String, List<String>> alternativeBookNames = new HashMap<>();
    
    private boolean validScrip;
    
    private final File books;
    private final File terms;
    private final File bookNames;
    
    private static final String PROPERTIES_FILE = "/Resources/journal.properties";
    private static final String BOOKS_TXT = "books";
    private static final String TERMS_TXT = "terms";
    private static final String BOOK_NAMES_TXT = "bookNames";
   
    /**
     * Constructs a finder with instructions found in a properties file.
     * @throws IOException if any reading errors occurred
     */
    public Finder() throws IOException {
        Properties propertyFile = new Properties();
        propertyFile.load(getClass().getResourceAsStream(PROPERTIES_FILE));
        
        books = new File(propertyFile.getProperty(BOOKS_TXT));
        terms = new File(propertyFile.getProperty(TERMS_TXT));
        bookNames = new File(propertyFile.getProperty(BOOK_NAMES_TXT));
        
        readScripturesFile();
        readFileToMap(terms, "topics");
        readFileToMap(bookNames, "bookNames");
    }
    
    /**
     * This will determine whether or not the String given is a valid scripture.
     * @param scrip the scripture to check if valid or not
     * @return true or false
     */
    public boolean isValidScrip(Scripture scrip) {
        validScrip = false;
        
        // Scripture is found, it's a valid scripture.
        for (Scripture scripture : scriptures) {
            if (scripture.getBookName().equals(scrip.getBookName())) {
                if (Integer.parseInt(scripture.getChapter()) >= 
                    Integer.parseInt(scrip.getChapter())) {
                    validScrip = true;
                    break;
                }
            }
        }
        
        return validScrip;
    }
    
    /**
     * This will determine whether or not the String given is a valid topic.
     * @param topic the topic at question
     * @return true or false
     */
    public boolean isValidTopic(String topic) {
        return topics.containsKey(topic);
    }
    
    /**
     * This is used to read in the file that contains the books and the
     * chapter limit in each book.
     * @return true or false whether or not it was successful with reading in
     *         the file.
     * @throws FileNotFoundException reading error
     * @throws IOException reading error
     */
    private boolean readScripturesFile() throws FileNotFoundException, IOException {
        if (books.canRead()) {
            BufferedReader bf = new BufferedReader(new FileReader(books));
            String line = "";
            while ((line = bf.readLine()) != null) {
                scriptures.add(new Scripture(line.substring(0, line.indexOf(":")),
                                             line.substring(line.indexOf(":") + 1, line.length())));
            }
            
            return true;
        }
        
        return false;
    }

    /**
     * This is used to read in the file that contains the topics.
     * @return true or false whether or not it was successful with reading in
     *         the file.
     * @throws FileNotFoundException reading error
     * @throws IOException reading error
     */
    private boolean readFileToMap(File file, String type) throws FileNotFoundException, IOException {
        if (file.canRead()) {
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = "";
            String key = "";
            String[] values;
            
            while ((line = bf.readLine()) != null) {
                List<String> valuesList = new ArrayList<>();
                
                key = line.substring(0, line.indexOf(":"));
                String temp = line.substring(line.indexOf(":")+1, line.length());
                values = temp.split(",");
                
                // Find all the variations of the topic!
                for (int i = 0; i < values.length; ++i) {
                    valuesList.add(values[i]);
                }
                
                // To which map are we storing this data?
                switch (type) {
                    case "topics":
                        topics.put(key, valuesList);
                        break;
                    case "bookNames":
                        alternativeBookNames.put(key, valuesList);
                        break;
                }
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Parse the entry for scriptures
     * @param entry entry to be parsed
     */
    public void parseForScriptures(Entry entry) {
        entry.removeAllScriptures();
        String patterns = "";
        for (Map.Entry pairs : alternativeBookNames.entrySet()) {
            List<String> values = (List<String>)pairs.getValue();
            
            // Pattern to look out for on each item.
            for (String value : values) {
                patterns += value + " chapter \\d+:\\d+|" +
                        value + " section \\d+:\\d+|" +
                        value + " chapter \\d+|" +
                        value + " section \\d+|" +
                        value + " \\d+:\\d+|" +
                        value + " \\d+|";
            }
        }
        
        // Finalize the patterns and text for compiling and matching.
        patterns += "v.\\d+|v. \\d+";
        String lowerCaseText = entry.getText().toLowerCase();
        
        // Put the patterns in a Pattern object and see if there's a match.
        Pattern pattern = Pattern.compile(patterns);
        Matcher match = pattern.matcher(lowerCaseText);

        // If the user wanted to display the results, display all the matches.
        while (match.find()) {
            String scripture = match.group();
            String substring1 = "chapter ";
            String substring2 = "section ";
            String replacement = "";
            String newScriptRef = null;

            // Replace chapter or section from the scripture reference.
            newScriptRef = scripture.replace(substring1, replacement);
            newScriptRef = newScriptRef.replace(substring2, replacement);
            
            String book       = "";
            String chapter    = "";
            String startVerse = "";
            String endVerse   = "";
            
            boolean bookTime       = true;
            boolean chapterTime    = true;
            boolean startVerseTime = true;
            boolean endVerseTime   = true;
            
            int i = 0;
            
            // Fill strings with book name, chapter, and verses..
            while (i < newScriptRef.length() && bookTime) {
                if (!Character.isDigit(newScriptRef.charAt(i)) || i == 0) {
                    book += newScriptRef.charAt(i);
                    ++i;
                } else {
                   bookTime = false;
                   book = book.trim();
                }
            }
            
            for (; i < newScriptRef.length() && chapterTime; ++i) {
                if (newScriptRef.charAt(i) != ':') {
                    chapter += newScriptRef.charAt(i);
                } else {
                    chapterTime = false;
                    chapter = chapter.trim();
                }
            }
            
            for (; i < newScriptRef.length() && startVerseTime; ++i) {
                if (newScriptRef.charAt(i) != ',' || newScriptRef.charAt(i) != '-') {
                    startVerse += newScriptRef.charAt(i);
                } else {
                    startVerseTime = false;
                    startVerse = startVerse.trim();
                }
            }
            
            for (; i < newScriptRef.length() && endVerseTime; ++i) {
                if (endVerseTime) {
                    endVerse += newScriptRef.charAt(i);
                }
            }
            
            // Trim off extra white space.
            endVerse = endVerse.trim();
            
            // Check for stray verses and rename the book name to whatever book was used last.
            if (book.equals("v.")) {
                if (!entry.getScriptureList().isEmpty()) {
                    book = entry.getScriptureAt(entry.getScriptureList().size()-1).getBookName();
                }
            }
        
            // Find the "true" name of the book.
            for (Map.Entry pairs : alternativeBookNames.entrySet()) {
                List<String> values = (List<String>)pairs.getValue();
                String key = (String)pairs.getKey();
                
                for (String value : values) {
                    if (value.equals(book)) {
                        book = key;
                        break;
                    }
                }
            }
            
            Scripture scriptureToAdd = new Scripture(book, chapter, startVerse, endVerse);
            
            // Is this a valid scripture and is there this scripture already in the entry.
            if (isValidScrip(scriptureToAdd) && !hasScripture(entry, scriptureToAdd)) {
                entry.addScripture(scriptureToAdd);
            }
        }
    }
    
    /**
     * Parse the entry for topics
     * @param entry entry to parse
     */
    public void parseForTopics(Entry entry) {
        entry.removeAllTopics();
        String patterns = "";
        for (Map.Entry pairs : topics.entrySet()) {
            List<String> values = (List<String>)pairs.getValue();
            
            // Pattern to look out for on each item.
            for (String value : values) {
                patterns += value + "|";
            }
        }
        
        Pattern pattern = Pattern.compile(patterns);
        Matcher match = pattern.matcher(entry.getText());
        
        while (match.find()) {
            for (Map.Entry pairs : topics.entrySet()) {
                boolean found = false;
                List<String> values = (List<String>)pairs.getValue();
                
                for (String value : values) {
                    if (value.equals(match.group())) {
                        if (!hasTopic(entry, (String)pairs.getKey())) {
                            entry.addTopic((String)pairs.getKey());
                            found = true;
                            break;
                        }
                    }
                }
                
                if (found) {
                    break;
                }
            }
        }
    }
    
    /**
     * Returns true or false whether or not the entry has the given scripture.
     * @param entry entry to check scripture presence.
     * @param scrip scripture to be checked for.
     * @return true or false.
     */
    public boolean hasScripture(Entry entry, Scripture scrip) {
        for (Scripture scripture : entry.getScriptureList()) {
            if (scrip.getFullTitle().equals(scripture.getFullTitle())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true or false depending on whether or not the given entry has
     * the given topic or not.
     * @param entry entry to check topic presence.
     * @param top topic to be checked for.
     * @return true or false.
     */
    public boolean hasTopic(Entry entry, String top) {
        for (String topic : entry.getTopicsList()) {
            if (topic.equals(top)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the scripture list.
     * @return scriptures.
     */
    public List<Scripture> getScriptures() {
        return scriptures;
    }

    /**
     * Sets the scripture list.
     * @param scriptures the new scripture list. 
     */
    public void setScriptures(List<Scripture> scriptures) {
        this.scriptures = scriptures;
    }

    /**
     * Returns the topic list.
     * @return topics
     */
    public Map<String, List<String>> getTopics() {
        return topics;
    }

    /**
     * Sets the topic list.
     * @param topics the new topic list.
     */
    public void setTopics(Map<String, List<String>> topics) {
        this.topics = topics;
    }
}