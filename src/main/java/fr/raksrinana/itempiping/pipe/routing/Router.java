package fr.raksrinana.itempiping.pipe.routing;

import fr.raksrinana.itempiping.ItemPiping;
import fr.raksrinana.itempiping.pipe.PipeBlock;
import fr.raksrinana.itempiping.pipe.PipeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
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
			ItemPiping.LOGGER.info("Removed {}", network);
			ItemPiping.LOGGER.info("Added {}", newNetworks);
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
	
	private static Network buildNetworkRecursive(@Nonnull World world, @Nonnull BlockPos pos, @Nullable Direction side, @Nonnull Network network){
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			if(!network.containsPipe(pos)){
				network.addPipe(pos);
				Arrays.stream(Direction.values()).forEach(direction -> {
					if(PipeBlock.canConnectTo(world, pos, direction)){
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
	
	private static boolean isValidExternalConnection(@Nonnull World world, @Nonnull PipeDestination destination){
		TileEntity te = world.getTileEntity(destination.getPos());
		return Objects.nonNull(te) && !(te instanceof PipeTileEntity) && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, destination.getSide()).isPresent();
	}
	
	@Nonnull
	public static Optional<Network> getNetworkFor(@Nonnull World world, @Nonnull BlockPos pos){
		Optional<Network> existingNetwork = getNetworks().stream().filter(network -> network.isSameWorld(world)).filter(network -> network.containsPipe(pos)).findAny();
		if(existingNetwork.isPresent()){
			return existingNetwork;
		}
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			ItemPiping.LOGGER.info("Building unknown network");
			Optional<Network> network = buildNetworkFrom(world, pos);
			network.ifPresent(networks::add);
			return network;
		}
		return Optional.empty();
	}
	
	public static void neighborChange(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockPos changePos, Block blockIn){
		getNetworkFor(world, pos).ifPresent(network -> {
			if(blockIn instanceof PipeBlock){
				return;
			}
			Arrays.stream(Direction.values()).filter(direction -> Objects.equals(pos, changePos.offset(direction))).findAny().ifPresent(side -> {
				final PipeDestination destination = new PipeDestination(changePos, side);
				if(isValidExternalConnection(world, destination)){
					network.addExternalConnection(destination);
				}
				else{
					network.removeExternalConnection(destination);
				}
				ItemPiping.LOGGER.info("Updated connections {}", network);
			});
		});
	}
	
	public static void attachPipe(@Nonnull World world, @Nonnull BlockPos pos){
		Collection<Network> aroundNetworks = getNetworksAround(world, pos);
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			if(aroundNetworks.size() < 1){
				buildNetworkFrom(world, pos).ifPresent(network -> {
					networks.add(network);
					ItemPiping.LOGGER.info("Created {}", network);
				});
			}
			else{
				Network mergedNetwork = aroundNetworks.stream().collect(() -> new Network(world), Network::merge, Network::merge);
				mergedNetwork.addPipe(pos);
				getExternalConnectionsFor(world, pos).forEach(mergedNetwork::addExternalConnection);
				getNetworks().add(mergedNetwork);
				ItemPiping.LOGGER.info("Merged {}", mergedNetwork);
			}
			getNetworks().removeAll(aroundNetworks);
		}
	}
	
	@Nonnull
	private static Optional<Network> buildNetworkFrom(@Nonnull World world, @Nonnull BlockPos pos){
		if(world.getBlockState(pos).getBlock() instanceof PipeBlock){
			Network network = buildNetworkRecursive(world, pos, null, new Network(world));
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
			if(Objects.nonNull(te) && te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent()){
				return new PipeDestination(aroundPos, side);
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Nonnull
	private static Collection<Network> getNetworksAround(@Nonnull World world, @Nonnull BlockPos pos){
		return Arrays.stream(Direction.values()).map(pos::offset).map(aroundPos -> getNetworkFor(world, aroundPos)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
	}
	
	@Nonnull
	private static Collection<Network> getNetworks(){
		return Router.networks;
	}
}
