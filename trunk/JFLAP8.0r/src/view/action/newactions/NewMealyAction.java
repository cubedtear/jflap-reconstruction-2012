package view.action.newactions;

import debug.JFLAPDebug;
import model.automata.transducers.mealy.MealyMachine;

public class NewMealyAction extends NewFormalDefinitionAction<MealyMachine> {

	public NewMealyAction() {
		super("Mealy Machine");
	}

	@Override
	public MealyMachine createDefinition() {
		
		JFLAPDebug.print("need to implement transition editor first");
		return new MealyMachine();
	}

}
