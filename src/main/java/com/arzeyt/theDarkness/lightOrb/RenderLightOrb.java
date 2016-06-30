package com.arzeyt.theDarkness.lightOrb;

import org.lwjgl.opengl.GL11;

import com.arzeyt.theDarkness.TheDarkness;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderLightOrb extends TileEntitySpecialRenderer{

	public static final ResourceLocation texture = new ResourceLocation(TheDarkness.MODID+":"+"textures/models/lightOrbModel.png");
	private ModelLightOrb model;
	
	private float speed;
	private float yRotationAngle=0.10F;
	
	public RenderLightOrb(){
		this.model=new ModelLightOrb();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		
		long time = System.currentTimeMillis() % 36000L;
		//model.rotate(time, 0, 1, 0);
		
		GL11.glPushMatrix();
			GL11.glTranslatef((float)x+0.5F, (float)y+1.5f, (float)z+0.5F);
			GL11.glRotatef(180, 0F, 0F, 1F);
			
			speed = ((TileEntityLightOrb)te).rotationSpeed;
			
			
			yRotationAngle = time*speed; 
	        
	        //GL11.glRotatef(yRotationAngle, 0.0f, 1.0f, 0.0f); //<- Rotate our object around the y axis
			
			this.model.orb.rotateAngleY = yRotationAngle;
			
			this.bindTexture(texture);
			
			GL11.glPushMatrix();
				this.model.renderModel(0.0625F);
			GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
