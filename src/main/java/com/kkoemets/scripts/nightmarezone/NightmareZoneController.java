package com.kkoemets.scripts.nightmarezone;

import com.kkoemets.scripts.nightmarezone.scripts.presets.AbstractNightmareZoneScript;
import com.runemate.game.api.hybrid.local.Skill;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.math.BigDecimal.ROUND_HALF_EVEN;
import static java.util.stream.Collectors.toList;

public class NightmareZoneController implements Initializable {
    private final NightmareZoneMain main;
    private final Stage stage;

    @FXML
    private ComboBox<String> presetOptions;
    @FXML
    private Button toggleBtn;
    @FXML
    private ListView<String> xpList;

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

        main.setOnStop(stage::close);
        main.setOnPause(() -> toggleBtn.setText("Turn on"));
        main.setOnResume(() -> toggleBtn.setText("Turn off"));
        main.setOnXpGained((xpGainMap, startTimeInMillis) -> {
            Platform.runLater(() -> {
                xpList.getItems().clear();
                xpList.getItems().addAll(xpGainMap.entrySet().stream()
                        .map(skillXpGain -> {
                            Skill skill = skillXpGain.getKey();
                            int xpGained = skillXpGain.getValue();

                            return format("%s: %d xp gained. %s xp/h", skill, xpGained, BigDecimal.valueOf(xpGained)
                                    .divide(BigDecimal.valueOf(currentTimeMillis() - startTimeInMillis)
                                                    .divide(BigDecimal.valueOf(60 * 60 * 1000), 6, ROUND_HALF_EVEN),
                                            0, ROUND_HALF_EVEN).toString());
                        })
                        .collect(toList()));
            });
        });
    }

    private void onToggle() {
        AbstractNightmareZoneScript selectedScript = main
                .getAllScripts().stream()
                .filter(script -> script.getScriptName().toString()
                        .equals(presetOptions.getSelectionModel().getSelectedItem()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Couldn't find preset!"));

        main.setCurrentScript(selectedScript);

        main.getLogger().info(format("Selected script: %s", selectedScript.getScriptName()));

        toggleScriptState();
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