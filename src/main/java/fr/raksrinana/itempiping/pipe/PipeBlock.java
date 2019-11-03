package fr.raksrinana.itempiping.pipe;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.pipe.routing.Router;
import fr.raksrinana.itempiping.registry.TileEntityRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import javax.annotation.Nonnull;
import java.util.Objects;

public class PipeBlock extends Block implements IBucketPickupHandler, ILiquidContainer{
	public static final String NAME = "pipe";
	protected final VoxelShape[] shapes;
	
	public PipeBlock(){
		super(Block.Properties.from(Blocks.IRON_BLOCK));
		setRegistryName(new ResourceLocation(ItemPiping.MOD_ID, NAME));
		this.shapes = this.buildShapes();
	}
	
	@Override
	public boolean allowsMovement(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull PathType type){
		return false;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state){
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world){
		return TileEntityRegistry.TE_TYPE_PIPE.create();
	}
	
	@Override
	@Deprecated
	public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, BlockState newState, boolean isMoving){
		if(!state.getBlock().equals(newState.getBlock())){
			if(!world.isRemote){
				Router.removePipe(world, pos);
			}
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}
	
	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean wat){
		if(!world.isRemote){
			Router.neighborChange(world, pos, fromPos, blockIn);
		}
		super.neighborChanged(state, world, pos, blockIn, fromPos, wat);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(!world.isRemote){
			Router.attachPipe(world, pos);
		}
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(SixWayBlock.DOWN, SixWayBlock.UP, SixWayBlock.NORTH, SixWayBlock.SOUTH, SixWayBlock.WEST, SixWayBlock.EAST, BlockStateProperties.WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		IBlockReader world = context.getWorld();
		BlockPos pos = context.getPos();
		IFluidState fluidState = context.getWorld().getFluidState(context.getPos());
		return super.getStateForPlacement(context).with(SixWayBlock.DOWN, canConnectTo(world, pos, Direction.DOWN)).with(SixWayBlock.UP, canConnectTo(world, pos, Direction.UP)).with(SixWayBlock.NORTH, canConnectTo(world, pos, Direction.NORTH)).with(SixWayBlock.SOUTH, canConnectTo(world, pos, Direction.SOUTH)).with(SixWayBlock.WEST, canConnectTo(world, pos, Direction.WEST)).with(SixWayBlock.EAST, canConnectTo(world, pos, Direction.EAST)).with(BlockStateProperties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}
	
	@Override
	@Nonnull
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos){
		if(stateIn.get(BlockStateProperties.WATERLOGGED)){
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		return stateIn.with(SixWayBlock.FACING_TO_PROPERTY_MAP.get(facing), canConnectTo(worldIn, currentPos, facing));
	}
	
	public static boolean canConnectTo(IBlockReader world, BlockPos pos, Direction direction){
		BlockPos connectBlockPos = pos.offset(direction);
		BlockState connectBlockState = world.getBlockState(connectBlockPos);
		Block connectBlock = connectBlockState.getBlock();
		if(connectBlock instanceof PipeBlock){
			return true;
		}
		TileEntity te = world.getTileEntity(connectBlockPos);
		return Objects.nonNull(te) && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction.getOpposite()).isPresent();
	}
	
	protected VoxelShape[] buildShapes(){
		final double MIN_VOXEL = 0D;
		final double MAX_VOXEL = 16D;
		final double CORE_LENGTH = 6D;
		final double BRANCH_LENGTH = 5D;
		final double BRANCH_OFFSET = 7D;
		final double BRANCH_THICKNESS = 2D;
		VoxelShape[] shapes = new VoxelShape[64];
		VoxelShape core = Block.makeCuboidShape(BRANCH_LENGTH, BRANCH_LENGTH, BRANCH_LENGTH, BRANCH_LENGTH + CORE_LENGTH, BRANCH_LENGTH + CORE_LENGTH, BRANCH_LENGTH + CORE_LENGTH);
		VoxelShape down = Block.makeCuboidShape(BRANCH_OFFSET, MIN_VOXEL, BRANCH_OFFSET, BRANCH_OFFSET + BRANCH_THICKNESS, BRANCH_LENGTH, BRANCH_OFFSET + BRANCH_THICKNESS);
		VoxelShape up = Block.makeCuboidShape(BRANCH_OFFSET, BRANCH_LENGTH, BRANCH_OFFSET, BRANCH_OFFSET + BRANCH_THICKNESS, MAX_VOXEL, BRANCH_OFFSET + BRANCH_THICKNESS);
		VoxelShape north = Block.makeCuboidShape(BRANCH_OFFSET, BRANCH_OFFSET, MIN_VOXEL, BRANCH_OFFSET + BRANCH_THICKNESS, BRANCH_OFFSET + BRANCH_THICKNESS, BRANCH_LENGTH);
		VoxelShape south = Block.makeCuboidShape(BRANCH_OFFSET, BRANCH_OFFSET, BRANCH_LENGTH, BRANCH_OFFSET + BRANCH_THICKNESS, BRANCH_OFFSET + BRANCH_THICKNESS, MAX_VOXEL);
		VoxelShape west = Block.makeCuboidShape(MIN_VOXEL, BRANCH_OFFSET, BRANCH_OFFSET, BRANCH_LENGTH, BRANCH_OFFSET + BRANCH_THICKNESS, BRANCH_OFFSET + BRANCH_THICKNESS);
		VoxelShape east = Block.makeCuboidShape(BRANCH_LENGTH, BRANCH_OFFSET, BRANCH_OFFSET, MAX_VOXEL, BRANCH_OFFSET + BRANCH_THICKNESS, BRANCH_OFFSET + BRANCH_THICKNESS);
		VoxelShape[] sides = {
				down,
				up,
				north,
				south,
				west,
				east
		};
		for(int i = 0; i < 64; i++){
			shapes[i] = core;
			for(int j = 0; j < 6; j++){
				if((i & (1 << j)) != 0){
					shapes[i] = VoxelShapes.or(shapes[i], sides[j]);
				}
			}
		}
		return shapes;
	}
	
	@Override
	@Nonnull
	public VoxelShape getRenderShape(BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos){
		return state.getShape(worldIn, pos);
	}
	
	@Override
	@Nonnull
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, ISelectionContext context){
		return this.getShape(state, worldIn, pos, context);
	}
	
	@Override
	@Nonnull
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return this.shapes[this.getShapeIndex(state)];
	}
	
	public int getShapeIndex(BlockState state){
		int index = 0;
		for(int j = 0; j < Direction.values().length; ++j){
			if(state.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(Direction.byIndex(j)))){
				index |= 1 << j;
			}
		}
		return index;
	}
	
	@Override
	@Nonnull
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean canContainFluid(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull Fluid fluidIn){
		return !state.get(BlockStateProperties.WATERLOGGED) && fluidIn == Fluids.WATER;
	}
	
	@Override
	public boolean receiveFluid(@Nonnull IWorld worldIn, @Nonnull BlockPos pos, BlockState state, @Nonnull IFluidState fluidStateIn){
		if(!state.get(BlockStateProperties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER){
			if(!worldIn.isRemote()){
				worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, true), 3);
				worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	@Override
	@Nonnull
	public Fluid pickupFluid(@Nonnull IWorld worldIn, @Nonnull BlockPos pos, BlockState state){
		if(state.get(BlockStateProperties.WATERLOGGED)){
			worldIn.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, false), 3);
			return Fluids.WATER;
		}
		else{
			return Fluids.EMPTY;
		}
	}
	
	@Override
	@Nonnull
	public IFluidState getFluidState(BlockState state){
		return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}
}
