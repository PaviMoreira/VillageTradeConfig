package com.pavi.entrepreneuradjust;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

public class ModTrades implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("==== Entrepreneur Adjust Loaded ====");
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            registerTrades();
        });
    }

    public static void registerTrades() {
        VillagerProfession entrepreneur = Registries.VILLAGER_PROFESSION.get(
                Identifier.of("legendarymonuments", "entrepreneur")
        );

        if (entrepreneur == null) {
            System.out.println("ERROR: Could not find entrepreneur profession!");
            return;
        }

        ModConfig config = ModConfig.getInstance();

        for (ModConfig.TradeConfig tradeConfig : config.trades) {
            try {
                ItemStack primaryInput = new ItemStack(
                        Registries.ITEM.get(Identifier.tryParse(tradeConfig.input_item)),
                        tradeConfig.input_count
                );

                ItemStack secondaryInput = tradeConfig.hasSecondaryItem() ?
                        new ItemStack(
                                Registries.ITEM.get(Identifier.tryParse(tradeConfig.secondary_item)),
                                tradeConfig.secondary_count
                        ) : null;

                ItemStack output = new ItemStack(
                        Registries.ITEM.get(Identifier.tryParse(tradeConfig.output_item)),
                        tradeConfig.output_count
                );

                if (primaryInput.isEmpty() || output.isEmpty()) {
                    System.err.println("Skipping trade - could not find items: " +
                            tradeConfig.input_item + " -> " + tradeConfig.output_item);
                    continue;
                }

                final int finalLevel = Math.min(5, Math.max(1, tradeConfig.level));
                final int finalMaxUses = Math.max(1, tradeConfig.max_uses);
                final int finalExp = Math.max(0, tradeConfig.experience);
                final float finalMult = Math.max(0, tradeConfig.multiplier);

                TradeOfferHelper.registerVillagerOffers(entrepreneur, finalLevel, factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(primaryInput.getItem(), primaryInput.getCount()),
                            secondaryInput != null ?
                                    java.util.Optional.of(new TradedItem(secondaryInput.getItem(), secondaryInput.getCount())) : null,
                            output,
                            finalMaxUses,
                            finalExp,
                            finalMult
                    ));
                });

                System.out.println("Registered trade: " +
                        tradeConfig.input_item + " x" + tradeConfig.input_count +
                        (tradeConfig.hasSecondaryItem() ?
                                " + " + tradeConfig.secondary_item + " x" + tradeConfig.secondary_count : "") +
                        " -> " + tradeConfig.output_item + " x" + tradeConfig.output_count +
                        " (Level " + finalLevel + ")");

            } catch (Exception e) {
                System.err.println("Failed to register trade: " + tradeConfig.input_item + " -> " + tradeConfig.output_item);
                e.printStackTrace();
            }
        }

        System.out.println("[Entrepreneur Adjust] Successfully registered " + config.trades.size() + " trades!");
    }
}