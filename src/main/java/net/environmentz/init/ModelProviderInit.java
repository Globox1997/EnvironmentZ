package net.environmentz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModelProviderInit {

    public static void init() {
        ModelPredicateProviderRegistry.register(ItemInit.HEATING_STONES_ITEM, new Identifier("heated"), (stack, world, entity, seed) -> {
            if (entity == null)
                return 0.0F;
            else
                return stack.getMaxDamage() - stack.getDamage() > 1 ? 1.0F : 0.0F;
        });
        ModelPredicateProviderRegistry.register(ItemInit.ICE_PACK_ITEM, new Identifier("melting"), (stack, world, entity, seed) -> {
            if (entity == null)
                return 0.0F;
            else
                return stack.getDamage() == 0 ? 0.0F : 1.0F;
        });

        ModelPredicateProviderRegistry.register(ItemInit.ICE_PACK_ITEM, new Identifier("melt"), (stack, world, entity, seed) -> {
            if (entity == null)
                return 0.0F;
            else
                return stack.getDamage() == 0 ? 0.0F : (float) stack.getDamage() / stack.getMaxDamage();
        });
    }
}
