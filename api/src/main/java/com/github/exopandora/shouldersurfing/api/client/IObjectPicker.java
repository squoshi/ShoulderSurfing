package com.github.exopandora.shouldersurfing.api.client;

import com.github.exopandora.shouldersurfing.api.model.PickContext;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public interface IObjectPicker
{
	HitResult pick(PickContext context, double interactionRange, float partialTick, MultiPlayerGameMode gameMode);
	
	EntityHitResult pickEntities(PickContext context, double interactionRange, float partialTick);
	
	BlockHitResult pickBlocks(PickContext context, double interactionRange, float partialTick);
}
