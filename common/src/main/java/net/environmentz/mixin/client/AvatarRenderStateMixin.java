package net.environmentz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.environmentz.access.FreezingRenderStateAccess;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements FreezingRenderStateAccess {

    @Unique
    private boolean freezing = false;

    @Override
    public boolean environmentz$isFreezing() {
        return this.freezing;
    }

    @Override
    public void environmentz$setFreezing(boolean freezing) {
        this.freezing = freezing;
    }

}
