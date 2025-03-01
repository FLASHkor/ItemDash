package mnm.mods.itemdash.mixin;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mnm.mods.itemdash.LiteModItemDash;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;

@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative extends InventoryEffectRenderer {

    @Shadow
    private GuiTextField searchField;

    @Inject(method = "drawGuiContainerBackgroundLayer(FII)V", at = @At("HEAD"))
    protected void onDrawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        LiteModItemDash.onPreRenderScreen(this, mouseX, mouseY, partialTicks);
    }

    @Inject(method = "drawGuiContainerForegroundLayer(II)V", at = @At("RETURN"))
    protected void onDrawGuiContainerForegroundLayer(int mouseX, int mouseY, CallbackInfo ci) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -this.guiTop, 0);
        LiteModItemDash.onPostRenderScreen(this, mouseX, mouseY);
        GlStateManager.popMatrix();
    }

    @Inject(method = "updateScreen()V", at = @At("HEAD"))
    private void onUpdateScreen(CallbackInfo ci) {
        LiteModItemDash.onUpdateScreen(this);
        fixCreativeInv();
    }

    private void fixCreativeInv() {
        this.searchField.xPosition = this.guiLeft + 82;
    }

    @Inject(method = "handleMouseInput()V", cancellable = true, at = @At("HEAD"))
    private void onHandleMouse(CallbackInfo ci) {
        int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        if (LiteModItemDash.onHandleMouseInput(x, y)) {
            ci.cancel();
        }
    }

    @Inject(method = "mouseClicked(III)V", at = @At("HEAD"))
    private void onMouseClicked(int mx, int my, int mb, CallbackInfo ci) {
        LiteModItemDash.onMouseClicked(mx, my, mb);
    }

    @Inject(method = "mouseReleased(III)V", at = @At("HEAD"))
    private void onMouseReleased(int mx, int my, int mb, CallbackInfo ci) {
        LiteModItemDash.onMouseReleased(mx, my, mb);
    }

    @Inject(method = "keyTyped(CI)V", cancellable = true, at = @At("HEAD"))
    private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (LiteModItemDash.onHandleKeyboardInput(typedChar, keyCode))
            // searching
            ci.cancel();
    }

    public MixinGuiContainerCreative() {
        super(null);
    }
}
