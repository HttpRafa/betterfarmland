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

package de.rafael.mods.better.farmland.logic;

//------------------------------
//
// This class was developed by Rafael K.
// On 11/20/2022 at 6:40 PM
// In the project BetterFarmland
//
//------------------------------

import de.rafael.mods.better.farmland.BetterFarmland;
import de.rafael.mods.better.farmland.classes.BlockChange;
import de.rafael.mods.better.farmland.mixin.CropBlockInvoker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.Optional;

public class UseBlockLogic {

    public static InteractionResult interact(Level world, InteractionHand hand, BlockHitResult hitResult) {
        if(!world.isClientSide() && hand == InteractionHand.MAIN_HAND && world instanceof ServerLevel serverWorld) {
            BlockState blockState = world.getBlockState(hitResult.getBlockPos());
            if (blockState.getBlock() instanceof CropBlock cropBlock && blockState.getValue(cropBlock.getAgeProperty()) == cropBlock.getMaxAge()) {
                List<ItemStack> itemStacks = Block.getDrops(blockState, serverWorld, hitResult.getBlockPos(), null);
                Optional<ItemStack> seedStack = itemStacks.stream().filter(itemStack -> itemStack.is(((CropBlockInvoker)cropBlock).invokeGetSeedsItem().asItem())).findFirst();
                if(seedStack.isPresent()) {
                    int newCount = seedStack.get().getCount() - 1;
                    if(newCount < 1) {
                        itemStacks.remove(seedStack.get());
                    } else {
                        seedStack.get().setCount(newCount);
                    }
                }
                for (ItemStack itemStack : itemStacks) {
                    Block.popResource(world, hitResult.getBlockPos(), itemStack);
                }

                world.setBlock(hitResult.getBlockPos(), cropBlock.getStateForAge(0), Block.UPDATE_ALL);

                for (BlockChange.ChangeSound harvestSound : BetterFarmland.getConfigManager().getHarvestSounds()) {
                    world.playSound(null, hitResult.getBlockPos(), harvestSound.sound(), SoundSource.BLOCKS, harvestSound.soundVolume(), harvestSound.soundPitch());
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

}
