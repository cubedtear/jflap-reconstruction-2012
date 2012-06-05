package model.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import model.automata.State;
import model.formaldef.components.symbols.Symbol;
import model.formaldef.components.symbols.Terminal;

public class UtilFunctions {

	public static String createDelimitedString(Iterable iterable, String delimiter){
		Iterator iter = iterable.iterator();
		String string = "";
		while (iter.hasNext()){
			string += iter.next();
			if (iter.hasNext()) 
				string += delimiter;
		}
		return string;
		
	}
	
	
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		  int totalLength = first.length;
		  for (T[] array : rest) {
		    totalLength += array.length;
		  }
		  T[] result = Arrays.copyOf(first, totalLength);
		  int offset = first.length;
		  for (T[] array : rest) {
		    System.arraycopy(array, 0, result, offset, array.length);
		    offset += array.length;
		  }
		  return result;
		}


	public static String createDelimitedString(Object[] stateGroup,
			String delimiter) {
		return createDelimitedString(Arrays.asList(stateGroup), delimiter);
	}


	public  static <T> T[] combine(T[] myColumns,
									T ... toAdd) {
		return concatAll(myColumns, toAdd);
	}
	
	
}