package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.scripts.nightmarezone.scripts.AbstractNightmareZoneScript;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
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
        presetOptions.getItems().addAll(collectAllScriptNames());

        presetOptions.getSelectionModel().selectFirst();

        toggleBtn.setText("Turn on");
        toggleBtn.setOnAction(event -> onToggle());
    }

    private void onToggle() {
        AbstractNightmareZoneScript selectedScript = main
                .getAllScripts().stream()
                .filter(script -> script.getScriptName().toString()
                        .equals(presetOptions.getSelectionModel().getSelectedItem().toString()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Couldn't find preset!"));

        main.setCurrentScript(selectedScript);

        System.out.printf("Selected script: %s%n", selectedScript.getScriptName());

        toggleScriptState();

        toggleBtn.setText(main.isPaused() ? "Turn on" : "Turn off");
    }

    private void toggleScriptState() {
        Runnable stateChange = main.isPaused()
                ? main::resume
                : main::pause;
        stateChange.run();
    }

    private List<String> collectAllScriptNames() {
        return main
                .getAllScripts().stream()
                .map(AbstractNightmareZoneScript::getScriptName)
                .map(Enum::toString)
                .collect(toList());
    }

}