package fr.raksrinana.itempiping.blocks.pipe.pipe;

import fr.raksrinana.itempiping.blocks.pipe.routing.Network;
import fr.raksrinana.itempiping.blocks.pipe.routing.Router;
import fr.raksrinana.itempiping.registry.TileEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class PipeTileEntity extends TileEntity{
	private final PipeInventoryHandler inventoryHandler;
	private final LazyOptional<IItemHandler> lazyInventoryHandler;
	
	public PipeTileEntity(){
		this(TileEntityRegistry.PIPE);
	}
	
	public PipeTileEntity(TileEntityType<?> tileEntityTypeIn){
		super(tileEntityTypeIn);
		this.inventoryHandler = new PipeInventoryHandler(this);
		this.lazyInventoryHandler = LazyOptional.of(() -> this.inventoryHandler);
	}
	
	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side){
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null){
			return this.getInventoryHandlerOptional().cast();
		}
		return super.getCapability(cap, side);
	}
	
	protected PipeInventoryHandler getInventoryHandler(){
		return this.inventoryHandler;
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
	
	protected LazyOptional<IItemHandler> getInventoryHandlerOptional(){
		return this.lazyInventoryHandler;
	}
	
	@Nonnull
	public Optional<Network> getNetwork(){
		return Optional.ofNullable(getWorld()).flatMap(world -> Router.getNetworkFor(world, pos));
	}
}
