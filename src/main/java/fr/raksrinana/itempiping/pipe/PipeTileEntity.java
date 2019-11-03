package fr.raksrinana.itempiping.pipe;

import fr.raksrinana.itempiping.pipe.routing.Network;
import fr.raksrinana.itempiping.pipe.routing.Router;
import fr.raksrinana.itempiping.registry.TileEntityRegistry;
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
		this(TileEntityRegistry.TE_TYPE_PIPE);
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
			return this.lazyInventoryHandler.cast();
		}
		return super.getCapability(cap, side);
	}
	
	public boolean canStackPassThrough(@Nonnull ItemStack itemStack, @Nonnull Direction side){
		return true;
	}
	
	@Nonnull
	public Optional<Network> getNetwork(){
		return Router.getNetworkFor(world, pos);
	}
}
