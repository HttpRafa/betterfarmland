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

package de.rafael.mods.better.farmland.classes;

//------------------------------
//
// This class was developed by Rafael K.
// On 07/16/2022 at 2:55 PM
// In the project BetterFarmland
//
//------------------------------

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public record BlockChange(ChangeSound sound, Block from, Block to, ChangeDrop drop, int newAge) {

    @Override
    public String toString() {
        return "BlockChange{" +
                "sound=" + sound +
                ", from=" + from +
                ", to=" + to +
                ", drop=" + drop +
                ", newAge=" + newAge +
                '}';
    }

    public record ChangeSound(SoundEvent sound, float soundVolume, float soundPitch) {

        @Override
        public String toString() {
            return "ChangeSound{" +
                    "sound=" + sound +
                    ", soundVolume=" + soundVolume +
                    ", soundPitch=" + soundPitch +
                    '}';
        }

    }

    public record ChangeDrop(Item item, int amount) {

        @Override
        public String toString() {
            return "ChangeDrop{" +
                    "item=" + item +
                    ", amount=" + amount +
                    '}';
        }

    }

}
