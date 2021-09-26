package com.teamderpy.shouldersurfing.event;

import org.lwjgl.input.Keyboard;

import com.teamderpy.shouldersurfing.config.Config;
import com.teamderpy.shouldersurfing.config.Perspective;
import com.teamderpy.shouldersurfing.util.ShoulderState;
import com.teamderpy.shouldersurfing.util.ShoulderSurfingHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;

@SideOnly(Side.CLIENT)
public class KeyHandler
{
	private static final String KEY_CATEGORY = "Shoulder Surfing";
	
	public static final KeyBinding KEYBIND_CAMERA_LEFT = new KeyBinding("Camera left", Keyboard.KEY_LEFT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_RIGHT = new KeyBinding("Camera right", Keyboard.KEY_RIGHT, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_IN = new KeyBinding("Camera closer", Keyboard.KEY_UP, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_OUT = new KeyBinding("Camera farther", Keyboard.KEY_DOWN, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_UP = new KeyBinding("Camera up", Keyboard.KEY_PRIOR, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_CAMERA_DOWN = new KeyBinding("Camera down", Keyboard.KEY_NEXT, KeyHandler.KEY_CATEGORY);
	
	public static final KeyBinding KEYBIND_SWAP_SHOULDER = new KeyBinding("Swap shoulder", Keyboard.KEY_O, KeyHandler.KEY_CATEGORY);
	public static final KeyBinding KEYBIND_TOGGLE_SHOULDER_SURFING = new KeyBinding("Toggle perspective", Keyboard.KEY_NONE, KeyHandler.KEY_CATEGORY);
	
	@SubscribeEvent
	public void keyInputEvent(KeyInputEvent event)
	{
		if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().currentScreen == null)
		{
			if(KEYBIND_TOGGLE_SHOULDER_SURFING.getIsKeyPressed())
			{
				if(ShoulderState.doShoulderSurfing())
				{
					ShoulderSurfingHelper.setPerspective(Perspective.FIRST_PERSON);
				}
				else if(Minecraft.getMinecraft().gameSettings.thirdPersonView == Perspective.FIRST_PERSON.getPointOfView())
				{
					ShoulderSurfingHelper.setPerspective(Perspective.SHOULDER_SURFING);
				}
			}
			
			if(ShoulderState.doShoulderSurfing())
			{
				if(KEYBIND_CAMERA_LEFT.getIsKeyPressed())
				{
					Config.CLIENT.adjustCameraLeft();
				}
				
				if(KEYBIND_CAMERA_RIGHT.getIsKeyPressed())
				{
					Config.CLIENT.adjustCameraRight();
				}
				
				if(KEYBIND_CAMERA_OUT.getIsKeyPressed())
				{
					Config.CLIENT.adjustCameraOut();
				}
				
				if(KEYBIND_CAMERA_IN.getIsKeyPressed())
				{
					Config.CLIENT.adjustCameraIn();
				}
				
				if(KEYBIND_CAMERA_UP.getIsKeyPressed())
				{
					Config.CLIENT.adjustCameraUp();
				}
				
				if(KEYBIND_CAMERA_DOWN.getIsKeyPressed())
				{
					Config.CLIENT.adjustCameraDown();
				}
				
				if(KEYBIND_SWAP_SHOULDER.getIsKeyPressed())
				{
					Config.CLIENT.swapShoulder();
				}
			}
			
			if(Minecraft.getMinecraft().gameSettings.keyBindTogglePerspective.isPressed())
			{
				Perspective perspective = Perspective.current();
				Perspective next = perspective.next();
				ShoulderSurfingHelper.setPerspective(next);
				
				if(Config.CLIENT.doRememberLastPerspective())
				{
					Config.CLIENT.setDefaultPerspective(next);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onGuiClosed(GuiOpenEvent event)
	{
		if(event.gui == null)
		{
			Keyboard.enableRepeatEvents(true);
		}
	}
}
