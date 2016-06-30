package com.arzeyt.theDarkness.tower;

import org.lwjgl.opengl.GL11;

import com.arzeyt.theDarkness.TheDarkness;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderTower extends TileEntitySpecialRenderer{

	public static final ResourceLocation texture = new ResourceLocation(TheDarkness.MODID+":"+"textures/models/Tower.png");
	private ModelTower model;
	
	public RenderTower(){
		this.model=new ModelTower();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float ptt) {
		GL11.glPushMatrix();
			GL11.glTranslatef((float)x+0.5F, (float)y+1.5f, (float)z+0.5F);
			GL11.glRotatef(180, 0F, 0F, 1F);
		
			
			
			this.bindTexture(texture);
			GL11.glPushMatrix();
				TileEntityTower te = (TileEntityTower)entity;
				if(te.renderTransitionToEmpty && te.renderTransitionToEmptyHandled==false){
					if(te.towerModel.transitionToEmpty2(te, te.animationOffset)){
						te.setRenderTransitionToEmptyHandled(true);
					}
				}else if(te.renderTransitionToFull && te.renderTransitionToFullHandled==false){
					if(te.towerModel.transitionToLight2(te, te.animationOffset)){
						te.setRenderTransitionToFullHandled(true);
					}
				}else if(te.renderTransitionToEmpty==false && te.renderTransitionToEmptyHandled){
					te.towerModel.renderEmpty();
				}else if(te.renderTransitionToFull==false && te.renderTransitionToFullHandled){
					te.towerModel.renderFull();
				}
				
				te.towerModel.renderModel(0.0625F);
			GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	public float toRadians(float degrees){
		return (float) (degrees*(Math.PI/180));
	}
	
}
