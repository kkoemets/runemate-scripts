package com.kkoemets.scripts;

import com.kkoemets.api.nightmarezone.NightmareZoneStateContainer;
import com.kkoemets.api.nightmarezone.threshold.AbsorptionPointsThresholdContainer;
import com.kkoemets.api.nightmarezone.threshold.HpThresholdContainer;
import com.kkoemets.api.nightmarezone.threshold.ThresholdContainer;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.Player;
import com.runemate.game.api.hybrid.local.Varbit;
import com.runemate.game.api.hybrid.local.hud.interfaces.ChatDialog;
import com.runemate.game.api.hybrid.local.hud.interfaces.Health;
import com.runemate.game.api.hybrid.local.hud.interfaces.Inventory;
import com.runemate.game.api.hybrid.location.Area;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.queries.results.SpriteItemQueryResults;
import com.runemate.game.api.hybrid.region.GameObjects;
import com.runemate.game.api.hybrid.region.Npcs;
import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.framework.LoopingBot;
import com.runemate.game.api.script.framework.listeners.MoneyPouchListener;
import com.runemate.game.api.script.framework.listeners.events.MoneyPouchEvent;
import com.runemate.game.api.script.framework.logger.BotLogger;

import java.util.List;
import java.util.Optional;

import static com.kkoemets.api.nightmarezone.NightmareZoneStateContainer.DreamMode.ABSORPTION_AND_OVERLOAD_MODE;
import static com.kkoemets.playersense.CustomPlayerSense.Key.ACTIVENESS_FACTOR_WHILE_WAITING;
import static com.kkoemets.playersense.CustomPlayerSense.initializeKeys;
import static com.runemate.game.api.hybrid.local.Varbits.load;
import static com.runemate.game.api.hybrid.local.hud.interfaces.Inventory.getItems;
import static com.runemate.game.api.hybrid.region.Players.getLocal;
import static com.runemate.game.api.script.Execution.delay;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class NightmareZone extends LoopingBot implements MoneyPouchListener {

    private final String DWARWEN_ROCK_CAKE = "Dwarven rock cake";
    private final int absorptionPointsVarBit = 3956;
    private final int overloadVarBit = 3955;

    private String aSetting;
    private BotLogger log;
    private Area dominicArea;

    private ThresholdContainer hpThresholdContainer;
    private ThresholdContainer absorptionPointsThreshold;
    private NightmareZoneStateContainer nightmareZoneStateContainer;


    // Required to tell the client that the bot is EmbeddableUI compatible. Remember, that a bot's main class must have a public no-args constructor, which every Object has by default.
    public NightmareZone() {
    }

    @Override
    public void onStart(String... args) {
        initializeKeys();
        // Submit your MoneyPouchListener
        getEventDispatcher().addListener(this);
        // Sets the length of time in milliseconds to wait before calling onLoop again
        setLoopDelay((int) (ACTIVENESS_FACTOR_WHILE_WAITING.getAsDouble() * 10000));
        // Load script configuration
        aSetting = getSettings().getProperty("setting");
        log = getLogger();

        dominicArea = Area.polygonal(nmzStartBox(
                new int[]{2610, 3112},
                new int[]{2610, 3120},
                new int[]{2601, 3120},
                new int[]{2601, 3112})
                .toArray(new Coordinate[1]));

        hpThresholdContainer = new HpThresholdContainer();
        absorptionPointsThreshold = new AbsorptionPointsThresholdContainer();
        nightmareZoneStateContainer = new NightmareZoneStateContainer(ABSORPTION_AND_OVERLOAD_MODE);
//        setZoom(0.027, 0.004);
    }

    @Override
    public void onContentsChanged(MoneyPouchEvent moneyPouchEvent) {
        // React to money pouch event
    }

    @Override
    public void onLoop() {
        Optional<Player> player = ofNullable(getLocal());
        if (!player.isPresent()) {
            log.info("Player is not visible yet, waiting...");
            return;
        }

        if (!isPlayerInADream(player.get())) {
            throw new IllegalStateException("Player is not in a dream!");
        }

        if (getDwarvenRockCake().isEmpty()) {
            throw new IllegalStateException("Player does not have a rock cake!");
        }

        if (getDwarvenRockCake().get(0).getIndex() < 24) {
            throw new IllegalStateException("Dwarven rock cake must in the last row in inventory");
        }

        switch (nightmareZoneStateContainer.DREAM_MODE()) {
            case ABSORPTION_MODE:
                absorptionModeScript();
                break;
            case ABSORPTION_AND_OVERLOAD_MODE:
                absorptionAndOverloadModeScript();
        }
        log.debug("End of main loop");
    }

    private void absorptionAndOverloadModeScript() {
        if (!getOverloadPotions().isEmpty()) {
            if (!getOverloadTime().isPresent() || hasOverloadPotionEnded()) {
                log.info("Drinking overload potion");
                drinkOverloadDose();
                if (!getAbsorptionPotions().isEmpty()) {
                    log.info("Drinking absorption potions until full");
                    drinkAbsorptionPotionsUntilFull();
                }
                log.info("Guzzling dwarven rock cake until full");
                guzzleRockCakeUntilHpIs(1);
            }
        } else {
            log.info("No overloads, using absorption potions only mode");
            absorptionModeScript();
        }
    }

    private void absorptionModeScript() {
        if (isHpUnder(hpThresholdContainer.getThreshold())) {
            log.info("Hp is below threshold: " + hpThresholdContainer.getThreshold());
            log.info("Starting to guzzle dwarven rock cake until hp is 1");
            guzzleRockCakeUntilHpIs(1);
            hpThresholdContainer.nextThreshold();
            log.info("New hp threshold: " + hpThresholdContainer.getThreshold());
            if ((!getAbsorptionPoints().isPresent()
                    || isAbsorptionPointsUnder(getAbsorptionPoints().get(),
                    absorptionPointsThreshold.getThreshold())) && !getAbsorptionPotions().isEmpty()) {
                log.info("Absorption potion is below threshold: " +
                        absorptionPointsThreshold.getThreshold());
                drinkAbsorptionPotionsUntilFull();
                absorptionPointsThreshold.nextThreshold();
                log.info("New absorption potion threshold: " +
                        absorptionPointsThreshold.getThreshold());
            }
        }
    }

    private void nmzScript() {
        if (!ofNullable(getLocal()).isPresent()) {

        } else if (!dominicArea.contains(getLocal().getPosition())) { //TODO!!! also check that is
            // not in nightmare zone
            throw new IllegalStateException("Player is not near Nightmare Zone vial and is not in" +
                    " a dream");
        } else if (getDwarvenRockCake().isEmpty()) {
            throw new IllegalStateException("Player does not have a rock cake in the inventory");
        } else if (getAbsorptionPotions().isEmpty()) {
            throw new IllegalStateException("Player does not have absorption potions in the " +
                    "inventory");
        } else if (dominicArea.contains(getLocal().getPosition())) {
            if (!GameObjects.newQuery().names("Empty vial").results().isEmpty()) {
                log.info("Dream has not been started to yet, talking to Dominic Onion");
                Optional<Npc> dominicOnion = ofNullable(Npcs.newQuery().names("Dominic Onion")
                        .results().get(0));
                if (dominicOnion.isPresent()) {
                    log.info("Clicking on Dominic Onion to start a dream");
                    dominicOnion.get().interact("Dream");
                    // TODO!!! sleep orsmth
                    delay(5000);
                    if (ChatDialog.isOpen()) {
                        log.info("Chat is open with Dominic Onion");
                        ofNullable(ChatDialog.getOption(4).select());
                        delay(1000);
                        ChatDialog.getContinue().select();
                        delay(1000);
                        ChatDialog.getOption(1).select();
                        log.info("A dream has been bought");
                    }
                }
            } else {
                Optional<GameObject> potion = ofNullable(GameObjects.newQuery()
                        .names("Potion").results().get(0));
                if (potion.isPresent()) {
                    log.info("Everything is ready for a dream");
                    potion.get().interact("Drink");
                }
            }
        }
    }

    private Optional<Varbit> getAbsorptionPoints() {
        return ofNullable(load(absorptionPointsVarBit));
    }

    private void drinkInitialOverloadDose() {
        getOverloadPotions().get(0).click();
    }

    private boolean hasOverloadPotionEnded() {
        return getOverloadTime().get().getValue() == 0;
    }

    private Optional<Varbit> getOverloadTime() {
        return ofNullable(load(overloadVarBit));
    }

    private void drinkOverloadDose() {
        delay(Random.nextInt(3533, 5345));
        drinkInitialOverloadDose();
        delay(Random.nextInt(5533, 6345));
    }

    private void drinkAbsorptionPotionsUntilFull() {
        while (!getAbsorptionPoints().isPresent() ||
                (isAbsorptionPointsUnderMax(getAbsorptionPoints().get()) && !getAbsorptionPotions()
                        .isEmpty())) {
            drinkAbsorptionPotion();
        }
    }

    private void guzzleRockCakeUntilHpIs(int i) {
        while (Health.getCurrent() != i) {
            guzzleRockCake();
        }
    }

    private void drinkAbsorptionPotion() {
        getAbsorptionPotions().get(0).click();
        delay(Random.nextInt(190, 330));
    }

    private boolean isAbsorptionPointsUnder(Varbit varbit, int i) {
        return varbit.getValue() < i;
    }

    private boolean isPlayerInADream(Player player) {
        return player.getPosition().getHeight() == -240;
    }

    private void guzzleRockCake() throws IllegalStateException {
        getDwarvenRockCake().get(0).interact("Guzzle");
        delay(Random.nextInt(190, 330) / 3);
    }

    private SpriteItemQueryResults getDwarvenRockCake() {
        return Inventory.getItems(DWARWEN_ROCK_CAKE);
    }

    private boolean isHpUnder(int i) {
        return Health.getCurrent() > i;
    }

    private boolean isAbsorptionPointsUnderMax(Varbit absorptionPoints) {
        return isAbsorptionPointsUnder(absorptionPoints, 950);
    }

    private SpriteItemQueryResults getAbsorptionPotions() {
        return getItems("Absorption (1)",
                "Absorption (2)", "Absorption (3)",
                "Absorption (4)").sortByIndex();
    }

    private SpriteItemQueryResults getOverloadPotions() {
        return getItems("Overload (1)",
                "Overload (2)", "Overload (3)",
                "Overload (4)").sortByIndex();
    }

    private List<Coordinate> nmzStartBox(int[]... intArrays) {
        return stream(intArrays).map(intArray -> new Coordinate(intArray[0], intArray[1]))
                .collect(toList());
    }

}