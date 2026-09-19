package net.environmentz.block.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CopperHeaterScreen extends HandledScreen<CopperHeaterScreenHandler> {

    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("environmentz", "textures/gui/copper_heater.png");
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.ofVanilla("container/furnace/lit_progress");

    public CopperHeaterScreen(CopperHeaterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;
        context.drawTexture(BACKGROUND_TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.handler.isBurning()) {
            int flameHeight = 14;
            int litHeight = MathHelper.ceil(this.handler.getFuelProgress() * flameHeight);
            context.drawGuiTexture(LIT_PROGRESS_TEXTURE, flameHeight, flameHeight, 0, flameHeight - litHeight, x + 80, y + 36 + flameHeight - litHeight, flameHeight, litHeight);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
