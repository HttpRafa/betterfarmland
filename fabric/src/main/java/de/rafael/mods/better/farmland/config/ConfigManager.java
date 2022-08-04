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
import de.rafael.mods.better.farmland.classes.BlockChange;
import de.rafael.mods.better.farmland.config.lib.JsonConfiguration;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigManager {

    public static final int latestConfigVersion = 1;

    private int currentConfigVersion = 1;

    private boolean ignoreUpdates = false;

    // Event
    private boolean preventChange = true;

    // Crops
    private boolean changeBlock = true;
    private final List<BlockChange> changes = new ArrayList<>();

    public boolean load() {

        JsonConfiguration jsonConfiguration = JsonConfiguration.loadConfig(new File("config//better_farmland/"), "config.json");

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

        if(!jsonConfiguration.getJson().has("event")) {
            jsonConfiguration.getJson().add("event", new JsonObject());
        }

        // Mod
        if(!jsonConfiguration.getJson().getAsJsonObject("mod").has("ignoreUpdates")) {
            jsonConfiguration.getJson().getAsJsonObject("mod").addProperty("ignoreUpdates", this.ignoreUpdates);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.ignoreUpdates = jsonConfiguration.getJson().getAsJsonObject("mod").get("ignoreUpdates").getAsBoolean();
        }

        // Event
        if(!jsonConfiguration.getJson().getAsJsonObject("event").has("prevent")) {
            jsonConfiguration.getJson().getAsJsonObject("event").addProperty("prevent", this.preventChange);
            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.preventChange = jsonConfiguration.getJson().getAsJsonObject("event").get("prevent").getAsBoolean();
        }

        if(!jsonConfiguration.getJson().getAsJsonObject("event").has("crops")) {
            jsonConfiguration.getJson().getAsJsonObject("event").add("crops", new JsonObject());
            jsonConfiguration.getJson().getAsJsonObject("event").getAsJsonObject("crops").addProperty("change", this.changeBlock);

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

            jsonConfiguration.getJson().getAsJsonObject("event").getAsJsonObject("crops").add("changes", defaultChangeType);

            jsonConfiguration.saveConfig();

            return false;
        } else {
            this.changeBlock = jsonConfiguration.getJson().getAsJsonObject("event").getAsJsonObject("crops").get("change").getAsBoolean();

            JsonArray changesArray = jsonConfiguration.getJson().getAsJsonObject("event").getAsJsonObject("crops").get("changes").getAsJsonArray();
            for (JsonElement jsonElement : changesArray) {
                JsonObject element = jsonElement.getAsJsonObject();
                if(element.get("use").getAsBoolean()) {
                    BlockChange.ChangeSound sound = null;
                    BlockChange.ChangeDrop drop = null;
                    if(!element.get("sound").isJsonNull()) {
                        Identifier soundIdentifier = new Identifier(element.getAsJsonObject("sound").get("sound").getAsString());
                        sound = new BlockChange.ChangeSound(Registry.SOUND_EVENT.get(soundIdentifier),
                                element.getAsJsonObject("sound").get("volume").getAsFloat(),
                                element.getAsJsonObject("sound").get("pitch").getAsFloat());
                    }
                    if(!element.get("drop").isJsonNull()) {
                        Item minecraftItem = null;
                        if(!element.getAsJsonObject("drop").get("item").getAsString().equals("0")) {
                            Identifier itemIdentifier = new Identifier(element.getAsJsonObject("drop").get("item").getAsString());
                            minecraftItem = Registry.ITEM.get(itemIdentifier);
                        }
                        drop = new BlockChange.ChangeDrop(minecraftItem,
                                element.getAsJsonObject("drop").get("amount").getAsInt());
                    }

                    Item from = null;
                    Item to = null;
                    if(!Objects.equals(element.get("from").getAsString(), "0")) {
                        Identifier itemIdentifier = new Identifier(element.get("from").getAsString());
                        from = Registry.ITEM.get(itemIdentifier);
                    }
                    if(!Objects.equals(element.get("to").getAsString(), "0")) {
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
    }

    public boolean isIgnoreUpdates() {
        return ignoreUpdates;
    }

    public boolean isChangeBlock() {
        return changeBlock;
    }

    public boolean isPreventChange() {
        return preventChange;
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

    @Override
    public String toString() {
        return "ConfigManager{" +
                "currentConfigVersion=" + currentConfigVersion +
                ", ignoreUpdates=" + ignoreUpdates +
                ", preventChange=" + preventChange +
                ", changeBlock=" + changeBlock +
                ", changes=" + changes +
                '}';
    }

}
