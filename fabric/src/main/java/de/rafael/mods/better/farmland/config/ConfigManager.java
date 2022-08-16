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

package de.rafael.mods.better.farmland.config;

//------------------------------
//
// This class was developed by Rafael K.
// On 07/05/2022 at 7:47 PM
// In the project BetterFarmland
//
//------------------------------

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.rafael.mods.better.farmland.BetterFarmland;
import de.rafael.mods.better.farmland.classes.BlockChange;
import de.rafael.mods.better.farmland.config.lib.JsonConfiguration;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigManager {

    public static final int latestConfigVersion = 2;
    public static final File dataFolder = new File("config//betterfarmland/");
    public static final File[] lookForDataFolders = new File[] { new File("config//better_farmland/") };

    private int currentConfigVersion = 1;

    // RightClickHarvest
    private boolean useRightClickHarvest = false;
    private final List<BlockChange.ChangeSound> harvestSounds = new ArrayList<>();

    // LandedUpon
    private boolean preventBreak = true;

    // Crops
    private boolean changeBlock = false;
    private final List<BlockChange> changes = new ArrayList<>();

    public boolean load() {

        for (File lookForDataFolder : lookForDataFolders) {
            if(lookForDataFolder.exists()) {
                try {
                    FileUtils.moveDirectory(lookForDataFolder, dataFolder);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }

        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(dataFolder, "config.json");

        // Config Version
        if(!jsonConfiguration.getJson().has("configVersion")) {
            jsonConfiguration.getJson().addProperty("configVersion", latestConfigVersion);
            jsonConfiguration.saveConfig();
        }
        this.currentConfigVersion = jsonConfiguration.getJson().get("configVersion").getAsInt();
        if(this.currentConfigVersion < latestConfigVersion) {
            updateConfig(this.currentConfigVersion);

            return false;
        }

        if(!jsonConfiguration.getJson().has("mod")) {
            jsonConfiguration.getJson().add("mod", new JsonObject());
        }

        if(!jsonConfiguration.getJson().has("rightClickHarvest")) {
            jsonConfiguration.getJson().add("rightClickHarvest", new JsonObject());
        }

        if(!jsonConfiguration.getJson().has("landedUpon")) {
            jsonConfiguration.getJson().add("landedUpon", new JsonObject());
        }

        // Mod
        // Nothing

        // rightClickHarvest
        if(!jsonConfiguration.getJson().getAsJsonObject("rightClickHarvest").has("use")) {
            jsonConfiguration.getJson().getAsJsonObject("rightClickHarvest").addProperty("use", this.useRightClickHarvest);
            jsonConfiguration.saveConfig();
            return false;
        } else {
            this.useRightClickHarvest = jsonConfiguration.getJson().getAsJsonObject("rightClickHarvest").get("use").getAsBoolean();
        }

        if(!jsonConfiguration.getJson().getAsJsonObject("rightClickHarvest").has("sounds")) {
            JsonArray soundArray = new JsonArray();
            {
                JsonObject soundObject = new JsonObject();
                SoundEvent sound = SoundEvents.BLOCK_CROP_BREAK;
                soundObject.addProperty("sound", sound.getId().toString());
                soundObject.addProperty("volume", 1f);
                soundObject.addProperty("pitch", 1f);
                soundArray.add(soundObject);
            }
            {
                JsonObject soundObject = new JsonObject();
                SoundEvent sound = SoundEvents.BLOCK_PUMPKIN_CARVE;
                soundObject.addProperty("sound", sound.getId().toString());
                soundObject.addProperty("volume", 0.75f);
                soundObject.addProperty("pitch", 1f);
                soundArray.add(soundObject);
            }
            jsonConfiguration.getJson().getAsJsonObject("rightClickHarvest").add("sounds", soundArray);
            jsonConfiguration.saveConfig();
            return false;
        } else {
            JsonArray soundArray = jsonConfiguration.getJson().getAsJsonObject("rightClickHarvest").getAsJsonArray("sounds");
            for (JsonElement jsonElement : soundArray) {
                JsonObject soundObject = jsonElement.getAsJsonObject();
                SoundEvent soundEvent = Registry.SOUND_EVENT.get(new Identifier(soundObject.get("sound").getAsString()));
                BlockChange.ChangeSound sound = new BlockChange.ChangeSound(soundEvent, soundObject.get("volume").getAsFloat(), soundObject.get("pitch").getAsFloat());
                this.harvestSounds.add(sound);
            }
        }

        // onLandedUpon
        if(!jsonConfiguration.getJson().getAsJsonObject("landedUpon").has("preventBreak")) {
            jsonConfiguration.getJson().getAsJsonObject("landedUpon").addProperty("preventBreak", this.preventBreak);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.preventBreak = jsonConfiguration.getJson().getAsJsonObject("landedUpon").get("preventBreak").getAsBoolean();
        }

        if(!jsonConfiguration.getJson().getAsJsonObject("landedUpon").has("crops")) {
            jsonConfiguration.getJson().getAsJsonObject("landedUpon").add("crops", new JsonObject());
            jsonConfiguration.getJson().getAsJsonObject("landedUpon").getAsJsonObject("crops").addProperty("change", this.changeBlock);

            JsonArray defaultChangeType = new JsonArray();
            {
                JsonObject example = new JsonObject();
                example.addProperty("use", false);

                {
                    JsonObject sound = new JsonObject();
                    SoundEvent defaultSound = SoundEvents.BLOCK_CROP_BREAK;
                    sound.addProperty("sound", defaultSound.getId().toString());
                    sound.addProperty("volume", 1f);
                    sound.addProperty("pitch", 1f);
                    example.add("sound", sound);
                }

                example.addProperty("from", 0);
                example.addProperty("to", Registry.ITEM.getId(Items.AIR).toString());

                {
                    JsonObject drop = new JsonObject();
                    drop.addProperty("item", 0);
                    drop.addProperty("amount", -1);
                    example.add("drop", drop);
                }

                example.addProperty("newAge", -1);
                defaultChangeType.add(example);
            }

            {
                JsonObject example = new JsonObject();
                example.addProperty("use", false);

                {
                    JsonObject sound = new JsonObject();
                    SoundEvent defaultSound = SoundEvents.BLOCK_CROP_BREAK;
                    sound.addProperty("sound", defaultSound.getId().toString());
                    sound.addProperty("volume", 1f);
                    sound.addProperty("pitch", 1f);
                    example.add("sound", sound);
                }

                example.addProperty("from", 0);
                example.addProperty("to", 0);
                example.add("drop", null);
                example.addProperty("newAge", 0);
                defaultChangeType.add(example);
            }

            jsonConfiguration.getJson().getAsJsonObject("landedUpon").getAsJsonObject("crops").add("changes", defaultChangeType);

            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.changeBlock = jsonConfiguration.getJson().getAsJsonObject("landedUpon").getAsJsonObject("crops").get("change").getAsBoolean();

            JsonArray changesArray = jsonConfiguration.getJson().getAsJsonObject("landedUpon").getAsJsonObject("crops").get("changes").getAsJsonArray();
            for (JsonElement jsonElement : changesArray) {
                JsonObject element = jsonElement.getAsJsonObject();
                if (element.get("use").getAsBoolean()) {
                    BlockChange.ChangeSound sound = null;
                    BlockChange.ChangeDrop drop = null;
                    if (!element.get("sound").isJsonNull()) {
                        Identifier soundIdentifier = new Identifier(element.getAsJsonObject("sound").get("sound").getAsString());
                        sound = new BlockChange.ChangeSound(Registry.SOUND_EVENT.get(soundIdentifier),
                                element.getAsJsonObject("sound").get("volume").getAsFloat(),
                                element.getAsJsonObject("sound").get("pitch").getAsFloat());
                    }
                    if (!element.get("drop").isJsonNull()) {
                        Item minecraftItem = null;
                        if (!element.getAsJsonObject("drop").get("item").getAsString().equals("0")) {
                            Identifier itemIdentifier = new Identifier(element.getAsJsonObject("drop").get("item").getAsString());
                            minecraftItem = Registry.ITEM.get(itemIdentifier);
                        }
                        drop = new BlockChange.ChangeDrop(minecraftItem,
                                element.getAsJsonObject("drop").get("amount").getAsInt());
                    }

                    Item from = null;
                    Item to = null;
                    if (!Objects.equals(element.get("from").getAsString(), "0")) {
                        Identifier itemIdentifier = new Identifier(element.get("from").getAsString());
                        from = Registry.ITEM.get(itemIdentifier);
                    }
                    if (!Objects.equals(element.get("to").getAsString(), "0")) {
                        Identifier itemIdentifier = new Identifier(element.get("to").getAsString());
                        to = Registry.ITEM.get(itemIdentifier);
                    }

                    int newAge = element.get("newAge").getAsInt();
                    BlockChange blockChange = new BlockChange(sound, from, to, drop, newAge);
                    this.changes.add(blockChange);
                }
            }
        }

        jsonConfiguration.saveConfig();

        return true;

    }

    private void updateConfig(int current) {
        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(dataFolder, "config.json");
        if(current == 1) {
            jsonConfiguration.getJson().addProperty("configVersion", current + 1);

            jsonConfiguration.getJson().add("landedUpon", jsonConfiguration.getJson().get("event").deepCopy());
            jsonConfiguration.getJson().remove("event");

            jsonConfiguration.getJson().getAsJsonObject("landedUpon").add("preventBreak", jsonConfiguration.getJson().getAsJsonObject("landedUpon").get("prevent"));
            jsonConfiguration.getJson().getAsJsonObject("landedUpon").remove("prevent");

            BetterFarmland.LOGGER.info("Config update completed from the version " + current + " to " + (current + 1));
        }
        jsonConfiguration.saveConfig();
    }

    public List<BlockChange.ChangeSound> getHarvestSounds() {
        return harvestSounds;
    }

    public boolean isUseRightClickHarvest() {
        return useRightClickHarvest;
    }

    public boolean isChangeBlock() {
        return changeBlock;
    }

    public boolean isPreventBreak() {
        return preventBreak;
    }

    public int getConfigVersion() {
        return currentConfigVersion;
    }

    public List<BlockChange> getChanges() {
        return changes;
    }

    public List<BlockChange> getChangeFor(Item from) {
        return this.changes.stream().filter(item -> item.from() == from || item.from() == null).collect(Collectors.toList());
    }

}
