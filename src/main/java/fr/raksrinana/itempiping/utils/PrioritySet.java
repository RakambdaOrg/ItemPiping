package fr.raksrinana.itempiping.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.SortedSet;

public class PrioritySet<E> extends PriorityQueue<E>{
	public PrioritySet(){
	}
	
	public PrioritySet(int initialCapacity){
		super(initialCapacity);
	}
	
	public PrioritySet(Comparator<? super E> comparator){
		super(comparator);
	}
	
	public PrioritySet(int initialCapacity, Comparator<? super E> comparator){
		super(initialCapacity, comparator);
	}
	
	public PrioritySet(Collection<? extends E> c){
		super(c);
	}
	
	public PrioritySet(PriorityQueue<? extends E> c){
		super(c);
	}
	
	public PrioritySet(SortedSet<? extends E> c){
		super(c);
	}
	
	@Override
	public boolean offer(E e){
		if(contains(e))
			return false;
		return super.offer(e);
	}
}
