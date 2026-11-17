package net.environmentz.mixin.client;

import net.environmentz.temperature.TemperatureHudRendering;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Hud.class)
public abstract class InGameHudMixin {

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        TemperatureHudRendering.tick(Minecraft.getInstance());
    }

}
