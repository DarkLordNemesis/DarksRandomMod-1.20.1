package net.DarkLordNemesis.DarksRandomMod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.DarkLordNemesis.DarksRandomMod.DarksRandomMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorBlockScreen extends AbstractContainerScreen<GeneratorBlockMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(DarksRandomMod.MOD_ID, "textures/gui/gem_polishing_station_gui.png");

    public GeneratorBlockScreen(GeneratorBlockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderEnergyBar(guiGraphics, x, y);
    }

    private void renderEnergyBar(GuiGraphics guiGraphics, int x, int y) {
        int energyBarHeight = menu.getScaledEnergy();
        int energyBarX = x + 10; // Adjust to the correct position
        int energyBarY = y + 10 + (50 - energyBarHeight); // Dynamic height decrease

        // Background bar
        guiGraphics.blit(TEXTURE, x + 10, y + 25, 185, 29, 7, 35); // Adjust UV coordinates

        // Energy level bar
        guiGraphics.blit(TEXTURE, x + 10, energyBarY, 177, 29, 7, energyBarHeight); // Adjust UV coordinates
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (isMouseOverEnergyBar(mouseX, mouseY)) {
            renderEnergyTooltip(guiGraphics, mouseX, mouseY);
        }

    }

    private boolean isMouseOverEnergyBar(int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int energyBarX = x + 10;
        int energyBarY = y + 25;
        int energyBarWidth = 7;
        int energyBarHeight = 35;

        return mouseX >= energyBarX && mouseX <= energyBarX + energyBarWidth &&
                mouseY >= energyBarY && mouseY <= energyBarY + energyBarHeight;
    }

    // Render tooltip showing energy
    private void renderEnergyTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component energyText = Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE");
        guiGraphics.renderTooltip(font, energyText, mouseX, mouseY);
    }
}
