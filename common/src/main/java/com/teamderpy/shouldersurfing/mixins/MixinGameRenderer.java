package com.teamderpy.shouldersurfing.mixins;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.teamderpy.shouldersurfing.client.ShoulderHelper;
import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import com.teamderpy.shouldersurfing.config.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer implements GameRendererAccessor
{
	@Redirect
	(
		method = "pick",
		at = @At
		(
			value = "INVOKE",
			target = "net/minecraft/entity/projectile/ProjectileHelper.getEntityHitResult(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/vector/Vector3d;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/function/Predicate;D)Lnet/minecraft/util/math/EntityRayTraceResult;"
		)
	)
	private EntityRayTraceResult getEntityHitResult(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distanceSq)
	{
		if(ShoulderInstance.getInstance().doShoulderSurfing() && !Config.CLIENT.getCrosshairType().isDynamic())
		{
			return ShoulderHelper.traceEntities(this.getMainCamera(), shooter, Math.sqrt(distanceSq), Minecraft.getInstance().getFrameTime(), true);
		}
		
		return ProjectileHelper.getEntityHitResult(shooter, startVec, endVec, boundingBox, filter, distanceSq);
	}
}
