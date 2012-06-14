package oldnewstuff.model.change.rules;

import oldnewstuff.model.change.ChangeEvent;
import oldnewstuff.model.change.events.SetComponentEvent;
import errors.BooleanWrapper;
import model.formaldef.components.alphabets.Alphabet;
import model.formaldef.components.alphabets.grouping.GroupingPair;
import model.formaldef.components.symbols.Symbol;




public abstract class GroupingRule extends SetComponentRule<Symbol>{

	private GroupingPair myGrouping;
	
	public GroupingRule(int type, GroupingPair gp){
		super(type);
		myGrouping = gp;
	}

	public Character getOpenGroup() {
		return myGrouping.getOpenGroup();
	}
	
	public Character getCloseGroup() {
		return myGrouping.getCloseGroup();
	}

	protected GroupingPair getGroupingPair() {
		return myGrouping;
	}
	
	public boolean containsGrouping(Symbol newSymbol) {
		return newSymbol.containsCharacters(this.getOpenGroup(), this.getCloseGroup());
	}


}
