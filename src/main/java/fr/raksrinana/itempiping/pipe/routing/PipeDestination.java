package fr.raksrinana.itempiping.pipe.routing;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class PipeDestination{
	private final BlockPos pos;
	private final Direction side;
	
	public PipeDestination(@Nonnull BlockPos pos, @Nonnull Direction side){
		this.pos = pos;
		this.side = side;
	}
	
	public boolean is(@Nullable BlockPos pos, @Nullable Direction side){
		return Objects.equals(getPos(), pos) && Objects.equals(getSide(), side);
	}
	
	@Nonnull
	public BlockPos getPos(){
		return pos;
	}
	
	@Nonnull
	public Direction getSide(){
		return side;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		PipeDestination that = (PipeDestination) o;
		return pos.equals(that.pos) && side == that.side;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(pos, side);
	}
}
