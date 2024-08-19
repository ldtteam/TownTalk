package com.ldtteam.towntalk;

import com.ldtteam.towntalk.generation.DefaultSoundProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Optional;

@Mod("towntalk")
public class TownTalk
{
    public TownTalk(final FMLModContainer modContainer, final Dist dist)
    {
        final IEventBus modBus = modContainer.getEventBus();

        modBus.addListener(this::dataGeneratorSetup);
        modBus.addListener(this::addResourcePack);
    }

    public void dataGeneratorSetup(final GatherDataEvent event)
    {
        final DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new DefaultSoundProvider(generator));
    }

    public void addResourcePack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            var resourcePath = ModList.get().getModFileById("towntalk").getFile().findResource("respack");

            var pack = Pack.readMetaAndCreate(
              new PackLocationInfo("mod/builtin/towntalk", Component.literal("Town Talk"), PackSource.BUILT_IN, Optional.of(new KnownPack("towntalk", "mod/builtin/towntalk", "1.0"))),
              BuiltInPackSource.fromName((path) -> new PathPackResources(path, resourcePath)),
              PackType.CLIENT_RESOURCES,
              new PackSelectionConfig(true, Pack.Position.BOTTOM, false));
        }
    }
}
