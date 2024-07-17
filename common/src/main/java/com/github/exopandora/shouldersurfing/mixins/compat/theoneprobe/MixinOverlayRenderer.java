package com.github.exopandora.shouldersurfing.mixins.compat.theoneprobe;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "mcjty.theoneprobe.rendering.OverlayRenderer")
public class MixinOverlayRenderer
{
	@Redirect
	(
		method = "renderHUD",
		at = @At
		(
			value = "NEW",
			target = "Lnet/minecraft/util/math/RayTraceContext;",
			remap = true
		),
		remap = false
	)
	private static RayTraceContext initClipContext(Vector3d start, Vector3d end, RayTraceContext.BlockMode blockContext, RayTraceContext.FluidMode fluidContext, @NotNull Entity entity)
	{
		if(ShoulderSurfingImpl.getInstance().isShoulderSurfing())
		{
			Minecraft minecraft = Minecraft.getInstance();
			ActiveRenderInfo camera = minecraft.gameRenderer.getMainCamera();
			PickContext pickContext = new PickContext.Builder(camera)
				.withFluidContext(fluidContext)
				.withEntity(entity)
				.build();
			return pickContext.toClipContext(start.distanceTo(end), minecraft.getFrameTime());
		}
		
		return new RayTraceContext(start, end, blockContext, fluidContext, entity);
	}
}