package model.automata.transducers;

import java.lang.reflect.InvocationTargetException;

import model.automata.Automaton;
import model.automata.InputAlphabet;
import model.automata.StartState;
import model.automata.StateSet;
import model.automata.TransitionSet;
import model.automata.acceptors.fsa.FSATransition;
import model.formaldef.FormalDefinition;
import model.formaldef.components.ComponentChangeEvent;
import model.formaldef.components.FormalDefinitionComponent;
import model.formaldef.components.symbols.Symbol;

public abstract class Transducer<T extends OutputFunction> extends Automaton<FSATransition> {


	public Transducer(StateSet states, 
					InputAlphabet langAlph,
					OutputAlphabet outputAlph,
					TransitionSet<FSATransition> functions, 
					StartState start,
					OutputFunctionSet outputFunctions) {
		super(states, langAlph, outputAlph, functions, start, outputFunctions);
	}

	@Override
	public Transducer alphabetAloneCopy() {
		Class<Transducer> clz = (Class<Transducer>) this.getClass();
		try {
					return clz.cast(clz.getConstructors()[0].newInstance(new StateSet()	,																		this.getInputAlphabet(),
																			this.getOutputAlphabet(),
																			new TransitionSet<FSATransition>(),
																			new StartState(),
																			new OutputFunctionSet()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public OutputAlphabet getOutputAlphabet() {
		return this.getComponentOfClass(OutputAlphabet.class);
	}
	
	public OutputFunctionSet getOutputFunctionSet(){
		return this.getComponentOfClass(OutputFunctionSet.class);
	}
	
	@Override
	public FormalDefinitionComponent[] getComponents() {
		return new FormalDefinitionComponent[]{this.getStates(),
											this.getInputAlphabet(),
											this.getOutputAlphabet(),
											this.getTransitions(),
											new StartState(this.getStartState()),
											this.getOutputFunctionSet()};
	}

	@Override
	public void componentChanged(ComponentChangeEvent event) {
		OutputAlphabet output = this.getOutputAlphabet();
		if (event.comesFrom(output) && event.getType() == ITEM_REMOVED){
			this.getOutputFunctionSet().purgeOfSymbol(getOutputAlphabet(), 
												(Symbol) event.getArg(0));
		}
		else 
			super.componentChanged(event);
	}
	
	@Override
	public Transducer copy() {
		Class<Transducer> clz = (Class<Transducer>) this.getClass();
		try {
					return clz.cast(clz.getConstructors()[0].newInstance(this.getStates().copy(),
																			this.getInputAlphabet().copy(),
																			this.getOutputAlphabet().copy(),
																			new TransitionSet<FSATransition>().copy(),
																			new StartState(this.getStartState().copy()),
																			this.getOutputFunctionSet().copy()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
