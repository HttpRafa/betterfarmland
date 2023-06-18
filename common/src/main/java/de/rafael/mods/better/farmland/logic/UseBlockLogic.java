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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class UseBlockLogic {

    public static ActionResult interact(@NotNull World world, Hand hand, BlockHitResult hitResult) {
        if(!world.isClient() && hand == Hand.MAIN_HAND && world instanceof ServerWorld serverWorld) {
            BlockState blockState = world.getBlockState(hitResult.getBlockPos());
            if (blockState.getBlock() instanceof CropBlock cropBlock && cropBlock.getAge(blockState) == cropBlock.getMaxAge()) {
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

                world.setBlockState(hitResult.getBlockPos(), cropBlock.withAge(0), Block.NOTIFY_ALL);

                for (BlockChange.ChangeSound harvestSound : BetterFarmland.getConfigManager().getHarvestSounds()) {
                    world.playSound(null, hitResult.getBlockPos(), harvestSound.sound(), SoundCategory.BLOCKS, harvestSound.soundVolume(), harvestSound.soundPitch());
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

}
