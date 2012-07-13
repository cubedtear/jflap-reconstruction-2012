package view.action.grammar.parse;

import model.algorithms.testinput.parse.brute.UnrestrictedBruteParser;
import model.grammar.Grammar;

import view.grammar.GrammarView;
import view.grammar.parsing.ParserView;
import view.grammar.parsing.brute.BruteParserView;

public class BruteParseAction extends ParseAction<UnrestrictedBruteParser> {
	
	public BruteParseAction(GrammarView view){
		super("Brute Force Parse", view);
	}

	@Override
	public ParserView<UnrestrictedBruteParser> createParseView(Grammar g) {
		UnrestrictedBruteParser parser = UnrestrictedBruteParser.createNewBruteParser(g);
		return new BruteParserView(parser);
	}

}
