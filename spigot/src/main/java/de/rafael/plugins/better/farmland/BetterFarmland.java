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

package de.rafael.plugins.better.farmland;

//------------------------------
//
// This class was developed by Rafael K.
// On 2/25/2022 at 1:47 PM
// In the project BetterFarmland
//
//------------------------------

import de.rafael.plugins.better.farmland.config.ConfigManager;
import de.rafael.plugins.better.farmland.stats.PluginStats;
import de.rafael.plugins.better.farmland.update.PluginVersion;
import de.rafael.plugins.better.farmland.listener.InteractListener;
import de.rafael.plugins.better.farmland.update.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterFarmland extends JavaPlugin {

    private static BetterFarmland betterFarmland;
    private static PluginVersion version;

    private final String prefix = "§8➜ §6B§eetterFarmland §8● §7";

    private ConfigManager configManager;
    private PluginStats pluginStats;

    private UpdateChecker updateChecker;

    @Override
    public void onLoad() {
        betterFarmland = this;
        version = new PluginVersion().from(getDescription().getVersion());

        Bukkit.getConsoleSender().sendMessage(prefix + "§7Loading §e" + getDescription().getName() + " §7version §6" + version.toString());

        this.configManager = new ConfigManager();
        this.pluginStats = new PluginStats();

        int amount = 1;
        while (!this.configManager.load()) {
            amount++;
        }
        this.pluginStats.load();

        Bukkit.getConsoleSender().sendMessage(prefix + "§7The config §aloaded §7in §e" + amount + " §7cycles§8.");
    }

    @Override
    public void onEnable() {
        this.updateChecker = new UpdateChecker(103677);
        if(this.configManager.isbStats()) {
            int pluginId = 15917;
            Metrics metrics = new Metrics(this, pluginId);
            metrics.addCustomChart(new SingleLineChart("farmland_protected", () -> this.pluginStats.getEventTriggered()));
        }
        if(!this.configManager.isIgnoreUpdates()) {
            this.updateChecker.isLatestVersion(version, aBoolean -> {
                if (!aBoolean) {
                    Bukkit.getConsoleSender().sendMessage(prefix + "§8--------------------------------------");
                    Bukkit.getConsoleSender().sendMessage(prefix + " ");
                    Bukkit.getConsoleSender().sendMessage(prefix + "§7The plugin §e" + getDescription().getName() + " §7has an §aupdate§8.");
                    Bukkit.getConsoleSender().sendMessage(prefix + "§7Current Version§8: §3" + getDescription().getVersion() + " §7Latest Version§8: §a" + this.updateChecker.getLatestVersion());
                    Bukkit.getConsoleSender().sendMessage(prefix + " ");
                    Bukkit.getConsoleSender().sendMessage(prefix + "§8--------------------------------------");
                } else {
                    Bukkit.getConsoleSender().sendMessage(prefix + "§7The §e" + getDescription().getName() + " §7plugin is §aup to date§8.");
                }
            });
        }

        // Events
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);

    }

    @Override
    public void onDisable() {
        this.pluginStats.save();
    }

    public String getPrefix() {
        return prefix;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PluginStats getPluginStats() {
        return pluginStats;
    }

    public static BetterFarmland getInstance() {
        return betterFarmland;
    }

    public static PluginVersion getVersion() {
        return version;
    }

}
