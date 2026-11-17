package net.environmentz.block.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class CopperHeaterScreen extends AbstractContainerScreen<CopperHeaterScreenHandler> {

    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath("environmentz", "textures/gui/copper_heater.png");
    private static final ResourceLocation LIT_PROGRESS_TEXTURE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");

    public CopperHeaterScreen(CopperHeaterScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        context.blit(INVENTORY_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.isBurning()) {
            int flameHeight = 14;
            int litHeight = Mth.ceil(this.menu.getFuelProgress() * flameHeight);
            context.blitSprite(LIT_PROGRESS_TEXTURE, flameHeight, flameHeight, 0, flameHeight - litHeight, x + 80, y + 36 + flameHeight - litHeight, flameHeight, litHeight);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }
}
