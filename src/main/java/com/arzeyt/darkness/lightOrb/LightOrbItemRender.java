package com.arzeyt.darkness.lightOrb;

import com.arzeyt.darkness.Darkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by Default on 11/9/2015.
 */
public class LightOrbItemRender implements IItemRenderer {
    public final LightOrbModel model;

    public LightOrbItemRender(LightOrbModel model){
        this.model=model;
    }
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type==ItemRenderType.EQUIPPED || type==ItemRenderType.EQUIPPED_FIRST_PERSON
                 || type==ItemRenderType.ENTITY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {

        if(type==ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Darkness.MODID, "textures/models/LightOrbModel.png"));

            GL11.glTranslatef(0F, -1F, 0F);

            GL11.glScalef(1F, 1F, 1F);
            model.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }else if(type==ItemRenderType.EQUIPPED){
            GL11.glPushMatrix();
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Darkness.MODID, "textures/models/LightOrbModel.png"));

            GL11.glTranslatef(0.5F, -1F, 0F);

            GL11.glScalef(1F, 1F, 1F);
            model.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }else if(type==ItemRenderType.ENTITY){
            GL11.glPushMatrix();
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Darkness.MODID, "textures/models/LightOrbModel.png"));

            GL11.glTranslatef(0.5F, -1F, 0F);

            GL11.glScalef(1F, 1F, 1F);
            model.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }
    }
}
