package fr.raksrinana.itempiping.pipe.routing;

import com.mojang.datafixers.util.Pair;
import fr.raksrinana.itempiping.pipe.PipeTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class Network{
	private final Set<BlockPos> pipes;
	private final Set<PipeDestination> externalConnections;
	private final Map<BlockPos, RoutingResult> routes;
	private final World world;
	
	public void addExternalConnection(@Nonnull PipeDestination destination){
		this.externalConnections.add(destination);
		this.invalidate();
	}
	
	public void removeExternalConnection(@Nonnull PipeDestination destination){
		this.externalConnections.remove(destination);
		this.invalidate();
	}
	
	@Nonnull
	public RoutingResult getDestinationsFor(@Nonnull BlockPos startPos, @Nonnull ItemStack itemStack){
		return this.routes.computeIfAbsent(startPos, pos -> exploreDestinations(pos, itemStack));
	}
	
	private RoutingResult exploreDestinations(@Nonnull BlockPos pos, @Nonnull ItemStack itemStack){
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
					for(Direction direction : Direction.values()){
						final BlockPos nextPos = explorePos.offset(direction);
						final Direction side = direction.getOpposite();
						final PipeDestination destination = new PipeDestination(nextPos, side);
						if(!Objects.equals(explorePos, pos) && this.externalConnections.contains(destination)){
							result.addRoute(new Route(destination, distance + 1, 1));
						}
						else if(pipes.contains(nextPos)){
							Optional.ofNullable(world.getTileEntity(explorePos)).filter(te -> te instanceof PipeTileEntity).map(PipeTileEntity.class::cast).ifPresent(te -> {
								if(te.canStackPassThrough(itemStack, side)){
									queue.add(new Pair<>(nextPos, distance + 1));
								}
							});
						}
					}
				}
			}
		}
		return result;
	}
	
	public World getWorld(){
		return world;
	}
	
	@Nonnull
	public Set<BlockPos> getPipes(){
		return pipes;
	}
	
	public Network(@Nonnull World world){
		this.world = world;
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
	
	public boolean isSameWorld(@Nonnull World world){
		return Objects.equals(this.world, world);
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
