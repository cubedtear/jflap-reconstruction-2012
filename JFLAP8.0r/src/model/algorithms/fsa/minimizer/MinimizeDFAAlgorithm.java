package model.algorithms.fsa.minimizer;

import java.util.ArrayList;
import java.util.List;

import debug.JFLAPDebug;
import errors.BooleanWrapper;
import model.algorithms.AlgorithmException;
import model.algorithms.AlgorithmExecutingStep;
import model.algorithms.AlgorithmStep;
import model.algorithms.FormalDefinitionAlgorithm;
import model.algorithms.SteppableAlgorithm;
import model.algorithms.fsa.AddTrapStateAlgorithm;
import model.algorithms.fsa.InacessibleStateRemover;
import model.automata.acceptors.fsa.FiniteStateAcceptor;
import model.automata.derterminism.FSADeterminismChecker;
import model.formaldef.FormalDefinition;

public class MinimizeDFAAlgorithm extends FormalDefinitionAlgorithm<FiniteStateAcceptor> {

	private FiniteStateAcceptor myTemporaryDFA;
	private ConstructMinimizeTreeStep myMinimizeTreeStep;
	private AlgorithmExecutingStep<BuildMinimalDFA> myBuildMinimalDFAStep;

	public MinimizeDFAAlgorithm(FiniteStateAcceptor fd) {
		super(fd);
	}
	
	public FiniteStateAcceptor getStartingDFA(){
		return super.getOriginalDefinition();
	}

	@Override
	public String getDescriptionName() {
		return "Minimize DFA";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BooleanWrapper[] checkOfProperForm(FiniteStateAcceptor fsa) {
		JFLAPDebug.print();
		List<BooleanWrapper> errors = new ArrayList<BooleanWrapper>();
		FSADeterminismChecker check = new FSADeterminismChecker();
		if (!check.isDeterministic(fsa))
			errors.add(new BooleanWrapper(false,"You may not minimize an NFA. It must first " +
					"be made into a DFA"));
		if(!FiniteStateAcceptor.hasAllSingleSymbolInput(fsa))
			errors.add(new BooleanWrapper(false, "The DFA to minimize must have transitions " +
					"with either 1 or 0 input symbols."));
		return errors.toArray(new BooleanWrapper[0]);
	}

	@Override
	public AlgorithmExecutingStep[] initializeAllSteps() {
		myMinimizeTreeStep = new ConstructMinimizeTreeStep();
		return new AlgorithmExecutingStep[]{
						new RemoveInacessibleStates(),
						new AddTrapStateStep(),
						myMinimizeTreeStep};
	}

	@Override
	public boolean reset() throws AlgorithmException {
		myTemporaryDFA = (FiniteStateAcceptor) this.getStartingDFA().copy();
		return true;
	}
	
	@Override
	public AlgorithmExecutingStep getCurrentStep() {
		if (myMinimizeTreeStep.isComplete() &&
				myBuildMinimalDFAStep == null){
			return myBuildMinimalDFAStep = createFinalbuildStep();
			
		}
		else if (amBuilding()){
			return myBuildMinimalDFAStep;
		}
		return (AlgorithmExecutingStep) super.getCurrentStep();
	}
	
	private AlgorithmExecutingStep<BuildMinimalDFA> createFinalbuildStep() {
		return new AlgorithmExecutingStep<BuildMinimalDFA>(){
			@Override
			public BuildMinimalDFA initializeAlgorithm() {
				MinimizeTreeModel finalTree = myMinimizeTreeStep.getAlgorithm().getMinimizeTree();
				return new BuildMinimalDFA(finalTree);
			}
			
		};
	}

	private boolean amBuilding() {
		return myBuildMinimalDFAStep != null &&
				!myBuildMinimalDFAStep.isComplete();
	}

	@Override
	public boolean isRunning() {
		return super.isRunning() || amBuilding();
	}
	
	public FiniteStateAcceptor getMinimizedDFA(){
		if (isRunning())
			throw new AlgorithmException("You must first finish the minimization " +
					"algorithm before exporting the minimal DFA.");
		return myBuildMinimalDFAStep.getAlgorithm().getMinimalDFA();
	}
	
	private class RemoveInacessibleStates extends AlgorithmExecutingStep<InacessibleStateRemover>{

		@Override
		public InacessibleStateRemover initializeAlgorithm() {
			return new InacessibleStateRemover(myTemporaryDFA);
		}
		
	}

	private class AddTrapStateStep extends AlgorithmExecutingStep<AddTrapStateAlgorithm>{

		@Override
		public AddTrapStateAlgorithm initializeAlgorithm() {
			return new AddTrapStateAlgorithm(myTemporaryDFA);
		}
		
	}

	private class ConstructMinimizeTreeStep extends AlgorithmExecutingStep<BuildMinimizeTreeAlgorithm>{
		
		@Override
		public BuildMinimizeTreeAlgorithm initializeAlgorithm() {
			return new BuildMinimizeTreeAlgorithm(myTemporaryDFA);
		}
	}
}
