package fr.raksrinana.itempiping.blocks.pipe.pipe;

import fr.raksrinana.itempiping.blocks.pipe.routing.Route;
import fr.raksrinana.itempiping.blocks.pipe.routing.RoutingResult;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

public class PipeInventoryHandler extends ItemStackHandler{
	private final PipeTileEntity pipeEntity;
	
	public PipeInventoryHandler(@Nonnull PipeTileEntity pipeTileEntity){
		super(1);
		this.pipeEntity = pipeTileEntity;
	}
	
	@Override
	@Nonnull
	public ItemStack insertItem(int slot, final ItemStack stack, boolean simulate){
		if(stack.isEmpty()){
			return ItemStack.EMPTY;
		}
		if(!simulate){
			this.pipeEntity.markDirty();
		}
		return pipeEntity.getNetwork().map(network -> {
			ItemStack remainingStack = stack.copy();
			World world = this.pipeEntity.getWorld();
			if(Objects.nonNull(world)){
				RoutingResult result = network.getDestinationsFor(world, this.pipeEntity.getPos(), stack.copy());
				Collection<Route> routes = result.getRoutes();
				for(Route route : routes){
					final TileEntity te = world.getTileEntity(route.getDestination().getPos());
					if(Objects.nonNull(te)){
						LazyOptional<IItemHandler> capability = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, route.getDestination().getSide());
						if(capability.isPresent()){
							final ItemStack remainingStackCopy = remainingStack.copy();
							remainingStack = capability.map(handler -> dispatchStackToItemHandler(handler, remainingStackCopy.copy(), simulate)).orElse(remainingStackCopy);
						}
					}
				}
			}
			return remainingStack.copy();
		}).orElse(stack.copy());
	}
	
	@Nonnull
	private ItemStack dispatchStackToItemHandler(@Nonnull IItemHandler handler, @Nonnull ItemStack stack, boolean simulate){
		for(int slot = 0; slot < handler.getSlots(); slot++){
			if(handler.isItemValid(slot, stack)){
				stack = handler.insertItem(slot, stack, simulate);
			}
			if(stack.isEmpty()){
				break;
			}
		}
		return stack.copy();
	}
}
