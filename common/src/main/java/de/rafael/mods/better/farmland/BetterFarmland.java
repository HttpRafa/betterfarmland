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
