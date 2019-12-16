package fr.raksrinana.itempiping.registry;

import fr.raksrinana.itempiping.ItemPiping;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;

public class TagsRegistry{
	public static Tag<Block> PIPE_BLOCK = new BlockTags.Wrapper(ItemPiping.getId("pipes"));
	public static Tag<Block> COLORED_PIPE_BLOCK = new BlockTags.Wrapper(ItemPiping.getId("colored_pipes"));
	public static Tag<Item> COLORED_PIPE_ITEM = new ItemTags.Wrapper(ItemPiping.getId("colored_pipes"));
}
