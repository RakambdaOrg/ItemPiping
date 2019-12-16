package fr.raksrinana.itempiping.blocks.pipe.colored;

import fr.raksrinana.itempiping.blocks.pipe.pipe.PipeBlock;
import net.minecraft.item.DyeColor;
import javax.annotation.Nonnull;
import java.util.Objects;

public class ColoredPipeBlock extends PipeBlock{
	public static final String NAME = "pipe";
	private final DyeColor color;
	
	public ColoredPipeBlock(DyeColor color){
		super();
		this.color = color;
	}
	
	@Override
	public DyeColor getColor(){
		return color;
	}
}
