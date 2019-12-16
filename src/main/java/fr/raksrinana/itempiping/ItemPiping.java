package fr.raksrinana.itempiping;

import fr.raksrinana.itempiping.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import javax.annotation.Nonnull;

@Mod(ItemPiping.MOD_ID)
public class ItemPiping{
	public static final String MOD_ID = "item_piping";
	public static final String MOD_NAME = "ItemPiping";
	public static final String VERSION = "1.1.1";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
	public static final ItemGroup CREATIVE_TAB = new ItemGroup(ItemPiping.MOD_ID){
		@Override
		public ItemStack createIcon(){
			return new ItemStack(BlockRegistry.COLORED_PIPES.stream().map(Block.class::cast).findAny().orElse(Blocks.IRON_BLOCK));
		}
	};
	
	public ItemPiping(){
		DistExecutor.runForDist(() -> SidedProxy.Client::new, () -> SidedProxy.Server::new);
	}
	
	@Nonnull
	public static ResourceLocation getId(@Nonnull String name){
		return new ResourceLocation(MOD_ID, name);
	}
	
	@Nonnull
	public static String getVersion(){
		return ModList.get().getModContainerById(MOD_ID).map(ModContainer::getModInfo).map(IModInfo::getVersion).map(ArtifactVersion::toString).orElse("NONE");
	}
	
	public static boolean isDevBuild(){
		return "NONE".equals(getVersion());
	}
}
