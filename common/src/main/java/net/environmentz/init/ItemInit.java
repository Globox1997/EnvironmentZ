package net.environmentz.init;

import com.mojang.serialization.Codec;
import net.environmentz.EnvironmentzMain;
import net.environmentz.item.*;
import net.minecraft.core.Holder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ItemInit {

    // Item Group
    public static final ResourceKey<CreativeModeTab> ENVIRONMENTZ_ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("environmentz", "item_group"));
    private static final List<Item> CREATIVE_TAB_ITEMS = new ArrayList<>();

    public static final int COOLING_HEATING_VALUE = 120;
    // Component
    public static DataComponentType<Integer> ICED;
    public static DataComponentType<Boolean> INSULATED;
    // Item
    public static Item HEATING_STONES_ITEM;
    public static Item WOLF_PELT_ITEM;
    public static Item POLAR_BEAR_FUR_ITEM;
    public static Item ICE_PACK_ITEM;
    // Armor
    public static Item WOLF_HELMET;
    public static Item WOLF_CHESTPLATE;
    public static Item WOLF_LEGGINGS;
    public static Item WOLF_BOOTS;

    public static Item WANDERER_HELMET;
    public static Item WANDERER_CHESTPLATE;
    public static Item WANDERER_LEGGINGS;
    public static Item WANDERER_BOOTS;

    public static Item WOOL_HELMET;
    public static Item WOOL_CHESTPLATE;
    public static Item WOOL_LEGGINGS;
    public static Item WOOL_BOOTS;
    // Potion
    public static Holder<Potion> COLD_RESISTANCE;
    public static Holder<Potion> LONG_COLD_RESISTANCE;
    public static Holder<Potion> OVERHEATING_RESISTANCE;
    public static Holder<Potion> LONG_OVERHEATING_RESISTANCE;

    public static void registerComponents(BiConsumer<Identifier, DataComponentType<?>> registrar) {
        ICED = registerComponent(registrar, "iced", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
        INSULATED = registerComponent(registrar, "insulated", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    }

    public static void register(BiConsumer<Identifier, Item> registrar) {
        BlockInit.registerItems((id, item) -> {
            registrar.accept(id, item);
            CREATIVE_TAB_ITEMS.add(item);
        });

        HEATING_STONES_ITEM = register(registrar, "heating_stones", HeatingStones::new, new Item.Properties().durability(COOLING_HEATING_VALUE));
        WOLF_PELT_ITEM = register(registrar, "wolf_pelt", WolfPeltItem::new, new Item.Properties());
        POLAR_BEAR_FUR_ITEM = register(registrar, "polar_bear_fur", PolarBearFurItem::new, new Item.Properties());
        ICE_PACK_ITEM = register(registrar, "ice_pack", IcePack::new, new Item.Properties().durability(COOLING_HEATING_VALUE));

        WOLF_HELMET = registerArmor(registrar, "wolf_helmet", EnvironmentzArmorMaterials.WOLF, ArmorType.HELMET);
        WOLF_CHESTPLATE = registerArmor(registrar, "wolf_chestplate", EnvironmentzArmorMaterials.WOLF, ArmorType.CHESTPLATE);
        WOLF_LEGGINGS = registerArmor(registrar, "wolf_leggings", EnvironmentzArmorMaterials.WOLF, ArmorType.LEGGINGS);
        WOLF_BOOTS = registerArmor(registrar, "wolf_boots", EnvironmentzArmorMaterials.WOLF, ArmorType.BOOTS);

        WANDERER_HELMET = registerDyedArmor(registrar, "wanderer_helmet", EnvironmentzArmorMaterials.WANDERER, ArmorType.HELMET, 0xffe3c88e);
        WANDERER_CHESTPLATE = registerDyedArmor(registrar, "wanderer_chestplate", EnvironmentzArmorMaterials.WANDERER, ArmorType.CHESTPLATE, 0xffe3c88e);
        WANDERER_LEGGINGS = registerDyedArmor(registrar, "wanderer_leggings", EnvironmentzArmorMaterials.WANDERER, ArmorType.LEGGINGS, 0xffe3c88e);
        WANDERER_BOOTS = registerDyedArmor(registrar, "wanderer_boots", EnvironmentzArmorMaterials.WANDERER, ArmorType.BOOTS, 0xffe3c88e);

        WOOL_HELMET = registerDyedArmor(registrar, "wool_helmet", EnvironmentzArmorMaterials.WOOL, ArmorType.HELMET, 0xffffff);
        WOOL_CHESTPLATE = registerDyedArmor(registrar, "wool_chestplate", EnvironmentzArmorMaterials.WOOL, ArmorType.CHESTPLATE, 0xffffff);
        WOOL_LEGGINGS = registerDyedArmor(registrar, "wool_leggings", EnvironmentzArmorMaterials.WOOL, ArmorType.LEGGINGS, 0xffffff);
        WOOL_BOOTS = registerDyedArmor(registrar, "wool_boots", EnvironmentzArmorMaterials.WOOL, ArmorType.BOOTS, 0xffffff);
    }

    public static void registerPotions(BiConsumer<Identifier, Potion> registrar) {
        COLD_RESISTANCE = registerPotion(registrar, "cold_resistance", new Potion("cold_resistance", new MobEffectInstance(EffectInit.WARMING, 3600)));
        LONG_COLD_RESISTANCE = registerPotion(registrar, "long_cold_resistance", new Potion("cold_resistance", new MobEffectInstance(EffectInit.WARMING, 9600)));
        OVERHEATING_RESISTANCE = registerPotion(registrar, "overheating_resistance", new Potion("overheating_resistance", new MobEffectInstance(EffectInit.COOLING, 3600)));
        LONG_OVERHEATING_RESISTANCE = registerPotion(registrar, "long_overheating_resistance", new Potion("overheating_resistance", new MobEffectInstance(EffectInit.COOLING, 9600)));
    }

    public static CreativeModeTab createCreativeTab(CreativeModeTab.Builder builder) {
        return builder.icon(() -> new ItemStack(WOLF_CHESTPLATE)).title(Component.translatable("item.environmentz.item_group")).build();
    }

    public static List<Item> getCreativeTabItems() {
        return List.copyOf(CREATIVE_TAB_ITEMS);
    }

    private static Item registerArmor(BiConsumer<Identifier, Item> registrar, String id, ArmorMaterial material, ArmorType type) {
        return register(registrar, id, Item::new, new Item.Properties().humanoidArmor(material, type));
    }

    private static Item registerDyedArmor(BiConsumer<Identifier, Item> registrar, String id, ArmorMaterial material, ArmorType type, int color) {
        return register(registrar, id, Item::new, new Item.Properties().humanoidArmor(material, type).component(DataComponents.DYED_COLOR, new DyedItemColor(color))
                .component(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.DYED_COLOR, true)));
    }

    private static Item register(BiConsumer<Identifier, Item> registrar, String id, Function<Item.Properties, Item> factory, Item.Properties properties) {
        Identifier identifier = EnvironmentzMain.identifierOf(id);
        Item item = factory.apply(properties.setId(ResourceKey.create(Registries.ITEM, identifier)));
        registrar.accept(identifier, item);
        CREATIVE_TAB_ITEMS.add(item);
        return item;
    }

    private static Holder<Potion> registerPotion(BiConsumer<Identifier, Potion> registrar, String name, Potion potion) {
        registrar.accept(EnvironmentzMain.identifierOf(name), potion);
        return BuiltInRegistries.POTION.wrapAsHolder(potion);
    }

    private static <T> DataComponentType<T> registerComponent(BiConsumer<Identifier, DataComponentType<?>> registrar, String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        DataComponentType<T> componentType = builderOperator.apply(DataComponentType.builder()).build();
        registrar.accept(EnvironmentzMain.identifierOf(id), componentType);
        return componentType;
    }

}
