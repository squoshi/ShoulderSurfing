package com.github.exopandora.shouldersurfing.mixins;

import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ModelPart.class, priority = 500 /* apply before sodium and iris */)
public class MixinModelPart
{
	@ModifyVariable
	(
		at = @At("HEAD"),
		method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
		index = 8,
		argsOnly = true
	)
	public float render(float alpha)
	{
		return Math.min(alpha, ShoulderSurfingImpl.getInstance().getCameraEntityRenderer().getCameraEntityAlpha());
	}
}
