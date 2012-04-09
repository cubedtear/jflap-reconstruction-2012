package model.automata;

import java.util.Iterator;
import java.util.TreeSet;

import errors.BooleanWrapper;

import model.formaldef.components.FormalDefinitionComponent;

public class StateSet extends TreeSet<State> implements
		FormalDefinitionComponent {

	
	@Override
	public String getDescriptionName() {
		return "Internal States";
	}

	@Override
	public String getDescription() {
		return "The set of internal states.";
	}

	@Override
	public Character getCharacterAbbr() {
		return 'Q';
	}

	@Override
	public BooleanWrapper isComplete() {
		return new BooleanWrapper(true);
	}

	@Override
	public StateSet clone() {
		return (StateSet) super.clone();
	}

	public int getNextID() {
		
		int i = 0;
		
		while (this.getStateWithID(i) != null){
			i++;
		}
		return i;
	}

	public State getStateWithID(int id) {
		for (State s: this){
			if (s.getID() == id)
				return s;
		}
		
		return null;
	}
}
