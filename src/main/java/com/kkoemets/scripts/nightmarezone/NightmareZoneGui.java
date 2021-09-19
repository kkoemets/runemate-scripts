package com.kkoemets.scripts.nightmarezone;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.stage.StageStyle.UTILITY;

public class NightmareZoneGui extends Stage {

    public NightmareZoneGui(NightmareZoneMain loopingBot) {
        try {
            setTitle("Nightmare Zone script");
            FXMLLoader loader = new FXMLLoader();

            loader.setController(new NightmareZoneController(loopingBot, this));

            setScene(new Scene
                    (loader.load(NightmareZoneMain.class.getResourceAsStream("NightmareZoneMain.fxml"))));
            initStyle(UTILITY);

            setOnCloseRequest(Event::consume);
        } catch (Exception e) {
            System.err.println("Failed to set GUI");
            e.printStackTrace();
        }

        show();
    }

}
