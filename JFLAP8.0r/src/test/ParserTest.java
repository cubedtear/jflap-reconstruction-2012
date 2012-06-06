package test;

import model.automata.InputAlphabet;
import model.formaldef.components.symbols.SymbolString;
import model.formaldef.components.symbols.Terminal;
import model.formaldef.components.symbols.Variable;
import model.grammar.Grammar;
import model.grammar.Production;
import model.grammar.ProductionSet;
import model.grammar.StartVariable;
import model.grammar.TerminalAlphabet;
import model.grammar.VariableAlphabet;
import model.grammar.parsing.Derivation;
import model.grammar.parsing.FirstFollowTable;
import model.grammar.parsing.ll.LL1Parser;
import model.regex.OperatorAlphabet;
import model.regex.RegularExpressionGrammar;
import model.util.UtilFunctions;

public class ParserTest extends GrammarTest {

	@Override
	public void runTest() {
		
		TerminalAlphabet terms = new TerminalAlphabet();
		VariableAlphabet vars = new VariableAlphabet();
		ProductionSet prod = new ProductionSet();
		StartVariable var = new StartVariable();
		Grammar g  = new Grammar(vars,
									terms,
									prod, 
									var);
		
		Variable S = new Variable("S");
		Variable A = new Variable("A");
		Variable B = new Variable("B");
		Terminal a = new Terminal("a");
		Terminal b = new Terminal("b");
		Terminal c = new Terminal("c");
		Terminal d = new Terminal("d");
		
		//ex7.6cnf-a.jff
		addSymbols(g.getVariables(), S,A,B );
		addSymbols(g.getTerminals(),a,b,c,d);
		prod.add(new Production(S, a,A,d,B));
		prod.add(new Production(A, a,A));
		prod.add(new Production(A, c));
		prod.add(new Production(B, b, B));
		prod.add(new Production(B));
		g.setStartVariable(S);
		
		outPrintln(g.toString());
		
		//construct First/Follow table
		FirstFollowTable table = new FirstFollowTable(g);
		outPrintln(table.toString());
		
		//construct First/Follow table
		table = new FirstFollowTable(new RegularExpressionGrammar(new InputAlphabet(), new OperatorAlphabet()));
		outPrintln(table.toString());
		
		//try LL1 parser
		String in = "aaaaacd";
		LL1Parser ll1parse = new LL1Parser(g);
		boolean accepts = ll1parse.quickParse(SymbolString.createFromString(in, g));
		outPrintln("Accept? " + accepts + "\n" + createPrintout(ll1parse.getDerivation()));
		
	}

	private String createPrintout(Derivation derivation) {
		SymbolString[] s = derivation.getResultArray();
		String str = "";
		for (int i = 0; i< s.length; i++){
			str += derivation.getProduction(i) + "\t" + s[i] + "\n";
		}

		return str;
	}

	@Override
	public String getTestName() {
		return null;
	}

}