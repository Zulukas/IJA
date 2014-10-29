/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InsightJournalApplication;

import javafx.application.Platform;
import javafx.scene.control.TextField;

/***************************************************************************
 * TO BE IMPLEMENTED 
 **************************************************************************/
public class TextFieldUpdater {
    public TextField text;
    int counter = 0;
    
    public void update(int count) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                text.setText("Something here..." + counter);
            }
        });

    }
}