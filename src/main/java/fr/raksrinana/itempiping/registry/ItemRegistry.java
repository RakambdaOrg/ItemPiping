package fr.raksrinana.itempiping.registry;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.blocks.pipe.colored.ColoredPipeBlock;
import fr.raksrinana.itempiping.blocks.pipe.importer.ImporterPipeBlock;
import fr.raksrinana.itempiping.items.WrenchItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = ItemPiping.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(ItemPiping.MOD_ID)
public class ItemRegistry{
	@ObjectHolder(WrenchItem.NAME)
	public static WrenchItem WRENCH;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		ItemPiping.LOGGER.info("Registering items...");
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(new WrenchItem().setRegistryName(ItemPiping.getId(WrenchItem.NAME)));
		BlockRegistry.COLORED_PIPES.forEach(coloredPipe -> registry.register(new BlockItem(coloredPipe, new Item.Properties().group(ItemPiping.CREATIVE_TAB)).setRegistryName(ItemPiping.getId(coloredPipe.getColor().getName() + "_" + ColoredPipeBlock.NAME))));
		registry.register(new BlockItem(BlockRegistry.IMPORTER_PIPE, new Item.Properties().group(ItemPiping.CREATIVE_TAB)).setRegistryName(ItemPiping.getId(ImporterPipeBlock.NAME)));
		ItemPiping.LOGGER.info("Items registered");
	}
}
