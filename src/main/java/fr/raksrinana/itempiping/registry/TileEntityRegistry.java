package fr.raksrinana.itempiping.registry;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.pipe.PipeBlock;
import fr.raksrinana.itempiping.pipe.PipeTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = ItemPiping.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TileEntityRegistry{
	@ObjectHolder(ItemPiping.MOD_ID + ":" + PipeBlock.NAME)
	public static TileEntityType<PipeTileEntity> TE_TYPE_PIPE;
	
	@SubscribeEvent
	public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event){
		event.getRegistry().register(TileEntityType.Builder.create(PipeTileEntity::new, BlockRegistry.PIPE).build(null).setRegistryName(new ResourceLocation(ItemPiping.MOD_ID, PipeBlock.NAME)));
	}
}

