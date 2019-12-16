package fr.raksrinana.itempiping.generator;

import fr.raksrinana.itempiping.ItemPiping;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ItemPiping.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators{
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event){
		DataGenerator generator = event.getGenerator();
		generator.addProvider(new BlockTagsGenerator(generator));
		generator.addProvider(new ItemTagsGenerator(generator));
		generator.addProvider(new RecipesGenerator(generator));
		generator.addProvider(new LootTablesGenerator(generator));
	}
}
