package fr.raksrinana.itempiping.blocks.pipe.pipe;

import net.minecraft.util.Direction;

public class SidedPipeInventoryHandler extends PipeInventoryHandler{
	private final Direction side;
	
	public SidedPipeInventoryHandler(PipeTileEntity pipeTileEntity, Direction side){
		super(pipeTileEntity);
		this.side = side;
	}
	
	@Override
	protected Direction getInsertedSide(){
		return this.side;
	}
}
