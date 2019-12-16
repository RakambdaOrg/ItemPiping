package fr.raksrinana.itempiping.blocks.pipe.importer;

import fr.raksrinana.itempiping.blocks.pipe.pipe.PipeTileEntity;
import fr.raksrinana.itempiping.registry.TileEntityRegistry;
import fr.raksrinana.itempiping.utils.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import javax.annotation.Nonnull;
import java.util.Objects;

public class ImporterPipeTileEntity extends PipeTileEntity implements ITickableTileEntity{
	private int transferCooldown = -1;
	
	public ImporterPipeTileEntity(){
		this(TileEntityRegistry.IMPORTER_PIPE);
	}
	
	public ImporterPipeTileEntity(TileEntityType<?> tileEntityTypeIn){
		super(tileEntityTypeIn);
	}
	
	@Override
	public boolean canExtract(@Nonnull ItemStack itemStack, @Nonnull BlockState state, @Nonnull Direction side){
		return super.canExtract(itemStack, state, side) && ImporterPipeBlock.getImportingSide(state) != side;
	}
	
	@Override
	public void tick(){
		World world = this.getWorld();
		if(Objects.nonNull(world)){
			if(!world.isRemote()){
				--this.transferCooldown;
				if(!this.isOnTransferCooldown()){
					this.setTransferCooldown(8);
					this.updateImporter();
				}
			}
		}
	}
	
	private void updateImporter(){
		if(this.pullItems()){
			this.setTransferCooldown(8);
			this.markDirty();
		}
	}
	
	private boolean pullItems(){
		World world = this.getWorld();
		BlockPos pos = this.getPos();
		if(Objects.nonNull(world)){
			Direction direction = ImporterPipeBlock.getImportingSide(world.getBlockState(pos));
			Direction pullDirection = direction.getOpposite();
			TileEntity te = world.getTileEntity(pos.offset(direction));
			if(Objects.nonNull(te)){
				return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, pullDirection).map(iItemHandler -> {
					for(int slotIndex = 0; slotIndex < iItemHandler.getSlots(); slotIndex++){
						ItemStack extractedStack = iItemHandler.extractItem(slotIndex, 64, false);
						if(!extractedStack.isEmpty()){
							ItemStack notInserted = this.getInventoryHandler().insertItem(0, extractedStack.copy(), false);
							if(!notInserted.isEmpty()){
								ItemStack notInsertedBack = iItemHandler.insertItem(slotIndex, notInserted.copy(), false);
								if(!notInsertedBack.isEmpty()){
									WorldUtils.popItemstackInWorld(world, pos, direction, notInsertedBack);
								}
							}
							if(notInserted.getCount() < extractedStack.getCount()){
								return true;
							}
						}
					}
					return false;
				}).orElse(false);
			}
		}
		return false;
	}
	
	private boolean isOnTransferCooldown(){
		return this.transferCooldown > 0;
	}
	
	public void setTransferCooldown(int ticks){
		this.transferCooldown = ticks;
	}
}
