package net.environmentz.mixin.client;

import net.environmentz.temperature.TemperatureHudRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class InGameHudMixin {

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        TemperatureHudRendering.tick(Minecraft.getInstance());
    }

}
