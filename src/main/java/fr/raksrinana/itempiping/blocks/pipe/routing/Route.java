package fr.raksrinana.itempiping.blocks.pipe.routing;

public class Route{
	private final PipeDestination destination;
	private final int distance;
	private final int priority;
	
	public Route(PipeDestination destination, int distance, int priority){
		this.destination = destination;
		this.distance = distance;
		this.priority = priority;
	}
	
	public PipeDestination getDestination(){
		return destination;
	}
	
	public int getDistance(){
		return distance;
	}
	
	public int getPriority(){
		return priority;
	}
}
