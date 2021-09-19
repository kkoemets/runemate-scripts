package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.scripts.nightmarezone.scripts.AbstractNightmareZoneScript;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;

public class NightmareZoneController implements Initializable {
    private final NightmareZoneMain main;
    private final Stage stage;

    @FXML
    private ComboBox presetOptions;
    @FXML
    private Button toggleBtn;

    public NightmareZoneController(NightmareZoneMain main, Stage stage) {
        this.main = main;
        this.stage = stage;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        presetOptions.getItems().addAll(main
                .getAllScripts().stream()
                .map(AbstractNightmareZoneScript::getScriptName)
                .map(Enum::toString)
                .collect(toList())
        );

        presetOptions.getSelectionModel().selectFirst();

        toggleBtn.setText("Turn on");
        toggleBtn.setOnAction(event -> {
            AbstractNightmareZoneScript selectedScript = main
                    .getAllScripts().stream()
                    .filter(script -> script.getScriptName().toString()
                            .equals(presetOptions.getSelectionModel().getSelectedItem().toString()))
                    .findFirst().get();

            main.setCurrentScript(selectedScript);

            System.out.printf("Selected script: %s%n", selectedScript.getScriptName());

            Runnable stateChange = main.isPaused()
                    ? main::resume
                    : main::pause;
            stateChange.run();

            toggleBtn.setText(main.isPaused() ? "Turn on" : "Turn off");
        });
    }

}