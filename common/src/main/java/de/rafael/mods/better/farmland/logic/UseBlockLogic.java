/*
 * MIT License
 *
 * Copyright (c) 2022.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
