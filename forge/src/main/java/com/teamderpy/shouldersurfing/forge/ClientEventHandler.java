package com.teamderpy.shouldersurfing.forge;

import com.teamderpy.shouldersurfing.client.KeyHandler;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.client.ShoulderRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("removal")
public class ClientEventHandler
{
	@SubscribeEvent
	public static void clientTickEvent(ClientTickEvent event)
	{
		if(Phase.START.equals(event.phase))
		{
			ShoulderInstance.getInstance().tick();
		}
	}
	
	@SubscribeEvent
	public static void preRenderLivingEntityEvent(RenderLivingEvent.Pre<LivingEntity, ?> event)
	{
		if(event.isCancelable() && event.getEntity().equals(Minecraft.getInstance().getCameraEntity()) && Minecraft.getInstance().screen == null && ShoulderRenderer.getInstance().skipEntityRendering())
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void preRenderGuiOverlayEvent(RenderGameOverlayEvent.PreLayer event)
	{
		if(ForgeIngameGui.CROSSHAIR_ELEMENT.equals(event.getOverlay()))
		{
			ShoulderRenderer.getInstance().offsetCrosshair(event.getMatrixStack(), event.getWindow(), event.getPartialTicks());
		}
		//Using BOSS_EVENT_PROGRESS to pop matrix because when CROSSHAIR is cancelled it will not fire RenderGuiOverlayEvent.Post and cause a stack overflow
		else if(ForgeIngameGui.BOSS_HEALTH_ELEMENT.equals(event.getOverlay()))
		{
			ShoulderRenderer.getInstance().clearCrosshairOffset(event.getMatrixStack());
		}
	}
	
	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void computeCameraAnglesEvent(CameraSetup event)
	{
		ShoulderRenderer.getInstance().offsetCamera(event.getCamera(), Minecraft.getInstance().level, event.getPartialTicks());
	}
	
	@SubscribeEvent
	@SuppressWarnings({"deprecation", "resource"})
	public static void renderLevelStageEvent(RenderLevelLastEvent event)
	{
		ShoulderRenderer.getInstance().updateDynamicRaytrace(Minecraft.getInstance().gameRenderer.getMainCamera(), event.getPoseStack().last().pose(), event.getProjectionMatrix(), event.getPartialTick());
	}
	
	@SubscribeEvent
	public static void keyInputEvent(InputEvent event)
	{
		KeyHandler.onInput();
	}
}