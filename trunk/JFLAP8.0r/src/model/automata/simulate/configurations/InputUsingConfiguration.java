package model.automata.simulate.configurations;

import model.automata.Automaton;
import model.automata.State;
import model.automata.Transition;
import model.automata.simulate.Configuration;
import model.formaldef.components.symbols.SymbolString;


public abstract class InputUsingConfiguration<S extends Automaton<T>, 
										T extends Transition> 
																	extends Configuration<S,T> {

	public InputUsingConfiguration(S a, State s, int pos, SymbolString input, SymbolString ... strings) {
		super(a, s, pos, input, null, strings);
	}

	
	@Override
	public int getPositionForIndex(int i) {
			return this.getStringForIndex(i).size();
	}

	@Override
	protected Configuration<S, T> createConfig(S a, State s, int ppos,
			SymbolString primary, int[] positions, SymbolString[] updatedClones)
			throws Exception {
		return createInputConfig(a, s, ppos, primary, updatedClones);
	}

	protected abstract Configuration<S, T> createInputConfig(S a, State s, int ppos,
			SymbolString input, SymbolString[] updatedClones) throws Exception;

	
	
	

	@Override
	protected boolean canMoveAlongTransition(T trans) {
		SymbolString remaining = getInput().subList(getPrimaryPosition());
		return remaining.startsWith(trans.getInput());
	}

	@Override
	protected int getNextPrimaryPosition(T trans) {
		return this.getPrimaryPosition() + trans.getInput().size();
	}
	
	@Override
	protected boolean isDone(){
		return getInput().size() == getPrimaryPosition();
	}
	
	@Override
	protected String getPrimaryPresentationName() {
		return "Input";
	}

	private SymbolString getInput(){
		return this.getPrimaryString();
	}
	
}