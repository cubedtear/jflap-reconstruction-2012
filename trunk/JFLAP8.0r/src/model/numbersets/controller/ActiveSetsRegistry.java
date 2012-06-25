package model.numbersets.controller;

import java.util.ArrayList;

import model.numbersets.AbstractNumberSet;

/**
 * Maintain a collection of the active sets
 * 
 * @author peggyli
 * 
 */

public class ActiveSetsRegistry extends SetRegistry {

	private ArrayList<AbstractNumberSet> myActiveSets;

	public ActiveSetsRegistry() {
		myActiveSets = new ArrayList<AbstractNumberSet>();
	}

	@Override
	public void remove(AbstractNumberSet s) {
		myActiveSets.remove(s);
	}

	@Override
	public void add(AbstractNumberSet s) {
		myActiveSets.add(s);
	}


	@Override
	public String[] getArray(ArrayList<AbstractNumberSet> sets) {
		return super.getArray(sets);
	}
}
