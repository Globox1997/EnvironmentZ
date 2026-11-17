package net.environmentz.init;

import com.mojang.serialization.Codec;
import net.environmentz.EnvironmentzMain;
import net.environmentz.item.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.DyedItemColor;
import java.util.function.UnaryOperator;

public class ItemInit {

    // Item Group
    public static final ResourceKey<CreativeModeTab> ENVIRONMENTZ_ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("environmentz", "item_group"));

    public static final int COOLING_HEATING_VALUE = 120;
    // Component
    public static final DataComponentType<Integer> ICED = registerComponent("iced", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<Boolean> INSULATED = registerComponent("insulated", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    // Item
    public static final Item HEATING_STONES_ITEM = register("heating_stones", new HeatingStones(new Item.Properties().durability(COOLING_HEATING_VALUE)));
    public static final Item WOLF_PELT_ITEM = register("wolf_pelt", new WolfPeltItem(new Item.Properties()));
    public static final Item POLAR_BEAR_FUR_ITEM = register("polar_bear_fur", new PolarBearFurItem(new Item.Properties()));
    public static final Item ICE_PACK_ITEM = register("ice_pack", new IcePack(new Item.Properties().durability(COOLING_HEATING_VALUE)));
    // Armor
    public static final Item WOLF_HELMET = register("wolf_helmet",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(7))));
    public static final Item WOLF_CHESTPLATE = register("wolf_chestplate",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(7))));
    public static final Item WOLF_LEGGINGS = register("wolf_leggings",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(7))));
    public static final Item WOLF_BOOTS = register("wolf_boots",
            new ArmorItem(EnvironmentzArmorMaterials.WOLF, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(7))));

    public static final Item WANDERER_HELMET = register("wanderer_helmet",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffe3c88e, false))));
    public static final Item WANDERER_CHESTPLATE = register("wanderer_chestplate",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffe3c88e, false))));
    public static final Item WANDERER_LEGGINGS = register("wanderer_leggings",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffe3c88e, false))));
    public static final Item WANDERER_BOOTS = register("wanderer_boots",
            new ArmorItem(EnvironmentzArmorMaterials.WANDERER, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffe3c88e, false))));

    public static final Item WOOL_HELMET = register("wool_helmet",
            new ArmorItem(EnvironmentzArmorMaterials.WOOL, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffffff, false))));
    public static final Item WOOL_CHESTPLATE = register("wool_chestplate",
            new ArmorItem(EnvironmentzArmorMaterials.WOOL, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffffff, false))));
    public static final Item WOOL_LEGGINGS = register("wool_leggings",
            new ArmorItem(EnvironmentzArmorMaterials.WOOL, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffffff, false))));
    public static final Item WOOL_BOOTS = register("wool_boots",
            new ArmorItem(EnvironmentzArmorMaterials.WOOL, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(4)).component(DataComponents.DYED_COLOR, new DyedItemColor(0xffffff, false))));
    // Potion
    public static final Holder<Potion> COLD_RESISTANCE = registerPotion("cold_resistance", new Potion(new MobEffectInstance(EffectInit.WARMING, 3600)));
    public static final Holder<Potion> LONG_COLD_RESISTANCE = registerPotion("long_cold_resistance", new Potion(new MobEffectInstance(EffectInit.WARMING, 9600)));
    public static final Holder<Potion> OVERHEATING_RESISTANCE = registerPotion("overheating_resistance", new Potion(new MobEffectInstance(EffectInit.COOLING, 3600)));
    public static final Holder<Potion> LONG_OVERHEATING_RESISTANCE = registerPotion("long_overheating_resistance", new Potion(new MobEffectInstance(EffectInit.COOLING, 9600)));

    private static Item register(String id, Item item) {
        return register(EnvironmentzMain.identifierOf(id), item);
    }

    private static Item register(ResourceLocation id, Item item) {
        ItemGroupEvents.modifyEntriesEvent(ENVIRONMENTZ_ITEM_GROUP).register(entries -> entries.accept(item));
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, EnvironmentzMain.identifierOf(name), potion);
    }

    private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, EnvironmentzMain.identifierOf(id), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ENVIRONMENTZ_ITEM_GROUP,
                FabricItemGroup.builder().icon(() -> new ItemStack(WOLF_CHESTPLATE)).title(Component.translatable("item.environmentz.item_group")).build());

        CauldronInteraction.WATER.map().put(WANDERER_HELMET, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.map().put(WANDERER_CHESTPLATE, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.map().put(WANDERER_LEGGINGS, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.map().put(WANDERER_BOOTS, CauldronInteraction.DYED_ITEM);

        CauldronInteraction.WATER.map().put(WOOL_HELMET, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.map().put(WOOL_CHESTPLATE, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.map().put(WOOL_LEGGINGS, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.map().put(WOOL_BOOTS, CauldronInteraction.DYED_ITEM);
    }

}
