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

package de.rafael.mods.better.farmland;

//------------------------------
//
// This class was developed by Rafael K.
// On 11/20/2022 at 6:38 PM
// In the project BetterFarmland
//
//------------------------------

import de.rafael.mods.better.farmland.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterFarmland {

    public static final String MOD_ID = "betterfarmland";
    public static final String CURRENT_VERSION = "1.0.3";

    public static final Logger LOGGER = LoggerFactory.getLogger("betterfarmland");

    private static ConfigManager configManager;

    public static void init() {
        LOGGER.info("Loading BetterFarmland version " + CURRENT_VERSION);
        configManager = new ConfigManager();

        int amount = 1;
        while (!configManager.load()) {
            amount++;
        }
        LOGGER.info("The config loaded in " + amount + " cycles.");
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

}
