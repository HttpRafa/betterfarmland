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

package de.rafael.mods.better.farmland.mixin;

//------------------------------
//
// This class was developed by Rafael K.
// On 08/01/2022 at 8:44 PM
// In the project BetterFarmland
//
//------------------------------

import de.rafael.mods.better.farmland.BetterFarmland;
import de.rafael.mods.better.farmland.classes.BlockChange;
import de.rafael.mods.better.farmland.config.ConfigManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandBlockMixin extends Block {

    @Shadow
    public static void setToDirt(BlockState state, World world, BlockPos pos) {}

    public FarmlandBlockMixin(Settings settings) {
        super(settings);
    }

    @Redirect(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    public void onLandedUpon(BlockState state, World world, BlockPos pos) {
        if(!world.isClient()) {
            ConfigManager configManager = BetterFarmland.INSTANCE.getConfigManager();
            if(!configManager.isPreventChange()) {
                setToDirt(state, world, pos);
            }
            if(configManager.isChangeBlock()) {
                BlockPos cropPos = pos.add(0, 1, 0);
                BlockState cropBlockState = world.getBlockState(cropPos);

                List<BlockChange> blockChangeList = configManager.getChangeFor(state.getBlock().asItem());
                for (BlockChange blockChange : blockChangeList) {

                    // Sound
                    if(blockChange.sound() != null) {
                        BlockChange.ChangeSound sound = blockChange.sound();
                        world.playSound(null, pos, sound.sound(), SoundCategory.BLOCKS, sound.soundVolume(), sound.soundPitch());
                    }

                    // Drop
                    if(blockChange.drop() != null) {
                        BlockChange.ChangeDrop drop = blockChange.drop();
                        Item item = drop.item();
                        if(item == null && world instanceof ServerWorld serverWorld) {
                            List<ItemStack> itemStacks = Block.getDroppedStacks(cropBlockState, serverWorld, cropPos, null);
                            for (ItemStack itemStack : itemStacks) {
                                Block.dropStack(world, cropPos, itemStack);
                            }
                        } else {
                            Block.dropStack(world, cropPos, new ItemStack(item, drop.amount()));
                        }
                    }

                    if(cropBlockState.getBlock() instanceof CropBlock cropBlock) {
                        int oldAge = cropBlockState.get(cropBlock.getAgeProperty());

                        if((blockChange.to() != blockChange.from()) && blockChange.to() != null) {
                            world.setBlockState(cropPos, Block.getBlockFromItem(blockChange.to()).getDefaultState(), Block.NOTIFY_LISTENERS);
                        }

                        int age;
                        if(blockChange.newAge() == -1) {
                            age = oldAge;
                        } else {
                            age = blockChange.newAge();
                        }

                        if(Block.getBlockFromItem(blockChange.to()) instanceof CropBlock) {
                            world.setBlockState(cropPos, cropBlock.withAge(age), Block.NOTIFY_LISTENERS);
                        }
                    } else {
                        if((blockChange.to() != blockChange.from()) && blockChange.to() != null) {
                            world.setBlockState(cropPos, Block.getBlockFromItem(blockChange.to()).getDefaultState(), Block.NOTIFY_LISTENERS);
                        }
                    }

                }
            }
        }
    }

}
