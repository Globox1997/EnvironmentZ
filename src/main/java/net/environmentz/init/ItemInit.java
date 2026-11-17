package net.environmentz.init;

import com.mojang.serialization.Codec;
import net.environmentz.EnvironmentzMain;
import net.environmentz.item.*;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ItemInit {

    // Item Group
    public static final ResourceKey<CreativeModeTab> ENVIRONMENTZ_ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("environmentz", "item_group"));

    public static final int COOLING_HEATING_VALUE = 120;
    // Component
    public static final DataComponentType<Integer> ICED = registerComponent("iced", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DataComponentType<Boolean> INSULATED = registerComponent("insulated", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    // Item
    public static final Item HEATING_STONES_ITEM = register("heating_stones", HeatingStones::new, new Item.Properties().durability(COOLING_HEATING_VALUE));
    public static final Item WOLF_PELT_ITEM = register("wolf_pelt", WolfPeltItem::new, new Item.Properties());
    public static final Item POLAR_BEAR_FUR_ITEM = register("polar_bear_fur", PolarBearFurItem::new, new Item.Properties());
    public static final Item ICE_PACK_ITEM = register("ice_pack", IcePack::new, new Item.Properties().durability(COOLING_HEATING_VALUE));
    // Armor
    public static final Item WOLF_HELMET = registerArmor("wolf_helmet", EnvironmentzArmorMaterials.WOLF, ArmorType.HELMET);
    public static final Item WOLF_CHESTPLATE = registerArmor("wolf_chestplate", EnvironmentzArmorMaterials.WOLF, ArmorType.CHESTPLATE);
    public static final Item WOLF_LEGGINGS = registerArmor("wolf_leggings", EnvironmentzArmorMaterials.WOLF, ArmorType.LEGGINGS);
    public static final Item WOLF_BOOTS = registerArmor("wolf_boots", EnvironmentzArmorMaterials.WOLF, ArmorType.BOOTS);

    public static final Item WANDERER_HELMET = registerDyedArmor("wanderer_helmet", EnvironmentzArmorMaterials.WANDERER, ArmorType.HELMET, 0xffe3c88e);
    public static final Item WANDERER_CHESTPLATE = registerDyedArmor("wanderer_chestplate", EnvironmentzArmorMaterials.WANDERER, ArmorType.CHESTPLATE, 0xffe3c88e);
    public static final Item WANDERER_LEGGINGS = registerDyedArmor("wanderer_leggings", EnvironmentzArmorMaterials.WANDERER, ArmorType.LEGGINGS, 0xffe3c88e);
    public static final Item WANDERER_BOOTS = registerDyedArmor("wanderer_boots", EnvironmentzArmorMaterials.WANDERER, ArmorType.BOOTS, 0xffe3c88e);

    public static final Item WOOL_HELMET = registerDyedArmor("wool_helmet", EnvironmentzArmorMaterials.WOOL, ArmorType.HELMET, 0xffffff);
    public static final Item WOOL_CHESTPLATE = registerDyedArmor("wool_chestplate", EnvironmentzArmorMaterials.WOOL, ArmorType.CHESTPLATE, 0xffffff);
    public static final Item WOOL_LEGGINGS = registerDyedArmor("wool_leggings", EnvironmentzArmorMaterials.WOOL, ArmorType.LEGGINGS, 0xffffff);
    public static final Item WOOL_BOOTS = registerDyedArmor("wool_boots", EnvironmentzArmorMaterials.WOOL, ArmorType.BOOTS, 0xffffff);
    // Potion
    public static final Holder<Potion> COLD_RESISTANCE = registerPotion("cold_resistance", new Potion("cold_resistance", new MobEffectInstance(EffectInit.WARMING, 3600)));
    public static final Holder<Potion> LONG_COLD_RESISTANCE = registerPotion("long_cold_resistance", new Potion("cold_resistance", new MobEffectInstance(EffectInit.WARMING, 9600)));
    public static final Holder<Potion> OVERHEATING_RESISTANCE = registerPotion("overheating_resistance", new Potion("overheating_resistance", new MobEffectInstance(EffectInit.COOLING, 3600)));
    public static final Holder<Potion> LONG_OVERHEATING_RESISTANCE = registerPotion("long_overheating_resistance", new Potion("overheating_resistance", new MobEffectInstance(EffectInit.COOLING, 9600)));

    private static Item registerArmor(String id, ArmorMaterial material, ArmorType type) {
        return register(id, Item::new, new Item.Properties().humanoidArmor(material, type));
    }

    private static Item registerDyedArmor(String id, ArmorMaterial material, ArmorType type, int color) {
        return register(id, Item::new, new Item.Properties().humanoidArmor(material, type).component(DataComponents.DYED_COLOR, new DyedItemColor(color))
                .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.DYED_COLOR, true)));
    }

    private static Item register(String id, Function<Item.Properties, Item> factory, Item.Properties properties) {
        Identifier identifier = EnvironmentzMain.identifierOf(id);
        Item item = Registry.register(BuiltInRegistries.ITEM, identifier, factory.apply(properties.setId(ResourceKey.create(Registries.ITEM, identifier))));
        CreativeModeTabEvents.modifyOutputEvent(ENVIRONMENTZ_ITEM_GROUP).register(entries -> entries.accept(item));
        return item;
    }

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, EnvironmentzMain.identifierOf(name), potion);
    }

    private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, EnvironmentzMain.identifierOf(id), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ENVIRONMENTZ_ITEM_GROUP,
                FabricCreativeModeTab.builder().icon(() -> new ItemStack(WOLF_CHESTPLATE)).title(Component.translatable("item.environmentz.item_group")).build());
    }

}
