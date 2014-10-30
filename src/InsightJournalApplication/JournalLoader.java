/*
 * This is a class that we created for the multi-threading that did not work out for us.
 */
package InsightJournalApplication;

/**
 *
 * @author Kevin Andres
public class JournalLoader implements Runnable{
    private final Updater updater;
    private Journal myJournal;
    private String fileName;
    
    public JournalLoader(String fileName, Updater updater, Journal myJournal) {
        this.updater = updater;
        this.myJournal = myJournal;
        this.fileName = fileName;
    }
    
    @Override
    public void run() {
        Importer importer = new Importer(fileName, updater);
        myJournal.setEntries(importer.readFile());
    }
}
* */