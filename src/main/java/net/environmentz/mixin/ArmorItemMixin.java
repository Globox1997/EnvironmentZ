package net.environmentz.mixin;

import java.util.List;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ArmorItem.class)
public class ArmorItemMixin extends Item {

    public ArmorItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound tag = stack.getNbt();
        if (stack.hasNbt()) {
            if (tag.contains("environmentz")) {
                tooltip.add(Text.translatable("item.environmentz.fur_insolated.tooltip").formatted(Formatting.BLUE));
            }
            if (tag.contains("iced")) {
                tooltip.add(Text.translatable("item.environmentz.iced.tooltip", tag.getInt("iced")).formatted(Formatting.BLUE));
            }
        }

    }

}
