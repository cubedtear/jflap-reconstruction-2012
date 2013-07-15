package view.action.automata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JLabel;

import model.automata.Automaton;
import model.automata.State;
import model.automata.Transition;
import model.automata.TransitionSet;
import model.automata.determinism.DeterminismChecker;
import model.automata.determinism.DeterminismCheckerFactory;
import model.undo.UndoKeeper;
import universe.JFLAPUniverse;
import util.JFLAPConstants;
import util.view.magnify.MagnifiablePanel;
import view.automata.AutomatonDisplayPanel;
import view.automata.AutomatonEditorPanel;
import view.automata.views.AutomataView;

public class HighlightNondeterminismAction extends AutomatonAction {

	private DeterminismChecker myChecker;

	public HighlightNondeterminismAction(AutomataView view) {
		super("Highlight Nondeterminism", view);
		Automaton auto = (Automaton) view.getDefinition();
		myChecker = DeterminismCheckerFactory.getChecker(auto);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		AutomatonEditorPanel panel = getEditorPanel();		
		NondeterminismPanel np = new NondeterminismPanel(panel);
		JFLAPUniverse.getActiveEnvironment().addSelectedComponent(np);
		
	}
	
	private class NondeterminismPanel extends AutomatonDisplayPanel{

		public NondeterminismPanel(AutomatonEditorPanel editor) {
			super(editor, "Nondeterminism");
			add(new JLabel("Nondeterministic states and transitions are highlighted."),
					BorderLayout.NORTH);
			
			Automaton auto = editor.getAutomaton();
			State[] states = myChecker.getNondeterministicStates(auto);
			Collection<Transition> trans = myChecker.getAllNondeterministicTransitions(auto);
			
			AutomatonEditorPanel panel = getPanel();
			panel.selectAll(trans);
			panel.selectAll(Arrays.asList(states));
			update();
		}

		
	}

}