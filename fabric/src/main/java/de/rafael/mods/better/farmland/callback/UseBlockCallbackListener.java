/*
 * Copyright (c) 2022. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *         this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *     * Neither the name of the developer nor the names of its contributors
 *         may be used to endorse or promote products derived from this software
 *         without specific prior written permission.
 *     * Redistributions in source or binary form must keep the original package
 *         and class name.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.rafael.mods.better.farmland.callback;

//------------------------------
//
// This class was developed by Rafael K.
// On 08/10/2022 at 8:09 PM
// In the project BetterFarmland
//
//------------------------------

import de.rafael.mods.better.farmland.BetterFarmland;
import de.rafael.mods.better.farmland.classes.BlockChange;
import de.rafael.mods.better.farmland.mixin.CropBlockInvoker;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class UseBlockCallbackListener implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!world.isClient() && hand == Hand.MAIN_HAND && world instanceof ServerWorld serverWorld) {
            BlockState blockState = world.getBlockState(hitResult.getBlockPos());
            if (blockState.getBlock() instanceof CropBlock cropBlock && blockState.get(cropBlock.getAgeProperty()) == cropBlock.getMaxAge()) {
                List<ItemStack> itemStacks = Block.getDroppedStacks(blockState, serverWorld, hitResult.getBlockPos(), null);
                Optional<ItemStack> seedStack = itemStacks.stream().filter(itemStack -> itemStack.isOf(((CropBlockInvoker)cropBlock).invokeGetSeedsItem().asItem())).findFirst();
                if(seedStack.isPresent()) {
                    int newCount = seedStack.get().getCount() - 1;
                    if(newCount < 1) {
                        itemStacks.remove(seedStack.get());
                    } else {
                        seedStack.get().setCount(newCount);
                    }
                }
                for (ItemStack itemStack : itemStacks) {
                    Block.dropStack(world, hitResult.getBlockPos(), itemStack);
                }

                world.setBlockState(hitResult.getBlockPos(), cropBlock.withAge(0), Block.NOTIFY_LISTENERS);

                for (BlockChange.ChangeSound harvestSound : BetterFarmland.INSTANCE.getConfigManager().getHarvestSounds()) {
                    world.playSound(null, hitResult.getBlockPos(), harvestSound.sound(), SoundCategory.BLOCKS, harvestSound.soundVolume(), harvestSound.soundPitch());
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

}
