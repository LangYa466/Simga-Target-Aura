package cn.langya.simgaaura.module;

import org.lwjgl.opengl.GL11;
import tech.konata.phosphate.api.Extension;
import tech.konata.phosphate.api.PApi;
import tech.konata.phosphate.api.events.AttackEvent;
import tech.konata.phosphate.api.events.UpdateEvent;
import tech.konata.phosphate.api.features.ExtensionModule;
import tech.konata.phosphate.api.interfaces.ICategory;
import tech.konata.phosphate.api.interfaces.game.entity.Entity;
import tech.konata.phosphate.api.interfaces.game.entity.Player;
import tech.konata.phosphate.api.interfaces.settings.*;

import java.awt.*;

/**
 * @author LangYa466
 * Date: 2025/03/08 05:47
 */
public class SimgaAura extends ExtensionModule {
    final ColorSetting circleColorSetting = Extension.getAPI().createColorSetting("Color",1,1,1,1);
    final PApi api = Extension.getAPI();
    Player target;

    public SimgaAura() {
        super("Simga Aura", "by langya466.", ICategory.Visual);
        super.addSettings(this.circleColorSetting);
    }

    private Color pulseColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float brightness = (float) Math.abs((System.currentTimeMillis() % 2000L) / 2000.0 + (float) 200) % 1.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], brightness));
    }

    private double easeInOutQuad(double d) {
        return d < 0.5 ? 2.0 * d * d : 1.0 - Math.pow(-2.0 * d + 2.0, 2.0) / 2.0;
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        try {
            if (target == null) return;
            if (target.getHealth() <= 0 || target.isInvisible() || target.isInvisible()) target = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttackEvent(AttackEvent event) {
        try {
            Entity entity = event.getAttackedEntity();
            if (entity instanceof Player) target = (Player) entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRender3DEvent() {
        try {
            System.out.println("onRender3DEvent");
            if (target == null) return;
            System.out.println("Target != null");
            double d = 1500.0;
            double d2 = (double) System.currentTimeMillis() % d;
            boolean bl = d2 > d / 2.0;
            double d3 = d2 / (d / 2.0);
            d3 = !bl ? 1.0 - d3 : d3 - 1.0;
            d3 = this.easeInOutQuad(d3);

            api.getGlStateManager().pushMatrix();
            api.getGlStateManager().disableTexture2D();
            api.getGlStateManager().blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            api.getGlStateManager().enableBlend();
            api.getGlStateManager().disableDepth();
            api.getGlStateManager().disableCull();
            api.getGlStateManager().shadeModel(GL11.GL_SMOOTH);

            // Calculate target position
            // double d4 = target.width;
            // 0.6000000238418579 (写死了 没api)
            double d4 = 0.6000000238418579;
            //         double d5 = (double) target.height + 0.1;
            // 1.8999999523162843 (写死了 没api)
            double d5 = 1.8999999523162843;
            double d6 = target.getLastTickPosX() + (target.getX() - target.getLastTickPosX()) * (double) api.getMinecraft().getRenderPartialTicks() - api.getMinecraft().getLocalPlayer().getViewPosX();
            double d7 = target.getLastTickPosY() + (target.getY() - target.getLastTickPosY()) * (double) api.getMinecraft().getRenderPartialTicks() - api.getMinecraft().getLocalPlayer().getViewPosY() + d5 * d3;
            double d8 = target.getLastTickPosZ() + (target.getZ() - target.getLastTickPosZ()) * (double) api.getMinecraft().getRenderPartialTicks() - api.getMinecraft().getLocalPlayer().getViewPosZ();
            double d9 = d5 / 3.0 * (d3 > 0.5 ? 1.0 - d3 : d3) * (double) (bl ? -1 : 1);

            // Render circle segments
            for (int i = 0; i < 360; i += 5) {
                Color color = new Color(circleColorSetting.getRGB());
                double d10 = d6 - Math.sin((double) i * Math.PI / 180.0) * d4;
                double d11 = d8 + Math.cos((double) i * Math.PI / 180.0) * d4;
                double d12 = d6 - Math.sin((double) (i - 5) * Math.PI / 180.0) * d4;
                double d13 = d8 + Math.cos((double) (i - 5) * Math.PI / 180.0) * d4;
                int blue = pulseColor(color).getBlue();
                int green = pulseColor(color).getGreen();
                int red = pulseColor(color).getRed();
                // 十六进制
                float rgbaAlpha = 255.0f;
                // Render the filled segments using GL11
                GL11.glBegin(GL11.GL_QUADS);
                GL11.glColor4f((float) red / rgbaAlpha,
                        (float) green / rgbaAlpha,
                        (float) blue / rgbaAlpha, 0.0f);
                GL11.glVertex3d(d10, d7 + d9, d11);
                GL11.glVertex3d(d12, d7 + d9, d13);

                GL11.glColor4f((float) red / rgbaAlpha,
                        (float) green / rgbaAlpha,
                        (float) blue / rgbaAlpha, 200.0f);
                GL11.glVertex3d(d12, d7, d13);
                GL11.glVertex3d(d10, d7, d11);
                GL11.glEnd();

                // Render the outline using GlStateManager
                api.getGlStateManager().color((float) red / rgbaAlpha,
                        (float) green / rgbaAlpha,
                        (float) blue / rgbaAlpha, 1.0f);
                GL11.glBegin(GL11.GL_LINES);
                GL11.glVertex3d(d12, d7, d13);
                GL11.glVertex3d(d10, d7, d11);
                GL11.glEnd();
            }

            // Restore OpenGL states using GlStateManager
            api.getGlStateManager().enableTexture2D();
            api.getGlStateManager().enableDepth();
            api.getGlStateManager().enableCull();
            api.getGlStateManager().shadeModel(GL11.GL_FLAT);
            api.getGlStateManager().disableBlend();
            api.getGlStateManager().popMatrix();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
