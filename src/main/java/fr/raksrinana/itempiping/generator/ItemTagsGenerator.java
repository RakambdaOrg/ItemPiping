package fr.raksrinana.itempiping.generator;

import fr.raksrinana.itempiping.registry.TagsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;

public class ItemTagsGenerator extends ItemTagsProvider{
	public ItemTagsGenerator(DataGenerator generator){
		super(generator);
	}
	
	@Override
	protected void registerTags(){
		this.copy(TagsRegistry.COLORED_PIPE_BLOCK, TagsRegistry.COLORED_PIPE_ITEM);
	}
}
