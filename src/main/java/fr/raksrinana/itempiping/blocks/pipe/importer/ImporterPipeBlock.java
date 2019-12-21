package fr.raksrinana.itempiping.blocks.pipe.importer;

import fr.raksrinana.itempiping.blocks.pipe.pipe.PipeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import javax.annotation.Nonnull;
import static net.minecraft.state.properties.BlockStateProperties.FACING;

public class ImporterPipeBlock extends PipeBlock{
	public static final String NAME = "importer_pipe";
	
	public ImporterPipeBlock(){
		super();
	}
	
	public static Direction getImportingSide(@Nonnull BlockState state){
		return state.get(FACING);
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return new ImporterPipeTileEntity();
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		super.fillStateContainer(builder);
		builder.add(FACING);
	}
	
	@Override
	public BlockState preGetStateForPlacement(@Nonnull BlockItemUseContext context, @Nonnull BlockState state){
		return state.with(FACING, context.getFace().getOpposite());
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation){
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirror){
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}
	
	@Override
	public boolean canPipeConnect(@Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Direction direction, @Nonnull BlockState state){
		if(direction == state.get(FACING)){
			return true;
		}
		return super.canPipeConnect(world, pos, direction, state);
	}
}
