package fr.raksrinana.itempiping.registry;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.blocks.pipe.colored.ColoredPipeBlock;
import fr.raksrinana.itempiping.blocks.pipe.importer.ImporterPipeBlock;
import fr.raksrinana.itempiping.blocks.pipe.importer.ImporterPipeTileEntity;
import fr.raksrinana.itempiping.blocks.pipe.pipe.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = ItemPiping.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(ItemPiping.MOD_ID)
public class TileEntityRegistry{
	@ObjectHolder(ColoredPipeBlock.NAME)
	public static TileEntityType<PipeTileEntity> PIPE;
	@ObjectHolder(ImporterPipeBlock.NAME)
	public static TileEntityType<ImporterPipeTileEntity> IMPORTER_PIPE;
	
	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event){
		ItemPiping.LOGGER.info("Registering tileentities...");
		event.getRegistry().register(TileEntityType.Builder.create(PipeTileEntity::new, BlockRegistry.COLORED_PIPES.toArray(new Block[0])).build(null).setRegistryName(ItemPiping.getId(ColoredPipeBlock.NAME)));
		event.getRegistry().register(TileEntityType.Builder.create(ImporterPipeTileEntity::new, BlockRegistry.IMPORTER_PIPE).build(null).setRegistryName(ItemPiping.getId(ImporterPipeBlock.NAME)));
		ItemPiping.LOGGER.info("TileEntities registered");
	}
}

