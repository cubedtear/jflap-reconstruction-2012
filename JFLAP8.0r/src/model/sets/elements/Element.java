package model.sets.elements;

public class Element implements Comparable {

	private String myValue;
	
	public Element (String value) {
		myValue = value;
	}
	
	public Element (int value) {
		myValue = Integer.toString(value);
	}
	
	public String getValue () {
		return myValue;	
	}
	
	
	
	@Override
	public int hashCode () {
		return myValue.hashCode();
	}
	
	@Override
	public boolean equals (Object obj) {
		if (!(obj instanceof Element))
			return false;
		return myValue.equals(((Element) obj).myValue);
	}
	
	
	@Override
	public String toString() {
		return myValue;
	}
	
	
	@Override
	public int compareTo(Object arg0) {
		Element other = (Element) arg0;
		if (myValue.length() != other.myValue.length())
			return myValue.length() - other.myValue.length();
		return myValue.compareTo(((Element) arg0).myValue);
	}
}
