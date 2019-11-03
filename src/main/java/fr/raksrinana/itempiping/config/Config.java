package fr.raksrinana.itempiping.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

// @Mod.EventBusSubscriber(modid = Pipe.MOD_ID)
public class Config{
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final CommonConfig COMMON;
	
	static {
		Pair<CommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = commonPair.getLeft();
		COMMON_SPEC = commonPair.getRight();
	}
}
