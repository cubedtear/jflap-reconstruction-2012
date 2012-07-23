package view.action.grammar;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import universe.JFLAPUniverse;
import view.environment.JFLAPEnvironment;
import view.grammar.LanguageGeneratorView;

import model.grammar.Grammar;

public class LanguageGeneratorAction extends AbstractAction {

	private Grammar myGrammar;

	public LanguageGeneratorAction(Grammar g){
		super("Generate Language");
		myGrammar = g;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (myGrammar == null) return;
		LanguageGeneratorView view = new LanguageGeneratorView(myGrammar);
		
		JFLAPEnvironment environ = JFLAPUniverse.getActiveEnvironment();
		environ.addSelectedComponent(view);
	}

}
