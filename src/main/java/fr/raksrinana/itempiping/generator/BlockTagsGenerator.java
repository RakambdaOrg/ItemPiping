package fr.raksrinana.itempiping.generator;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.registry.BlockRegistry;
import fr.raksrinana.itempiping.registry.TagsRegistry;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;

public class BlockTagsGenerator extends BlockTagsProvider{
	public BlockTagsGenerator(DataGenerator generator){super(generator);}
	
	@Override
	protected void registerTags(){
		this.getBuilder(TagsRegistry.COLORED_PIPE_BLOCK).add(BlockRegistry.COLORED_PIPES.toArray(new Block[0]));
		this.getBuilder(TagsRegistry.PIPE_BLOCK).add(TagsRegistry.COLORED_PIPE_BLOCK).add(BlockRegistry.IMPORTER_PIPE);
	}
}
