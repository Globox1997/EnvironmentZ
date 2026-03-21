package net.environmentz.init;

import com.mojang.serialization.Codec;
import net.environmentz.EnvironmentzMain;
import net.environmentz.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.UnaryOperator;

public class ItemInit {

    // Item Group
    public static final RegistryKey<ItemGroup> ENVIRONMENTZ_ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of("environmentz", "item_group"));

    public static final int COOLING_HEATING_VALUE = 120;
    // Component
    public static final ComponentType<Integer> ICED = registerComponent("iced", builder -> builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));
    public static final ComponentType<Boolean> INSULATED = registerComponent("insulated", builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));
    // Item
    public static final Item HEATING_STONES_ITEM = register("heating_stones", new HeatingStones(new Item.Settings().maxDamage(COOLING_HEATING_VALUE)));
    public static final Item WOLF_PELT_ITEM = register("wolf_pelt", new WolfPeltItem(new Item.Settings()));
    public static final Item POLAR_BEAR_FUR_ITEM = register("polar_bear_fur", new PolarBearFurItem(new Item.Settings()));
    public static final Item ICE_PACK_ITEM = register("ice_pack", new IcePack(new Item.Settings().maxDamage(COOLING_HEATING_VALUE)));
    // Armor
    public static final Item WOLF_HELMET = register("wolf_helmet",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(7))));
    public static final Item WOLF_CHESTPLATE = register("wolf_chestplate",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(7))));
    public static final Item WOLF_LEGGINGS = register("wolf_leggings",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(7))));
    public static final Item WOLF_BOOTS = register("wolf_boots",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(7))));

    public static final Item WANDERER_HELMET = register("wanderer_helmet",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(4)).component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xffe3c88e, false))));
    public static final Item WANDERER_CHESTPLATE = register("wanderer_chestplate",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(4)).component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xffe3c88e, false))));
    public static final Item WANDERER_LEGGINGS = register("wanderer_leggings",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(4)).component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xffe3c88e, false))));
    public static final Item WANDERER_BOOTS = register("wanderer_boots",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(4)).component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xffe3c88e, false))));

    // Potion
    public static final RegistryEntry<Potion> COLD_RESISTANCE = registerPotion("cold_resistance", new Potion(new StatusEffectInstance(EffectInit.WARMING, 3600)));
    public static final RegistryEntry<Potion> LONG_COLD_RESISTANCE = registerPotion("long_cold_resistance", new Potion(new StatusEffectInstance(EffectInit.WARMING, 9600)));
    public static final RegistryEntry<Potion> OVERHEATING_RESISTANCE = registerPotion("overheating_resistance", new Potion(new StatusEffectInstance(EffectInit.COOLING, 3600)));
    public static final RegistryEntry<Potion> LONG_OVERHEATING_RESISTANCE = registerPotion("long_overheating_resistance", new Potion(new StatusEffectInstance(EffectInit.COOLING, 9600)));

    private static Item register(String id, Item item) {
        return register(EnvironmentzMain.identifierOf(id), item);
    }

    private static Item register(Identifier id, Item item) {
        ItemGroupEvents.modifyEntriesEvent(ENVIRONMENTZ_ITEM_GROUP).register(entries -> entries.add(item));
        return Registry.register(Registries.ITEM, id, item);
    }

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, EnvironmentzMain.identifierOf(name), potion);
    }

    private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, ENVIRONMENTZ_ITEM_GROUP,
                FabricItemGroup.builder().icon(() -> new ItemStack(WOLF_CHESTPLATE)).displayName(Text.translatable("item.environmentz.item_group")).build());

        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(WOLF_HELMET, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(WOLF_CHESTPLATE, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(WOLF_LEGGINGS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(WOLF_BOOTS, CauldronBehavior.CLEAN_DYEABLE_ITEM);
    }

}
