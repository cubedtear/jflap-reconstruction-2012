package model.graph;

import java.awt.Point;

/**
 * A vertex object to be used in graph object creation.
 * @author Julian
 *
 */
public class Vertex implements Comparable<Vertex>{

	private String myName;

	private Point myLocation;

	public Vertex(String name, Point loc) {
		setName(name);
		setLocation(loc);
	}

	public Vertex(String name) {
		this(name, new Point());
	}

	/**
	 * Returns the graphical location of this state
	 * in the automaton transition graph.
	 * 
	 * @return
	 */
	public Point getLocation(){
		return myLocation;
	}
	
	public double getX(){
		return myLocation.getX();
	}
	
	public double getY(){
		return myLocation.getY();
	}
	
	/**
	 * Sets the location of this state to the
	 * passed in point. Only used in graphical
	 * representation of the Automaton.
	 * 
	 * @param point
	 */
	public void setLocation(Point point) {
		myLocation = point;
	}
	
	/**
	 * Sets the name for this state. A parameter of <CODE>null</CODE> will
	 * reset this to the default.
	 * 
	 * @param name
	 *            the new name for the state
	 */
	public void setName(String name) {
		myName = name;
	}

	/**
	 * Returns the simple "name" for this state. By default this will simply be
	 * "qd", where d is the ID number.
	 * 
	 * @return the name for this state
	 */
	public String getName() {

		return myName;
	}

	@Override
	public int compareTo(Vertex o) {
		int compare = this.getName().compareTo(o.getName());
		if (compare != 0) return compare;
		
		compare = new Double(this.getX()).compareTo(o.getX());
		if (compare != 0) return compare;
		
		return new Double(this.getY()).compareTo(o.getY());
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.compareTo((Vertex) obj) == 0;
	}
	
	@Override
	public String toString() {
		return myName + "|" + myLocation;
	}
}