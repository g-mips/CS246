package Document;

/**
 * This stores the information of a scripture. It knows the book name,
 * the chapter, and the verse. It will store this information
 * individually and together.
 * @author Grant
 */
public class Scripture {
    private String bookName;
    private String chapter;
    private String startVerse;
    private String endVerse;
    private String fullTitle;
    private String bookAndChapter;

    /**
     * Constructs a scripture based off of the parameters givne. Will create
     * bookAndChapter based of off given bookName and chapter and full title
     * will be based of off bookName, chapter, startVerse and endVerse.
     * 
     * @param bookName the name of the scripture.
     * @param chapter the chapter of the scripture.
     * @param startVerse the starting verse of the scripture. 
     * @param endVerse the ending verse of the scripture.
     */
    public Scripture(String bookName, String chapter, String startVerse, String endVerse) {
        this.bookName = bookName;
        this.chapter = chapter;
        this.startVerse = startVerse;
        this.endVerse = endVerse;
        fullTitle = bookName + " " + chapter + " " + startVerse;
        if (!endVerse.equals("")) {
            fullTitle += " - " + endVerse;
        }
        bookAndChapter = bookName + " " + chapter;
    }
    
    /**
     * Constructor for Scripture taking into account just the bookName and chapter.
     * fullTitle is based off of bookAndChapter and bookAndChapter is just bookName
     * and chapter combined.
     * 
     * @param bookName the book name of the scripture
     * @param chapter the chapter of the scripture
     */
    public Scripture(String bookName, String chapter) {
        this.bookName = bookName;
        this.chapter = chapter;
        this.startVerse = "";
        this.endVerse = "";
        bookAndChapter = bookName + " " + chapter;
        fullTitle = bookAndChapter;
    }
    
    /**
     * Returns the bookName
     * @return bookName
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Sets bookName
     * @param bookName name of the book of the scripture 
     */
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    /**
     * Returns the chapter
     * @return chapter
     */
    public String getChapter() {
        return chapter;
    }

    /**
     * Sets the chapter of the scripture
     * @param chapter chapter of the scripture
     */
    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    /**
     * Returns the startVerse
     * @return starting verse of the scripture
     */
    public String getStartVerse() {
        return startVerse;
    }

    /**
     * Sets the starting verse of the scripture
     * @param verse starting verse of the scripture
     */
    public void setStartVerse(String verse) {
        this.startVerse = verse;
    }

    /**
     * Returns the endVerse
     * @return endVerse
     */
    public String getEndVerse() {
        return endVerse;
    }

    /**
     * Sets the endVerse
     * @param endVerse ending verse of the scripture
     */
    public void setEndVerse(String endVerse) {
        this.endVerse = endVerse;
    }
    
    /**
     * Returns the fullTitle
     * @return fullTitle
     */
    public String getFullTitle() {
        return fullTitle;
    }

    /**
     * Sets the fullTitle
     * @param fullTitle the full title of the scripture
     */
    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    /**
     * Returns the bookAndChapter
     * @return bookAndChapter
     */
    public String getBookAndChapter() {
        return bookAndChapter;
    }

    /**
     * Sets the bookAndChapter
     * @param bookAndChapter the book and chapter of the scripture.
     */
    public void setBookAndChapter(String bookAndChapter) {
        this.bookAndChapter = bookAndChapter;
    }
}