package com.ldtteam.towntalk;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.PathPackResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Mod("towntalk")
public class TownTalk
{
    Logger logger = LogManager.getLogger("towntalk");

    public TownTalk()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addResourcePack);
    }

    public void addResourcePack(final AddPackFindersEvent event)
    {
        if (event.getPackType() == PackType.CLIENT_RESOURCES)
        {
            try
            {
                final Path resourcePath = ModList.get().getModFileById("towntalk").getFile().findResource("respack");
                final PathPackResources pack = new PathPackResources(ModList.get().getModFileById("towntalk").getFile().getFileName() + ":" + resourcePath, resourcePath);
                final PackMetadataSection  metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                event.addRepositorySource((packConsumer, packConstructor) ->
                                                packConsumer.accept(packConstructor.create(
                                                  "builtin/towntalk", Component.literal("TownTalk"), true,
                                                  () -> pack, metadataSection, Pack.Position.BOTTOM, PackSource.BUILT_IN, false)));

            }
            catch (IOException e)
            {
                logger.warn("Ooopsy", e);
            }

        }
    }
}
