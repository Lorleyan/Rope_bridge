package dev.lorleyan.ropebridge;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class RopeBridgeMod implements ModInitializer {
    public static final String MOD_ID = "rope_bridge";
    public static final Item ANCHOR_SHOT = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "anchor_shot"),
            new AnchorShotItem(new Item.Settings())
    );

    @Override
    public void onInitialize() {
        // Static registration above makes the item available to registries and recipes.
    }
}
