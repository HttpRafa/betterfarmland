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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(FarmBlock.class)
public abstract class FarmBlockMixin extends Block {

    @Shadow
    public static void turnToDirt(BlockState state, Level world, BlockPos pos) {}

    public FarmBlockMixin(Properties settings) {
        super(settings);
    }

    @Redirect(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    public void fallOn(BlockState blockState, Level level, BlockPos blockPos) {
        if(!level.isClientSide()) {
            ConfigManager configManager = BetterFarmland.getConfigManager();

            if(!configManager.isPreventBreak()) {
                turnToDirt(blockState, level, blockPos);
            }
            if(configManager.isChangeBlock()) {
                BlockPos cropPos = blockPos.offset(0, 1, 0);
                BlockState cropBlockState = level.getBlockState(cropPos);

                List<BlockChange> blockChangeList = configManager.getChangeFor(cropBlockState.getBlock());
                for (BlockChange blockChange : blockChangeList) {

                    // Sound
                    if(blockChange.sound() != null) {
                        BlockChange.ChangeSound sound = blockChange.sound();
                        level.playSound(null, blockPos, sound.sound(), SoundSource.BLOCKS, sound.soundVolume(), sound.soundPitch());
                    }

                    // Drop
                    if(blockChange.drop() != null) {
                        BlockChange.ChangeDrop drop = blockChange.drop();
                        Item item = drop.item();
                        if(item == null && level instanceof ServerLevel serverLevel) {
                            List<ItemStack> itemStacks = Block.getDrops(cropBlockState, serverLevel, cropPos, null);
                            for (ItemStack itemStack : itemStacks) {
                                Block.popResource(level, cropPos, itemStack);
                            }
                        } else {
                            Block.popResource(level, cropPos, new ItemStack(item, drop.amount()));
                        }
                    }

                    if(cropBlockState.getBlock() instanceof CropBlock cropBlock) {
                        int oldAge = cropBlockState.getValue(cropBlock.getAgeProperty());

                        int age;
                        if(blockChange.newAge() == -1) {
                            age = oldAge;
                        } else {
                            age = blockChange.newAge();
                        }

                        if(cropBlockState.getBlock() instanceof CropBlock) {
                            level.setBlock(cropPos, cropBlock.getStateForAge(age), Block.UPDATE_ALL);
                        }
                    }
                    if((blockChange.to() != blockChange.from()) && blockChange.to() != null) {
                        level.setBlock(cropPos, blockChange.to().defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

}
