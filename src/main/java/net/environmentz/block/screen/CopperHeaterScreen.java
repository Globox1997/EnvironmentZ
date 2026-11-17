package net.environmentz.block.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class CopperHeaterScreen extends AbstractContainerScreen<CopperHeaterScreenHandler> {

    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath("environmentz", "textures/gui/copper_heater.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.withDefaultNamespace("container/furnace/lit_progress");

    public CopperHeaterScreen(CopperHeaterScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title, 176, 166);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        int x = this.leftPos;
        int y = this.topPos;
        context.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        if (this.menu.isBurning()) {
            int flameHeight = 14;
            int litHeight = Mth.ceil(this.menu.getFuelProgress() * flameHeight);
            context.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_TEXTURE, flameHeight, flameHeight, 0, flameHeight - litHeight, x + 80, y + 36 + flameHeight - litHeight, flameHeight,
                    litHeight);
        }
    }
}
