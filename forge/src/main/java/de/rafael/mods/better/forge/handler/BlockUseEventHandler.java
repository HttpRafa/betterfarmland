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

package de.rafael.mods.better.forge.handler;

//------------------------------
//
// This class was developed by Rafael K.
// On 11/20/2022 at 7:57 PM
// In the project BetterFarmland
//
//------------------------------

import de.rafael.mods.better.farmland.logic.UseBlockLogic;
import net.minecraft.util.ActionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class BlockUseEventHandler {

    @SubscribeEvent
    public void onUse(PlayerInteractEvent.@NotNull RightClickBlock rightClickBlock) {
        var actionResult = UseBlockLogic.interact(rightClickBlock.getLevel(), rightClickBlock.getHand(), rightClickBlock.getHitVec());
        if(actionResult == ActionResult.SUCCESS) {
            rightClickBlock.setCancellationResult(actionResult);
            rightClickBlock.setCanceled(true);
        }
    }

}
