package model.automata.turing.buildingblock;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import oldnewstuff.view.Updateable;

import model.automata.InputAlphabet;
import model.automata.StartState;
import model.automata.TransitionSet;
import model.automata.acceptors.FinalStateSet;
import model.automata.turing.BlankSymbol;
import model.automata.turing.TapeAlphabet;
import model.automata.turing.TuringMachine;
import model.change.events.AdvancedChangeEvent;

public abstract class UpdatingBlock extends Block implements ChangeListener {
	
	public UpdatingBlock(TapeAlphabet parentAlph, String name, int id, Object ... args) {
		super(createTuringMachine(parentAlph), name, id);
		parentAlph.addListener(this);
		constructFromBase(parentAlph, this.getTuringMachine(), args);
		this.updateTuringMachine(parentAlph);
	}

	public abstract void constructFromBase(TapeAlphabet parentAlph, TuringMachine localTM, Object ... args);

	private static BlockTuringMachine createTuringMachine(TapeAlphabet alph) {

		BlockTuringMachine tm = new BlockTuringMachine(new BlockSet(), 
				alph.copy(),
				new BlankSymbol(), 
				new InputAlphabet(), 
				new TransitionSet<BlockTransition>(),
				new StartState(), 
				new FinalStateSet());
		return tm;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if(!(e instanceof AdvancedChangeEvent))
		return;
		AdvancedChangeEvent event = (AdvancedChangeEvent) e;
		if(event.comesFrom(TapeAlphabet.class)){
			updateTuringMachine((TapeAlphabet) event.getSource());
		}
	}
	
	public abstract void updateTuringMachine(TapeAlphabet parentAlph);

}
