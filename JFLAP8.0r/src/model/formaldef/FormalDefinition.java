package model.formaldef;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import preferences.JFLAPPreferences;



import debug.JFLAPDebug;

import util.Copyable;
import util.JFLAPConstants;

import model.change.ChangingObject;
import model.change.events.AdvancedChangeEvent;
import model.formaldef.components.ChangeTypes;
import model.formaldef.components.ComponentChangeListener;
import model.formaldef.components.FormalDefinitionComponent;
import model.formaldef.components.alphabets.Alphabet;
import model.formaldef.components.functionset.FunctionSet;
import model.formaldef.rules.applied.DisallowedCharacterRule;
import model.symbols.Symbol;
import model.symbols.SymbolString;
import errors.BooleanWrapper;


public abstract class FormalDefinition extends ChangingObject implements Describable, 
																			UsesSymbols, 
																			ChangeListener, 
																			ChangeTypes,
																			JFLAPConstants,
																			Copyable{

	private LinkedList<FormalDefinitionComponent> myComponents;


	public FormalDefinition(FormalDefinitionComponent ... comps) {
		myComponents = new LinkedList<FormalDefinitionComponent>();
		for (FormalDefinitionComponent comp : comps){
			myComponents.add(comp);
			comp.addListener(this);
		}
		for (Alphabet a: this.getAlphabets())
			a.addRules(new DisallowedCharacterRule(this));
		updateAlphabets(ITEM_MODIFIED);
	}

	public String toNtupleString(){
		String out = this.getDescriptionName() + " = (";

		for (FormalDefinitionComponent comp : this.getComponents()){
			out += comp.getCharacterAbbr() + ", ";
		}

		out = out.substring(0,out.length()-2)+")";

		return out;
	}

	@Override
	public String toString() {
		String out = this.toNtupleString() + "\n";

		for (FormalDefinitionComponent comp : this.getComponents()){
			out += "\t" + comp.toString() + "\n";
		}

		return out;
	}

	public <T extends FormalDefinitionComponent> T getComponentOfClass(Class<T> clz) {
		for (FormalDefinitionComponent comp: this.getComponents()){
			if (clz.isAssignableFrom(comp.getClass()))
				return clz.cast(comp);
		}
		return null;
	}

	public void trimAlphabets(){
		for (Alphabet a: this.getAlphabets()){
			Set<Symbol> used = this.getSymbolsUsedForAlphabet(a);
			a.retainAll(used);
		}
	}

//	@Override
//	public FormalDefinition copy() {
//		ArrayList<FormalDefinitionComponent> cloned = new ArrayList<FormalDefinitionComponent>();
//		for (FormalDefinitionComponent comp : this.getComponents())
//			cloned.add(comp.copy());
//		cloned.trimToSize();
//		try {
//			return (FormalDefinition) this.getClass().getConstructors()[0].newInstance(cloned.toArray());
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		} 
//	}

	public BooleanWrapper[] isComplete() {
		ArrayList<BooleanWrapper> incomplete = new ArrayList<BooleanWrapper>();
		for (FormalDefinitionComponent comp: this.getComponents()){
			BooleanWrapper amComplete = comp.isComplete();
			if (amComplete.isError())
				incomplete.add(amComplete);
		}
		return incomplete.toArray(new BooleanWrapper[0]);
	}


	/**
	 * Retrieves all of the characters disallowed in this formal definition.
	 * Those are characters which are used in other parts of the definition
	 * which cannot be confused for other symbols.
	 * 
	 * @return
	 */
	public ArrayList<Character> getDisallowedCharacters() {
		return new ArrayList<Character>(Arrays.asList(new Character[]{' ', JFLAPPreferences.getEmptyStringSymbol().charAt(0)}));
	}

	public AbstractCollection<Alphabet> getAlphabets() {
		AbstractCollection<Alphabet> alphs = new ArrayList<Alphabet>();

		for (FormalDefinitionComponent comp : this.getComponents()){
			if (comp instanceof Alphabet){
				alphs.add((Alphabet) comp);
			}
		}


		return alphs;
	}

	/**
	 * Retrieves all of the {@link FormalDefinitionComponent}s in order
	 * as they should be in the n-tuple of this {@link FormalDefinition}.
	 * 
	 * @return all of the {@link FormalDefinitionComponent} in this 
	 * 											{@link FormalDefinition}.
	 */
	public FormalDefinitionComponent[] getComponents(){
		return myComponents.toArray(new FormalDefinitionComponent[0]);
	}

	public Set<Symbol> getUnusedSymbols() {
		Set<Symbol> symbols = this.getAllSymbolsInAlphabets();
		for (Alphabet a: this.getAlphabets())
			symbols.removeAll(this.getSymbolsUsedForAlphabet(a));
		return symbols;
	}

	@Override
	public Set<Symbol> getSymbolsUsedForAlphabet(Alphabet a) {
		TreeSet<Symbol> used = new TreeSet<Symbol>();

		for (FormalDefinitionComponent f: this.getComponents()){
			if (f instanceof UsesSymbols)
				used.addAll(((UsesSymbols) f).getSymbolsUsedForAlphabet(a));
		}

		return used;
	}

	public Set<Symbol> getAllSymbolsInAlphabets() {
		Set<Symbol> symbols = new HashSet<Symbol>();
		for (Alphabet alph: getAlphabets()){
			symbols.addAll(alph);
		}
		return symbols;
	}

	@Override
	public boolean purgeOfSymbols(Alphabet a, Collection<Symbol> s){
		boolean result = false;
		for (FormalDefinitionComponent f: this.getComponents()){
			if (f instanceof UsesSymbols)
				result = ((UsesSymbols) f).purgeOfSymbols(a, s) || result;
		}
		this.distributeChanged();
		return result;
	}

	public abstract FormalDefinition alphabetAloneCopy();

	
	
	////////////////////////////////////////////////////	
	////////////////// INTERACTIONS ////////////////////
	////////////////////////////////////////////////////
	
	@Override
	public void stateChanged(ChangeEvent event) {
		if (event instanceof AdvancedChangeEvent)
			this.componentChanged((AdvancedChangeEvent) event);
	}

	public void componentChanged(AdvancedChangeEvent event){
		Collection<Alphabet> alphabets = this.getAlphabets();
		for (Alphabet a: alphabets){
			if (event.comesFrom(a.getClass())){
				switch (event.getType()){
				case ITEM_REMOVED: 
					this.purgeOfSymbols(a, (Collection<Symbol>) event.getArg(0));
					return;
				case ITEM_MODIFIED:
					Symbol from = (Symbol) event.getArg(0);
					Symbol to = (Symbol) event.getArg(1);
					applySymbolMod(from.getString(), to.getString());
					return;
				}
			}
		}

		Collection<UsesSymbols> users = this.getUsesSymbols();
		for (UsesSymbols us: users){
			if (event.comesFrom(us.getClass())){
				updateAlphabets(event.getType());
				return;
			}
		}
		
	}

	private void updateAlphabets(int type) {
		for (Alphabet a: this.getAlphabets()){
			Set<Symbol> used = this.getSymbolsUsedForAlphabet(a);

			if (type == ITEM_ADDED || 
					type == ITEM_MODIFIED ||
						type == SPECIAL_CHANGED)
				a.addAll(used);
			if (type == ITEM_REMOVED || 
					type == ITEM_MODIFIED||
						type == SPECIAL_CHANGED)
				a.retainAll(used);
		}
	}

	private Collection<UsesSymbols> getUsesSymbols() {
		Collection<UsesSymbols> users = new ArrayList<UsesSymbols>();

		for (FormalDefinitionComponent comp : this.getComponents()){
			if (comp instanceof UsesSymbols){
				users.add((UsesSymbols) comp);
			}
		}


		return users;
	}
	
	@Override
	public boolean applySymbolMod(String from, String to) {
		boolean changed = false;
		for (UsesSymbols us: this.getUsesSymbols()){
			if (us.applySymbolMod(from, to))
				changed  = true;
		}
		return changed;
	}
	
	public SymbolString createFromString(String in, boolean custom){
		Collection<Object> extracted = extractSymbols(in);
		SymbolString result = new SymbolString();
		for (Object o : extracted){
			if (o instanceof Symbol)
				result.add((Symbol) o);
			else
				result.addAll(toSymbols(in, custom));
		}
		return result;
	}

	private SymbolString toSymbols(String in, boolean custom) {
		SymbolString ss = new SymbolString();
		if (!custom){
			for (char c: in.toCharArray()){
				ss.add(toSymbol(c+""));
			}
		}
		else
			ss.add(toSymbol(in));
		return ss;
	}

	public Symbol toSymbol(String string) {
		return new Symbol(string);
	}

	private Collection<Object> extractSymbols(String in) {
		
		if (in == null ||in.length() == 0) 
			return new ArrayList<Object>();

		AbstractCollection<Alphabet> alphs = this.getAlphabets();
		
		ArrayList<List<Object>> options = new ArrayList<List<Object>>();
		
		for (int i = in.length(); i > 0; i--){
			List<Object> temp = new ArrayList<Object>();
			Object sub = in.substring(0,i);
			loop: for (Alphabet alph: alphs){
				Symbol s = alph.getByString((String) sub);
				if (s != null){
					sub = s;
					break loop;
				}
			}
			temp.add(sub);
			
			String remaining = in.substring(i);
			temp.addAll(extractSymbols(remaining));
			options.add(temp);
		}
		
		List<Object> best = options.get(0);
		for (List<Object> option: options){
			if (stringCount(option) < stringCount(best))
				best = option;
		}
		
		
		return best;
	}

	private int stringCount(List<Object> option) {
		int count = 0;
		for (Object o: option){
			if (o instanceof String)
				count+= ((String) o).length();
		}
		return count;
	}

}