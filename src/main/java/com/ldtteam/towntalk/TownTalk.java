package com.ldtteam.towntalk;

import com.ldtteam.towntalk.generation.DefaultSoundProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

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
        event.addPackFinders(ResourceLocation.fromNamespaceAndPath("towntalk", "respack"), PackType.CLIENT_RESOURCES, Component.literal("Town Talk"), PackSource.BUILT_IN, true, Pack.Position.TOP);
    }
}
