package fr.raksrinana.itempiping.blocks.pipe.pipe;

import fr.raksrinana.itempiping.blocks.pipe.routing.Network;
import fr.raksrinana.itempiping.blocks.pipe.routing.Router;
import fr.raksrinana.itempiping.registry.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PipeTileEntity extends TileEntity{
	private static final String NBT_DISABLED_SIDES = "disabledSides";
	private final Map<Direction, IItemHandler> inventoryHandlers;
	private final Map<Direction, LazyOptional<IItemHandler>> lazyInventoryHandlers;
	private Set<Direction> disabledSides;
	
	public PipeTileEntity(){
		this(TileEntityRegistry.PIPE);
	}
	
	public PipeTileEntity(TileEntityType<?> tileEntityTypeIn){
		super(tileEntityTypeIn);
		this.inventoryHandlers = Arrays.stream(Direction.values()).collect(Collectors.toMap(dir -> dir, dir -> new SidedPipeInventoryHandler(this, dir)));
		this.lazyInventoryHandlers = Arrays.stream(Direction.values()).collect(Collectors.toMap(dir -> dir, dir -> LazyOptional.of(() -> this.inventoryHandlers.get(dir))));
		this.disabledSides = new HashSet<>();
	}
	
	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null){
			return this.getInventoryHandlerOptional(side).cast();
		}
		return super.getCapability(cap, side);
	}
	
	public boolean isSideDisabled(@Nonnull Direction side){
		return this.disabledSides.contains(side);
	}
	
	public void disabledSide(@Nonnull Direction side){
		this.disabledSides.add(side);
	}
	
	public void enableSide(@Nonnull Direction side){
		this.disabledSides.remove(side);
	}
	
	protected IItemHandler getInventoryHandler(@Nonnull Direction side){
		return this.inventoryHandlers.get(side);
	}
	
	public boolean canExtract(@Nonnull ItemStack itemStack, @Nonnull BlockState state, @Nonnull Direction side){
		return getConnection(state, side);
	}
	
	public boolean canReceive(@Nonnull ItemStack itemStack, @Nonnull BlockState state, @Nonnull Direction side){
		return getConnection(state, side);
	}
	
	public boolean getConnection(@Nonnull BlockState state, @Nonnull Direction side){
		return PipeBlock.getConnection(state, side);
	}
	
	protected LazyOptional<IItemHandler> getInventoryHandlerOptional(@Nonnull Direction side){
		return this.lazyInventoryHandlers.get(side);
	}
	
	@Nonnull
	public Optional<Network> getNetwork(){
		return Optional.ofNullable(getWorld()).flatMap(world -> Router.getNetworkFor(world, pos));
	}
	
	@Nullable
	@Override
	public SUpdateTileEntityPacket getUpdatePacket(){
		CompoundNBT tag = new CompoundNBT();
		tag.putIntArray(NBT_DISABLED_SIDES, disabledSides.stream().mapToInt(Direction::getIndex).toArray());
		return new SUpdateTileEntityPacket(pos, 1, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		World world = getWorld();
		CompoundNBT tag = pkt.getNbtCompound();
		if(tag.contains(NBT_DISABLED_SIDES)){
			Set<Direction> newDirections = Arrays.stream(tag.getIntArray(NBT_DISABLED_SIDES)).mapToObj(Direction::byIndex).collect(Collectors.toSet());
			if(!Objects.equals(disabledSides, newDirections)){
				disabledSides = newDirections;
				if(Objects.nonNull(world)){
					world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
				}
			}
		}
	}
	
	@Override
	public void read(CompoundNBT tag){
		super.read(tag);
		if(tag.contains(NBT_DISABLED_SIDES)){
			disabledSides = Arrays.stream(tag.getIntArray(NBT_DISABLED_SIDES)).mapToObj(Direction::byIndex).collect(Collectors.toSet());
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag){
		tag.putIntArray(NBT_DISABLED_SIDES, disabledSides.stream().mapToInt(Direction::getIndex).toArray());
		return super.write(tag);
	}
}
