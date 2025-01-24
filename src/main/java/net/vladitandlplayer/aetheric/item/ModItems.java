package net.vladitandlplayer.aetheric.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.vladitandlplayer.aetheric.Aetheric;

public class ModItems {

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Aetheric.MOD_ID, name), item);
    }

    public static void registerModItems() {
        //to invoke the class
    }
}
