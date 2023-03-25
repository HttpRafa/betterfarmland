/*
 * MIT License
 *
 * Copyright (c) 2022-2023.
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
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
public abstract class FarmBlockMixin extends Block {

    @Shadow
    public static void setToDirt(Entity entity, BlockState state, World world, BlockPos pos) {

    }

    public FarmBlockMixin(Settings settings) {
        super(settings);
    }

    @Redirect(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V"))
    public void onLandedUpon(Block instance, World world, BlockState blockState, BlockPos blockPos, Entity entity, float fallDistance) {
        if (!world.isClient()) {
            ConfigManager configManager = BetterFarmland.getConfigManager();

            if (!configManager.isPreventBreak()) {
                setToDirt(entity, blockState, world, blockPos);
            }
            if (configManager.isChangeBlock()) {
                BlockPos cropPos = blockPos.add(0, 1, 0);
                BlockState cropBlockState = world.getBlockState(cropPos);

                List<BlockChange> blockChangeList = configManager.getChangeFor(cropBlockState.getBlock());
                for (BlockChange blockChange : blockChangeList) {

                    // Sound
                    if(blockChange.sound() != null) {
                        BlockChange.ChangeSound sound = blockChange.sound();
                        world.playSound(null, blockPos, sound.sound(), SoundCategory.BLOCKS, sound.soundVolume(), sound.soundPitch());
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

                        int age;
                        if(blockChange.newAge() == -1) {
                            age = oldAge;
                        } else {
                            age = blockChange.newAge();
                        }

                        if(cropBlockState.getBlock() instanceof CropBlock) {
                            world.setBlockState(cropPos, cropBlock.withAge(age), Block.NOTIFY_ALL);
                        }
                    }
                    if((blockChange.to() != blockChange.from()) && blockChange.to() != null) {
                        world.setBlockState(cropPos, blockChange.to().getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
            }
        }
    }

}
