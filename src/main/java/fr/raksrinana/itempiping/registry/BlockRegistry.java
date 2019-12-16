package fr.raksrinana.itempiping.registry;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.blocks.pipe.colored.ColoredPipeBlock;
import fr.raksrinana.itempiping.blocks.pipe.importer.ImporterPipeBlock;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Mod.EventBusSubscriber(modid = ItemPiping.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(ItemPiping.MOD_ID)
public class BlockRegistry{
	public static Collection<ColoredPipeBlock> COLORED_PIPES = new ArrayList<>(DyeColor.values().length);
	@ObjectHolder(ImporterPipeBlock.NAME)
	public static ImporterPipeBlock IMPORTER_PIPE;
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		ItemPiping.LOGGER.info("Registering blocks...");
		IForgeRegistry<Block> registry = event.getRegistry();
		Arrays.stream(DyeColor.values()).forEach(color -> {
			ColoredPipeBlock pipe = new ColoredPipeBlock(color);
			COLORED_PIPES.add(pipe);
			registry.register(pipe.setRegistryName(ItemPiping.getId(color.getName() + "_" + ColoredPipeBlock.NAME)));
		});
		registry.register(new ImporterPipeBlock().setRegistryName(ItemPiping.getId(ImporterPipeBlock.NAME)));
		ItemPiping.LOGGER.info("Blocks registered");
	}
}

