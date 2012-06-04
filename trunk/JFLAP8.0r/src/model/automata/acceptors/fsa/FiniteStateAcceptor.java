package model.automata.acceptors.fsa;

import model.automata.InputAlphabet;
import model.automata.StartState;
import model.automata.StateSet;
import model.automata.TransitionSet;
import model.automata.acceptors.Acceptor;
import model.automata.acceptors.FinalStateSet;
import model.formaldef.FormalDefinition;
import model.formaldef.components.FormalDefinitionComponent;

public class FiniteStateAcceptor extends Acceptor<FSTransition> {

	public FiniteStateAcceptor(StateSet states, 
									InputAlphabet langAlph,
									TransitionSet<FSTransition> functions, 
									StartState start,
									FinalStateSet finalStates) {
		super(states, langAlph, functions, start, finalStates);
	}

	public FiniteStateAcceptor() {
		this(new StateSet(),
				new InputAlphabet(),
				new TransitionSet<FSTransition>(),
				new StartState(),
				new FinalStateSet());
	}

	@Override
	public String getDescriptionName() {
		return "Finite State Automaton (FSA)";
	}

	@Override
	public String getDescription() {
		return "A finite state automaton, basic accepter with no memory.";
	}

	@Override
	public FiniteStateAcceptor alphabetAloneCopy() {
		return new FiniteStateAcceptor(new StateSet(), 
						this.getInputAlphabet(), 
						new TransitionSet<FSTransition>(), 
						new StartState(), 
						new FinalStateSet());
	}

	public static boolean hasAllSingleSymbolInput(FiniteStateAcceptor dfa) {
		for (FSTransition trans : dfa.getTransitions()){
			if (trans.getInput().size() > 1){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public FiniteStateAcceptor copy() {
		return new FiniteStateAcceptor(this.getStates().copy(),
				this.getInputAlphabet().copy(),
				this.getTransitions().copy(),
				new StartState(this.getStartState().copy()),
				this.getFinalStateSet().copy());
	}
	

}