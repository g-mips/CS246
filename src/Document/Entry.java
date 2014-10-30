package Document;

import java.util.ArrayList;
import java.util.List;

/**
 * It will store the text that the user writes and it stores the scriptures and 
 * topics within the text itself. The entry will know the date of when it was
 * written as well.
 * @author Grant
 */
public class Entry {
    protected String text = "";
    protected List <Scripture> scriptures = new ArrayList();
    protected List <String> topics = new ArrayList();
    protected String date;
    
    /**
     * Constructs an entry with no text nor date. The scriptures and topics
     * list are empty.
     */
    public Entry() {
        
    }
    
    /**
     * Adds the topic to the list.
     * @param topic topic to add.
     */
    public void addTopic(String topic) {
        topics.add(topic);
    }
    
    /**
     * Removes a topic from the list given the index.
     * @param index index of the topic to remove
     */
    public void removeTopic(int index) {
        if (index < topics.size() && index != 0) {
            topics.remove(index);
        }
    }
    
    /**
     * Removes all topics from the list.
     */
    public void removeAllTopics() {
        while (topics.size() > 0) {
            topics.remove(0);
        }
    }
    
    /**
     * Adds the scripture given to the list.
     * @param scripture scripture to add
     */
    public void addScripture(Scripture scripture) {
        scriptures.add(scripture);
    }
    
    /**
     * Removes a scripture given the index
     * @param index the location of the scripture to remove.
     */
    public void removeScripture(int index) {
        if (index < scriptures.size() && index != 0) {
            scriptures.remove(index);
        }
    }
    
    /**
     * Removes all the scriptures in the list.
     */
    public void removeAllScriptures() {
        while (scriptures.size() > 0) {
            scriptures.remove(0);
        }
    }
    
    /**
     * Returns a list of the scriptures
     * @return scriptures
     */
    public List <Scripture> getScriptureList() {
        return scriptures;
    }
    
    /**
     * Returns a list of the topics
     * @return topics
     */
    public List <String> getTopicsList() {
        return topics;
    }
    
    /**
     * Returns the scripture at location of the index. If it is out of bounds
     * it will return null.
     * @param index the index of the desired scripture
     * @return scripture.get(index)
     */
    public Scripture getScriptureAt(int index) {
        if (index > scriptures.size() || index < 0) {
            return null;
        }
        return scriptures.get(index);
    }
    
    /**
     * Sets the scripture list to the new one.
     * @param scriptureList the new scripture list for scriptures 
     */
    public void setScriptureList(List<Scripture> scriptureList) {
        scriptures = scriptureList;
    }
    
    /**
     * Returns the topic at the given index. If the index is out of bounds, return
     * null.
     * @param index the index of the desired topic.
     * @return topic.get(index)
     */
    public String getTopicAt(int index) {
        if (index > topics.size() || index < 0) {
            return null;
        }
        return topics.get(index);
    }
    
    /**
     * Returns the text
     * @return text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Sets the text to the new text.
     * @param text the new text
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Returns the date.
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date to the new date.
     * @param date the new date.
     */
    public void setDate(String date) {
        this.date = date;
    }
}