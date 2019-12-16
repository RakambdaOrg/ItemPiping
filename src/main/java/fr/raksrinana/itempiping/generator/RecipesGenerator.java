package fr.raksrinana.itempiping.generator;

import fr.raksrinana.itempiping.registry.BlockRegistry;
import fr.raksrinana.itempiping.registry.ItemRegistry;
import fr.raksrinana.itempiping.registry.TagsRegistry;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import java.util.function.Consumer;

public class RecipesGenerator extends RecipeProvider{
	public RecipesGenerator(DataGenerator generator){
		super(generator);
	}
	
	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer){
		BlockRegistry.COLORED_PIPES.stream().filter(coloredPipeBlock -> coloredPipeBlock.getColor() == DyeColor.WHITE).findAny().ifPresent(coloredPipeBlock -> ShapedRecipeBuilder.shapedRecipe(coloredPipeBlock).patternLine("IGI").key('I', Items.IRON_INGOT).key('G', Blocks.GLASS).addCriterion("glass", InventoryChangeTrigger.Instance.forItems(Blocks.GLASS)).addCriterion("iron_ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)).build(consumer));
		BlockRegistry.COLORED_PIPES.stream().filter(coloredPipeBlock -> coloredPipeBlock.getColor() != DyeColor.WHITE).forEach(coloredPipeBlock -> ShapelessRecipeBuilder.shapelessRecipe(coloredPipeBlock).addIngredient(DyeItem.getItem(coloredPipeBlock.getColor())).addIngredient(TagsRegistry.COLORED_PIPE_ITEM).addCriterion("pipe", InventoryChangeTrigger.Instance.forItems(BlockRegistry.COLORED_PIPES.toArray(new Block[0]))).build(consumer));
		ShapedRecipeBuilder.shapedRecipe(ItemRegistry.WRENCH).patternLine(" I ").patternLine(" PI").patternLine("I  ").key('I', Items.IRON_INGOT).key('P', TagsRegistry.COLORED_PIPE_ITEM).addCriterion("pipe", InventoryChangeTrigger.Instance.forItems(BlockRegistry.COLORED_PIPES.toArray(new Block[0]))).addCriterion("iron_ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT)).build(consumer);
		ShapedRecipeBuilder.shapedRecipe(BlockRegistry.IMPORTER_PIPE).patternLine(" P ").patternLine(" H ").key('P', TagsRegistry.COLORED_PIPE_ITEM).key('H', Blocks.HOPPER).addCriterion("pipe", InventoryChangeTrigger.Instance.forItems(BlockRegistry.COLORED_PIPES.toArray(new Block[0]))).addCriterion("hopper", InventoryChangeTrigger.Instance.forItems(Blocks.HOPPER)).build(consumer);
	}
}
