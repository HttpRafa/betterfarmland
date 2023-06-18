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

package de.rafael.mods.better.farmland.config.lib;

//------------------------------
//
// This class was developed by Rafael K.
// On 31.12.2021 at 12:37
// In the project BetterFarmland
//
//------------------------------

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JsonConfiguration {

    private final File file;
    private JsonObject jsonObject;

    public JsonConfiguration(File file, JsonObject jsonObject) {

        this.file = file;
        this.jsonObject = jsonObject;

    }

    public void clear() {
        this.jsonObject = new JsonObject();
    }

    public JsonObject getJson() {
        return jsonObject;
    }

    public void setJson(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public File getFile() {
        return file;
    }

    public void saveConfig() {

        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().setPrettyPrinting().create();

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8));
            try {
                out.write(gson.toJson(jsonObject));
            } finally {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Contract("_, _ -> new")
    public static @NotNull JsonConfiguration loadConfig(@NotNull File folder, String fileName) {

        if(!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder.getPath() + "/" + fileName);

        JsonObject jsonObject = new JsonObject();

        if(!file.exists()) {
            try {

                file.createNewFile();

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("{}");
                fileWriter.flush();
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            try {

                JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                jsonObject = JsonParser.parseReader(jsonReader).getAsJsonObject();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        return new JsonConfiguration(file, jsonObject);

    }

}