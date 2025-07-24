package com.pavi.villagertradeconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("villager-trade-config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig INSTANCE;

    public List<TradeConfig> trades = new ArrayList<>();

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ModConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    // Loads the config file
    public void load() {
        if (!Files.exists(CONFIG_PATH)) {
            createDefaultConfig();
            return;
        }

        try {
            String json = Files.readString(CONFIG_PATH);
            Type type = new TypeToken<List<TradeConfig>>(){}.getType();
            this.trades = GSON.fromJson(json, type);

            if (this.trades == null) {
                throw new IOException("Empty or invalid config file");
            }
        } catch (Exception e) {
            System.err.println("Failed to load config, using defaults");
            e.printStackTrace();
            createDefaultConfig();
        }
    }

    // This is just a test using the entrepreneur from legendarymonuments, can edit at will, but basically this is options on the config creation
    private void createDefaultConfig() {
        trades.clear();

        // Add default trades, each one is a different trade
        trades.add(new TradeConfig(
                "legendarymonuments",
                "entrepreneur",
                "cobblemon:relic_coin_pouch", 16,
                "minecraft:emerald", 8,
                "unimplemented_items:bottle_cap", 1,
                2, 20, 15, 0.1f
        ));

        trades.add(new TradeConfig(
                "legendarymonuments",
                "entrepreneur",
                "cobblemon:relic_coin_sack", 16,
                "minecraft:emerald", 64,
                "mega_showdown:blueorb", 1,
                4, 1, 50, 0.2f
        ));

        trades.add(new TradeConfig(
                "legendarymonuments",
                "entrepreneur",
                "cobblemon:relic_coin_sack", 16,
                "minecraft:emerald", 64,
                "mega_showdown:redorb", 1,
                4, 1, 50, 0.2f
        ));

        save();
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(trades));
        } catch (IOException e) {
            System.err.println("Failed to save config");
            e.printStackTrace();
        }
    }

    public static class TradeConfig {
        // Villager name and mod from villager
        public String villager_mod;
        public String villager_id;

        // Primary input item
        public String input_item;
        public int input_count;

        // Secondary input item (optional)
        public String secondary_item;
        public int secondary_count;

        // Output item
        public String output_item;
        public int output_count;

        public int level;
        public int max_uses;
        public int experience;
        public float multiplier;

        public TradeConfig() {}

        public TradeConfig(String villager_mod, String villager_id, String inputItem, int inputCount,
                           String secondaryItem, int secondaryCount,
                           String outputItem, int outputCount,
                           int level, int maxUses, int exp, float mult) {
            this.villager_mod = villager_mod;
            this.villager_id = villager_id;
            this.input_item = inputItem;
            this.input_count = inputCount;
            this.secondary_item = secondaryItem;
            this.secondary_count = secondaryCount;
            this.output_item = outputItem;
            this.output_count = outputCount;
            this.level = level;
            this.max_uses = maxUses;
            this.experience = exp;
            this.multiplier = mult;
        }

        public boolean hasSecondaryItem() {
            return secondary_item != null && !secondary_item.isEmpty();
        }
    }
}