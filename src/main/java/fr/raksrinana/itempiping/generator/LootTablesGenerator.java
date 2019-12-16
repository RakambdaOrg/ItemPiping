package fr.raksrinana.itempiping.generator;

import fr.raksrinana.itempiping.blocks.pipe.colored.ColoredPipeBlock;
import fr.raksrinana.itempiping.blocks.pipe.importer.ImporterPipeBlock;
import fr.raksrinana.itempiping.registry.BlockRegistry;
import net.minecraft.data.DataGenerator;

public class LootTablesGenerator extends BaseLootTableProvider{
	public LootTablesGenerator(DataGenerator generator){
		super(generator);
	}
	
	@Override
	protected void addTables(){
		BlockRegistry.COLORED_PIPES.forEach(coloredPipeBlock -> lootTables.put(coloredPipeBlock, createStandardTable(coloredPipeBlock.getColor().getName() + "_" + ColoredPipeBlock.NAME, coloredPipeBlock)));
		lootTables.put(BlockRegistry.IMPORTER_PIPE, createStandardTable(ImporterPipeBlock.NAME, BlockRegistry.IMPORTER_PIPE));
	}
}
