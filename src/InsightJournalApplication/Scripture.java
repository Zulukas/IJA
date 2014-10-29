package InsightJournalApplication;

/*******************************************************************************
* The Scripture class holds information relevant to a scripture reference.     
******************************************************************************/
class Scripture{
   private String book;
   private int chapter;
   private int startVerse;
   private int endVerse;

   // Constructor setting everything to empty or zero.
    Scripture(){
    book = "";
    chapter = 0;
    startVerse = 0;
    endVerse = 0;
    }

   // GETTERS
   public String getBook(){return book;}
   public int getChapter(){return chapter;}
   public int getStartVerse(){return startVerse;}
   public int getEndVerse(){return endVerse;}

   // SETTERS
   public void setBook(String newBook) {book = newBook;}
   public void setChapter(int newChapter){chapter = newChapter;}
   public void setStartVerse(int newStartVerse){startVerse = newStartVerse;}
   public void setEndVerse(int newEndVerse){endVerse = newEndVerse;}
   
   /***************************************************************************
     * Build a scripture from book, chapter, verse-verse
     **************************************************************************/
   public Scripture(String book, int chapter, int startVerse, int endVerse) {
       this.book = book;
       this.chapter = chapter;
       this.startVerse = startVerse;
       this.endVerse = endVerse;
   }
   
   /***************************************************************************
     * Build a scripture from book, chapter, verse
     **************************************************************************/
   public Scripture(String book, int chapter, int startVerse) {
       this.book = book;
       this.chapter = chapter;
       this.startVerse = startVerse;
   }
   
   /***************************************************************************
     * Build a scripture from book and chapter
     **************************************************************************/
   public Scripture(String book, int chapter) {
       this.book = book;
       this.chapter = chapter;
   }
   
   /****************************************************************************
    * Display builds a scripture string to show information about the scripture.
    ***************************************************************************/
   public String display() {
       String rString = book + " " + chapter;
       
       if (startVerse > 0) {
            rString += ":" + startVerse;
            
            if (endVerse > 0) {
                rString += "-" + endVerse;
            }
       }
       return rString;
   }
}
