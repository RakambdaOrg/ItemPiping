package fr.raksrinana.itempiping.blocks.pipe.routing;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.blocks.pipe.pipe.PipeBlock;
import fr.raksrinana.itempiping.blocks.pipe.pipe.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class Router{
	private static final Collection<Network> networks = new ArrayList<>();
	
	public static void removePipe(@Nonnull World world, @Nonnull BlockPos pos){
		getNetworkFor(world, pos).ifPresent(network -> {
			getNetworks().remove(network);
			Collection<Network> newNetworks = buildNetworksAround(world, pos);
			getNetworks().addAll(newNetworks);
			ItemPiping.LOGGER.debug("Removed {}", network);
			ItemPiping.LOGGER.debug("Added {}", newNetworks);
		});
	}
	
	private static Collection<Network> buildNetworksAround(@Nonnull World world, @Nonnull BlockPos pos){
		Collection<BlockPos> aroundPositions = Arrays.stream(Direction.values()).map(pos::offset).collect(Collectors.toList());
		Collection<Network> networks = new ArrayList<>();
		for(BlockPos aroundPos : aroundPositions){
			if(networks.stream().noneMatch(network -> network.containsPipe(aroundPos))){
				buildNetworkFrom(world, aroundPos).ifPresent(networks::add);
			}
		}
		return networks;
	}
	
	private static Network buildNetworkRecursive(@Nonnull IWorldReader world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull Network network){
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			if(!network.containsPipe(pos)){
				network.addPipe(pos);
				final BlockState state = world.getBlockState(pos);
				Arrays.stream(Direction.values()).forEach(direction -> {
					if(PipeBlock.getPipeTileEntity(world, pos).map(pipeTileEntity -> pipeTileEntity.getConnection(state, direction)).orElse(false)){
						buildNetworkRecursive(world, pos.offset(direction), direction.getOpposite(), network);
					}
				});
			}
		}
		else{
			if(Objects.nonNull(side)){
				final PipeDestination destination = new PipeDestination(pos, side);
				if(!network.contains(pos, side) && isValidExternalConnection(world, destination)){
					network.addExternalConnection(destination);
				}
			}
		}
		return network;
	}
	
	private static boolean isValidExternalConnection(@Nonnull IWorldReader world, @Nonnull PipeDestination destination){
		TileEntity te = world.getTileEntity(destination.getPos());
		return Objects.nonNull(te) && !(te instanceof PipeTileEntity) && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, destination.getSide()).isPresent();
	}
	
	@Nonnull
	public static Optional<Network> getNetworkFor(@Nonnull IWorldReader world, @Nonnull BlockPos pos){
		Optional<Network> existingNetwork = getNetworks().stream().filter(network -> network.isSameWorld(world.getDimension().getType())).filter(network -> network.containsPipe(pos)).findAny();
		if(existingNetwork.isPresent()){
			return existingNetwork;
		}
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			ItemPiping.LOGGER.debug("Building unknown network");
			Optional<Network> network = buildNetworkFrom(world, pos);
			network.ifPresent(networks::add);
			return network;
		}
		return Optional.empty();
	}
	
	public static void neighborChange(@Nonnull IWorldReader world, @Nonnull BlockPos pos, @Nonnull BlockPos changePos){
		Block blockIn = world.getBlockState(changePos).getBlock();
		if(blockIn instanceof PipeBlock){
			return;
		}
		getNetworkFor(world, pos).ifPresent(network -> Arrays.stream(Direction.values()).filter(direction -> Objects.equals(pos, changePos.offset(direction))).findAny().ifPresent(side -> {
			final PipeDestination destination = new PipeDestination(changePos, side);
			if(isValidExternalConnection(world, destination)){
				network.addExternalConnection(destination);
			}
			else{
				network.removeExternalConnection(destination);
			}
			ItemPiping.LOGGER.trace("Updated connections {}", network);
		}));
	}
	
	public static void attachPipe(@Nonnull World world, @Nonnull BlockPos pos){
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			Collection<Network> aroundNetworks = getNetworksAround(world, pos);
			if(aroundNetworks.size() < 1){
				buildNetworkFrom(world, pos).ifPresent(network -> {
					networks.add(network);
					ItemPiping.LOGGER.debug("Created {}", network);
				});
			}
			else{
				Network mergedNetwork = aroundNetworks.stream().collect(() -> new Network(world.dimension.getType()), Network::merge, Network::merge);
				mergedNetwork.addPipe(pos);
				getExternalConnectionsFor(world, pos).forEach(mergedNetwork::addExternalConnection);
				getNetworks().add(mergedNetwork);
				ItemPiping.LOGGER.debug("Merged {}", mergedNetwork);
			}
			getNetworks().removeAll(aroundNetworks);
		}
	}
	
	@Nonnull
	private static Optional<Network> buildNetworkFrom(@Nonnull IWorldReader world, @Nonnull BlockPos pos){
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			Network network = buildNetworkRecursive(world, pos, null, new Network(world.getDimension().getType()));
			if(network.getPipes().size() >= 1){
				return Optional.of(network);
			}
		}
		return Optional.empty();
	}
	
	@Nonnull
	private static Collection<PipeDestination> getExternalConnectionsFor(@Nonnull World world, @Nonnull BlockPos pos){
		return Arrays.stream(Direction.values()).map(direction -> {
			BlockPos aroundPos = pos.offset(direction);
			Direction side = direction.getOpposite();
			TileEntity te = world.getTileEntity(pos.offset(direction));
			if(Objects.nonNull(te) && !(te instanceof PipeTileEntity) && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent()){
				return new PipeDestination(aroundPos, side);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Nonnull
	private static Collection<Network> getNetworksAround(@Nonnull World world, @Nonnull BlockPos pos){
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if(block instanceof PipeBlock){
			PipeBlock pipe = (PipeBlock) block;
			return Arrays.stream(Direction.values()).filter(direction -> {
				final BlockPos otherPos = pos.offset(direction);
				Block otherBlock = world.getBlockState(otherPos).getBlock();
				return otherBlock instanceof PipeBlock && pipe.canPipeConnect(world, pos, direction);
			}).map(pos::offset).map(aroundPos -> getNetworkFor(world, aroundPos)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}
	
	@Nonnull
	private static Collection<Network> getNetworks(){
		return Router.networks;
	}
}
