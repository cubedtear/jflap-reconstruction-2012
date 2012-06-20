package model.automata.turing.buildingblock;

import java.util.Collection;
import java.util.Set;

import model.automata.State;
import model.automata.turing.MultiTapeTuringMachine;
import model.automata.turing.TuringMachine;
import model.formaldef.UsesSymbols;
import model.formaldef.components.alphabets.Alphabet;
import model.formaldef.components.symbols.Symbol;

public class Block extends State implements UsesSymbols{
	private TuringMachine myMachine;

	public Block(TuringMachine machine, String name, int id){
		super(name, id);
		myMachine = machine;
	}
	
	public void setTuringMachine(TuringMachine tm){
		myMachine = tm;
	}
	
	public TuringMachine getTuringMachine(){
		return myMachine;
	}

	@Override
	public Set<Symbol> getSymbolsUsedForAlphabet(Alphabet a) {
		return myMachine.getSymbolsUsedForAlphabet(a);
	}

	@Override
	public boolean applySymbolMod(String from, String to) {
		return myMachine.applySymbolMod(from, to);
	}

	@Override
	public boolean purgeOfSymbols(Alphabet a, Collection<Symbol> s) {
		return myMachine.purgeOfSymbols(a, s);
	}
	
	@Override
	public String toDetailedString(){
		return super.toDetailedString()+ "\n" + myMachine.toString();
	}
	
}
