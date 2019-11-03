package fr.raksrinana.itempiping.pipe.routing;

import fr.raksrinana.itempiping.utils.PrioritySet;
import java.util.*;

public class RoutingResult{
	private final Collection<Route> routes;
	
	public RoutingResult(){
		routes = new PrioritySet<>((r1, r2) -> {
			if(r1.getPriority() == r2.getPriority()){
				return Integer.compare(r1.getDistance(), r2.getDistance());
			}
			return Integer.compare(r1.getPriority(), r2.getPriority());
		});
	}
	
	public void addRoute(Route route){
		this.routes.add(route);
	}
	
	public Collection<Route> getRoutes(){
		return routes;
	}
}
