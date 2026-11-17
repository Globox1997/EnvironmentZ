package net.environmentz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ModelInit {

    public static void init() {
        ItemProperties.register(ItemInit.HEATING_STONES_ITEM, ResourceLocation.parse("heated"), (stack, world, entity, seed) -> {
            if (entity == null)
                return 0.0F;
            else
                return stack.getMaxDamage() - stack.getDamageValue() > 1 ? 1.0F : 0.0F;
        });
        ItemProperties.register(ItemInit.ICE_PACK_ITEM, ResourceLocation.parse("melting"), (stack, world, entity, seed) -> {
            if (entity == null)
                return 0.0F;
            else
                return stack.getDamageValue() == 0 ? 0.0F : 1.0F;
        });

        ItemProperties.register(ItemInit.ICE_PACK_ITEM, ResourceLocation.parse("melt"), (stack, world, entity, seed) -> {
            if (entity == null)
                return 0.0F;
            else
                return stack.getDamageValue() == 0 ? 0.0F : (float) stack.getDamageValue() / stack.getMaxDamage();
        });
    }
}
