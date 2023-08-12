package com.ldtteam.towntalk;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.PathPackResources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            var resourcePath = ModList.get().getModFileById("towntalk").getFile().findResource("respack");
            var pack = Pack.readMetaAndCreate("builtin/towntalk", Component.literal("Town Talk"), true,
              (path) -> new PathPackResources(path, true, resourcePath), PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.BUILT_IN);
            event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
        }
    }
}
