package model.automata.turing.buildingblock.library;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import debug.JFLAPDebug;

import model.automata.turing.BlankSymbol;
import model.automata.turing.TapeAlphabet;
import model.automata.turing.TuringMachine;
import model.automata.turing.TuringMachineMove;
import model.automata.turing.buildingblock.Block;
import model.automata.turing.buildingblock.BlockSet;
import model.automata.turing.buildingblock.BlockTransition;
import model.automata.turing.buildingblock.BlockTuringMachine;
import model.formaldef.components.alphabets.Alphabet;
import model.symbols.Symbol;
import model.symbols.SymbolString;

public class ShiftBlock extends BlockTMUpdatingBlock {

	private StartBlock myStart;
	private Map<Symbol, Block> mySubBlocks;
	private TuringMachineMove myShift;

	public ShiftBlock(TuringMachineMove shift, 
						TapeAlphabet tape, 
						int id) {
		super(tape, BlockLibrary.SHIFT + BlockLibrary.UNDSCR+shift.char_abbr, id, shift);
	}
	

	@Override
	public void updateTuringMachine(TapeAlphabet tape) {
		BlockTuringMachine tm = (BlockTuringMachine) getTuringMachine();
		BlockSet blocks = tm.getStates();
		Set<Symbol> symbols = new TreeSet<Symbol>(tape);
		for (Symbol sym: mySubBlocks.keySet().toArray(new Symbol[0])){
			if (symbols.contains(sym))
				symbols.remove(sym);
			else{
				blocks.remove(mySubBlocks.get(sym));
				mySubBlocks.remove(sym);
			}
		}
		for (Symbol s: symbols){
			Block block = new SingleShiftBlock(s, myShift, tape, blocks.getNextUnusedID());
			blocks.add(block);
			mySubBlocks.put(s, block);
			BlockTransition trans = new BlockTransition(myStart, block, 
											new SymbolString(new Symbol(TILDE)));
			tm.getTransitions().add(trans);
			tm.getFinalStateSet().add(block);
		}
	}


	@Override
	public void constructFromBase(TapeAlphabet parentAlph,
			TuringMachine localTM, Object... args) {
		mySubBlocks = new TreeMap<Symbol, Block>();
		myShift = (TuringMachineMove) args[0];
		myStart = new StartBlock(0);
		this.getTuringMachine().setStartState(myStart);		
	}


}
