package com.ldtteam.towntalk.generation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Data generator for the sounds.json file.
 */
public class DefaultSoundProvider implements DataProvider
{
    private static final String GENERAL_PREFIX  = "general";
    private static final String CRAFTER_PREFIX  = "crafter";
    private static final String EMPLOYED_PREFIX = "employed";

    private final DataGenerator generator;

    private final List<DirectorySpec> directoriesToTraverse;

    public DefaultSoundProvider(final DataGenerator generator)
    {
        this.generator = generator;

        this.directoriesToTraverse = List.of(
          new DirectorySpec("builder", true, false),
          new DirectorySpec("deliveryman", true, false),
          new DirectorySpec("miner", true, false),
          new DirectorySpec("lumberjack", true, false),
          new DirectorySpec("farmer", true, false),
          new DirectorySpec("undertaker", true, false),
          new DirectorySpec("fisherman", true, false),
          new DirectorySpec("baker", true, true),
          new DirectorySpec("cook", true, true),
          new DirectorySpec("shepherd", true, false),
          new DirectorySpec("cowboy", true, false),
          new DirectorySpec("swineherder", true, false),
          new DirectorySpec("chickenherder", true, false),
          new DirectorySpec("smelter", true, true),
          new DirectorySpec("ranger", true, false),
          new DirectorySpec("knight", true, false),
          new DirectorySpec("composter", true, false),
          new DirectorySpec("student", true, false),
          new DirectorySpec("archertraining", true, false),
          new DirectorySpec("combattraining", true, false),
          new DirectorySpec("sawmill", true, true),
          new DirectorySpec("blacksmith", true, true),
          new DirectorySpec("stonemason", true, true),
          new DirectorySpec("stonesmeltery", true, true),
          new DirectorySpec("crusher", true, true),
          new DirectorySpec("sifter", true, false),
          new DirectorySpec("florist", true, false),
          new DirectorySpec("enchanter", true, false),
          new DirectorySpec("researcher", true, false),
          new DirectorySpec("healer", true, false),
          new DirectorySpec("pupil", true, false),
          new DirectorySpec("teacher", true, false),
          new DirectorySpec("glassblower", true, true),
          new DirectorySpec("dyer", true, true),
          new DirectorySpec("fletcher", true, true),
          new DirectorySpec("mechanic", true, true),
          new DirectorySpec("planter", true, false),
          new DirectorySpec("rabbitherder", true, false),
          new DirectorySpec("concretemixer", true, true),
          new DirectorySpec("beekeeper", true, false),
          new DirectorySpec("cookassistant", true, true),
          new DirectorySpec("netherworker", true, false),
          new DirectorySpec("quarrier", true, false),
          new DirectorySpec("druid", true, false),
          new DirectorySpec("alchemist", true, true),
          new DirectorySpec("visitor", false, false),
          new DirectorySpec("unemployed", false, false));
    }

    @Override
    public void run(final @NotNull CachedOutput cache) throws IOException
    {
        final JsonObject sounds = new JsonObject();
        final Path outputFolder = this.generator.getOutputFolder();
        final Path sourceFolder = outputFolder.getParent().getParent().getParent().resolve("main/resources/respack/assets/minecolonies/sounds");

        try (final Stream<Path> genders = Files.list(sourceFolder))
        {
            for (final Path gender : genders.toList())
            {
                final String genderName = sourceFolder.relativize(gender).toString();

                final Map<String, List<Path>> generalCategories = new HashMap<>();
                parseCategories(gender.resolve(GENERAL_PREFIX), generalCategories);

                for (final DirectorySpec spec : directoriesToTraverse)
                {
                    final Map<String, List<Path>> allCategories = generalCategories.entrySet().stream()
                                                                    .collect(Collectors.toMap(Entry::getKey, e -> new ArrayList<>(e.getValue())));

                    if (Files.exists(gender.resolve(spec.name)))
                    {
                        parseCategories(gender.resolve(spec.name), allCategories);
                    }

                    if (spec.isJob)
                    {
                        parseCategories(gender.resolve(EMPLOYED_PREFIX), allCategories);
                    }

                    if (spec.isCrafter)
                    {
                        parseCategories(gender.resolve(CRAFTER_PREFIX), allCategories);
                    }

                    for (final Entry<String, List<Path>> entry : allCategories.entrySet())
                    {
                        final JsonArray soundsArray = new JsonArray();
                        for (final Path soundPath : entry.getValue())
                        {
                            final JsonObject soundArrayItem = new JsonObject();
                            soundArrayItem.addProperty("name", new ResourceLocation("minecolonies", sourceFolder.relativize(soundPath).toString().replace('\\', '/')).toString());
                            soundArrayItem.addProperty("stream", false);
                            soundsArray.add(soundArrayItem);
                        }

                        final JsonObject soundObject = new JsonObject();
                        soundObject.addProperty("category", "neutral");
                        soundObject.addProperty("replace", true);
                        soundObject.add("sounds", soundsArray);
                        sounds.add("citizen." + spec.name + "." + genderName + "." + entry.getKey(), soundObject);
                    }
                }
            }
        }

        DataProvider.saveStable(cache, sounds, outputFolder.resolve("respack/assets/minecolonies/sounds.json"));
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Default Sound Json Provider";
    }

    private void parseCategories(final Path root, final Map<String, List<Path>> target) throws IOException
    {
        try (final Stream<Path> sounds = Files.walk(root))
        {
            for (final Path sound : sounds.filter(Files::isRegularFile).toList())
            {
                final Path relative = root.relativize(sound);
                final String[] split = relative.toString().split(Pattern.quote("\\"));

                if (split.length == 1)
                {
                    target.putIfAbsent("general", new ArrayList<>());
                    target.get("general").add(sound);
                }
                else if (split.length > 1)
                {
                    target.putIfAbsent(split[0], new ArrayList<>());
                    target.get(split[0]).add(sound);
                }
            }
        }
    }

    private record DirectorySpec(String name, boolean isJob, boolean isCrafter)
    {
    }
}
