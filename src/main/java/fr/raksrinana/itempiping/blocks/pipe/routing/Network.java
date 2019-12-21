package fr.raksrinana.itempiping.blocks.pipe.routing;

import com.mojang.datafixers.util.Pair;
import fr.raksrinana.itempiping.blocks.pipe.pipe.PipeBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Network{
	private final Set<BlockPos> pipes;
	private final Set<PipeDestination> externalConnections;
	private final Map<BlockPos, RoutingResult> routes;
	private final DimensionType dimensionType;
	
	public void addExternalConnection(@Nonnull PipeDestination destination){
		this.externalConnections.add(destination);
		this.invalidate();
	}
	
	public void removeExternalConnection(@Nonnull PipeDestination destination){
		this.externalConnections.remove(destination);
		this.invalidate();
	}
	
	@Nonnull
	public RoutingResult getDestinationsFor(@Nonnull World world, @Nonnull BlockPos startPos, @Nonnull ItemStack itemStack, @Nonnull Direction sideIn){
		return this.routes.computeIfAbsent(startPos, pos -> exploreDestinations(world, pos, itemStack, sideIn));
	}
	
	private RoutingResult exploreDestinations(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack itemStack, @Nonnull Direction sideIn){
		RoutingResult result = new RoutingResult();
		Set<BlockPos> explored = new HashSet<>();
		Queue<Pair<BlockPos, Integer>> queue = new PriorityQueue<>(Comparator.comparingInt(Pair::getSecond));
		queue.add(new Pair<>(pos, 0));
		Pair<BlockPos, Integer> toExplore;
		while(Objects.nonNull(toExplore = queue.poll())){
			final BlockPos explorePos = toExplore.getFirst();
			final int distance = toExplore.getSecond();
			if(!explored.contains(explorePos)){
				explored.add(explorePos);
				if(pipes.contains(explorePos)){
					PipeBlock.getPipeTileEntity(world, explorePos).ifPresent(explorePipeTileEntity -> {
						for(Direction direction : Direction.values()){
							if(Objects.equals(explorePos, pos) && Objects.equals(direction, sideIn)){
								continue;
							}
							if(explorePipeTileEntity.canExtract(itemStack, world.getBlockState(explorePos), direction)){
								final BlockPos nextPos = explorePos.offset(direction);
								final Direction face = direction.getOpposite();
								final PipeDestination destination = new PipeDestination(nextPos, face);
								if(this.externalConnections.contains(destination)){
									result.addRoute(new Route(destination, distance + 1, 1));
								}
								else if(pipes.contains(nextPos)){
									PipeBlock.getPipeTileEntity(world, nextPos).ifPresent(nextPipeTileEntity -> {
										if(nextPipeTileEntity.canReceive(itemStack, world.getBlockState(nextPos), face)){
											queue.add(new Pair<>(nextPos, distance + 1));
										}
									});
								}
							}
						}
					});
				}
			}
		}
		return result;
	}
	
	@Nonnull
	public Set<BlockPos> getPipes(){
		return pipes;
	}
	
	public Network(@Nonnull DimensionType dimensionType){
		this.dimensionType = dimensionType;
		this.pipes = new LinkedHashSet<>();
		this.externalConnections = new HashSet<>();
		this.routes = new HashMap<>();
	}
	
	public boolean contains(@Nonnull BlockPos pos, @Nullable Direction side){
		return containsPipe(pos) || externalConnections.stream().anyMatch(destination -> destination.is(pos, side));
	}
	
	public boolean containsPipe(@Nonnull BlockPos pos){
		return pipes.contains(pos);
	}
	
	public boolean isSameWorld(@Nonnull DimensionType world){
		return Objects.equals(this.dimensionType, world);
	}
	
	public void addPipe(@Nonnull BlockPos pos){
		this.pipes.add(pos);
		this.invalidate();
	}
	
	public void merge(@Nonnull Network other){
		this.pipes.addAll(other.getPipes());
		this.externalConnections.addAll(other.externalConnections);
		this.invalidate();
	}
	
	@Override
	public String toString(){
		return new StringJoiner(", ", Network.class.getSimpleName() + "[", "]").add("pipes(" + pipes.size() + ")=" + pipes).add("externalConnections(" + externalConnections.size() + ")=" + externalConnections).toString();
	}
	
	public void invalidate(){
		this.routes.clear();
	}
}
