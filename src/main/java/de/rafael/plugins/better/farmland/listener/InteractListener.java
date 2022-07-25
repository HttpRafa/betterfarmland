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

package de.rafael.plugins.better.farmland.listener;

//------------------------------
//
// This class was developed by Rafael K.
// On 07/16/2022 at 2:28 PM
// In the project BetterFarmland
//
//------------------------------

import de.rafael.plugins.better.farmland.BetterFarmland;
import de.rafael.plugins.better.farmland.classes.BlockChange;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InteractListener implements Listener {

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FARMLAND) {
            Block crops = event.getClickedBlock().getRelative(BlockFace.UP);

            // Farmland destroyed
            if(BetterFarmland.getInstance().getConfigManager().isChangeBlock()) {
                applyChanges(crops);
            }

            if(BetterFarmland.getInstance().getConfigManager().isPreventChange()) {
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void on(EntityInteractEvent event) {
        if(event.getEntityType() != EntityType.PLAYER && event.getBlock().getType() == Material.FARMLAND) {
            Block crops = event.getBlock().getRelative(BlockFace.UP);

            // Farmland destroyed
            if(BetterFarmland.getInstance().getConfigManager().isChangeBlock()) {
                applyChanges(crops);
            }

            if(BetterFarmland.getInstance().getConfigManager().isPreventChange()) {
                event.setCancelled(true);
            }
        }

    }

    public void applyChanges(Block crops) {
        BetterFarmland.getInstance().getPluginStats().triggered();

        List<BlockChange> blockChanges = BetterFarmland.getInstance().getConfigManager().getChangeFor(crops.getType());
        for (BlockChange blockChange : blockChanges) {

            // Sound
            if(blockChange.sound() != null) {
                BlockChange.ChangeSound sound = blockChange.sound();
                crops.getWorld().playSound(crops.getLocation(), sound.sound(), sound.soundVolume(), sound.soundPitch());
            }

            // Drop
            if(blockChange.drop() != null) {
                BlockChange.ChangeDrop drop = blockChange.drop();
                Material material = drop.item();
                if(material == null) {
                    for (ItemStack cropsDrop : crops.getDrops()) {
                        crops.getWorld().dropItemNaturally(crops.getLocation(), cropsDrop);
                    }
                } else {
                    crops.getWorld().dropItemNaturally(crops.getLocation(), new ItemStack(material, drop.amount()));
                }
            }

            Integer oldAge = null;
            if(crops.getBlockData() instanceof Ageable ageable) {
                oldAge = ageable.getAge();
            }
            if((blockChange.to() != blockChange.from()) && blockChange.to() != null) {
                crops.setType(blockChange.to(), true);
            }
            if(crops.getBlockData() instanceof Ageable ageable) {
                int age;
                if(blockChange.newAge() == -1 && oldAge != null) {
                    age = oldAge;
                } else {
                    age = blockChange.newAge();
                }
                ageable.setAge(age);
                crops.setBlockData(ageable, true);
            }

        }
    }

}
