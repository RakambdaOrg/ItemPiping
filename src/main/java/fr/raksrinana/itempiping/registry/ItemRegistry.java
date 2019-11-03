package fr.raksrinana.itempiping.registry;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.pipe.PipeBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ItemPiping.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistry
{
	private static final ItemGroup CREATIVE_TAB = new ItemGroup(ItemPiping.MOD_ID){
		@Override
		public ItemStack createIcon(){
			return new ItemStack(BlockRegistry.PIPE);
		}
	};
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> registry = event.getRegistry();
		
		registry.register(new BlockItem(BlockRegistry.PIPE, new Item.Properties().group(CREATIVE_TAB)).setRegistryName(new ResourceLocation(ItemPiping.MOD_ID, PipeBlock.NAME)));
	}
}
