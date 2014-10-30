package Document;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores all necessary functions and data to save and read in a text document
 * with entries present.
 * @author Grant
 */
public class TextFile {
    List <Entry> entries = new ArrayList<>();
    File path;
    String text = "";

    /**
     * Constructs a TextFile based of the File given to it.
     * @param path 
     */
    public TextFile(File path) {
        this.path = path;
    }
    
    /**
     * Saves the file as a .txt document. If there are no entries to save, it will
     * throw an IOException.
     * @exception IOException throws when BufferedOutputStream has trouble or
     *                        when there are no entries.
     */
    public void save() throws IOException{
        if (entries.size() > 0) {
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(path));
            for (Entry entry : entries) {
                String content = "-----\n";
                byte[] data = content.getBytes();
                output.write(data, 0, data.length);
                
                content = entry.getDate() + "\n";
                data = content.getBytes();
                output.write(data, 0, data.length);
                
                content = entry.getText() + "\n";
                data = content.getBytes();
                output.write(data, 0, data.length);
            }
            output.close();
        } else {
            throw new IOException("ERROR: No entries to save.");
        }
    }
    
    /**
     * Reads in the .txt file.
     * @return true if read correctly
     * @throws java.io.FileNotFoundException File's not found
     * @throws java.io.IOException Trouble reading file
     */
    public boolean readFile() throws FileNotFoundException, IOException {
        boolean read = false;
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(path));
            String line = "";
            while ((line = bf.readLine()) != null) {
                text += line + "\n";
            }
            read = true;
        } finally {
            if (bf != null) {
                bf.close();
            }
        }
        return read;
    }

    /**
     * Returns the File of the TextDoc
     * @return path
     */
    public File getPath() {
        return path;
    }

    /**
     * Sets a new File for the TextDoc
     * @param path the new File of the TextDoc
     */
    public void setPath(File path) {
        this.path = path;
    }

    /**
     * Returns the content of the TextDoc
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the content of the TextDoc
     * @param text the new content of the TextDoc.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the entries list.
     * @return entries
     */
    public List<Entry> getEntries() {
        return entries;
    }
    
    /**
     * Sets the entries of the TextDoc.
     * @param entries the new entries of the TextDoc.
     */
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}