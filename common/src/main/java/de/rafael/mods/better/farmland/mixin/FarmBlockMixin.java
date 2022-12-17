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
