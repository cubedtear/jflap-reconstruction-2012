package test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import util.UtilFunctions;

import file.xml.XMLCodec;

import model.algorithms.SteppableAlgorithm;
import model.algorithms.conversion.autotogram.PDAtoCFGConverter;
import model.automata.InputAlphabet;
import model.automata.StartState;
import model.automata.State;
import model.automata.StateSet;
import model.automata.TransitionSet;
import model.automata.acceptors.FinalStateSet;
import model.automata.acceptors.pda.BottomOfStackSymbol;
import model.automata.acceptors.pda.PDATransition;
import model.automata.acceptors.pda.PushdownAutomaton;
import model.automata.acceptors.pda.StackAlphabet;
import model.formaldef.components.symbols.Symbol;
import model.formaldef.components.symbols.SymbolString;
import model.formaldef.components.symbols.Terminal;
import model.formaldef.components.symbols.Variable;
import model.grammar.*;
import model.grammar.transform.CNFConverter;
import model.grammar.transform.LambdaProductionRemover;
import model.grammar.transform.UnitProductionRemover;
import model.grammar.transform.UselessProductionRemover;
import model.languages.StringGenerator;
import model.regex.OperatorAlphabet;
import model.regex.RegularExpression;
import model.regex.RegularExpressionGrammar;

public class StringGeneratorTest {

	public static void main (String[] args) {
//
//		Variable S = new Variable("S"), NP = new Variable("NP"),VP = new Variable("VP"),
//				PP = new Variable("PP"), V = new Variable("V"), P = new Variable("P"),
//				Det = new Variable("Det"), N = new Variable("N");
//		Terminal eats = new Terminal("eats"), she = new Terminal("she"), with = new Terminal("with"),
//				fish = new Terminal("fish"), fork = new Terminal("fork"), a = new Terminal("a");
//		VariableAlphabet v = new VariableAlphabet();
//		v.addAll(S,NP,VP,PP,V,P,Det,N);
//		TerminalAlphabet t = new TerminalAlphabet();
//		t.addAll(eats,she,with,fish,fork,a);
//		ProductionSet p = new ProductionSet();
//		p.add(new Production(S, NP,VP));
//		p.add(new Production(VP, VP, PP));
//		p.add(new Production(VP, V,NP));
//		p.add(new Production(VP, eats));
//		p.add(new Production(PP, P, NP));
//		p.add(new Production(NP, Det,N));
//		p.add(new Production(NP, she));
//		p.add(new Production(V, eats));
//		p.add(new Production(P, with));
//		p.add(new Production(N, fish));
//		p.add(new Production(N, fork));
//		p.add(new Production(Det, a));
//		StartVariable s = new StartVariable("S");

		
//		StateSet states = new StateSet();
//		InputAlphabet input = new InputAlphabet();
//		StackAlphabet stack = new StackAlphabet();
//		TransitionSet<PDATransition> transitions = new TransitionSet<PDATransition>();
//		StartState start = new StartState();
//		FinalStateSet finalStates = new FinalStateSet();
//		BottomOfStackSymbol bos = new BottomOfStackSymbol();
//		PushdownAutomaton pda = new PushdownAutomaton(states, 
//														input, 
//														stack,
//														transitions, 
//														start, 
//														bos,
//														finalStates);
//		
//		for (char i = 'a'; i <= 'z'; i++){
//			pda.getInputAlphabet().add(new Symbol(Character.toString(i)));
//			pda.getStackAlphabet().add(new Symbol(Character.toString(i)));
//		}
//		
//		pda.setBottomOfStackSymbol(new Symbol("z"));
//		
//		State q0 = new State("Z0", 0);
//		State q1 = new State("Z1", 1);
//		State q2 = new State("Z2", 2);
//		State q3 = new State("Z3", 3);
//
//		pda.getStates().addAll(Arrays.asList(new State[]{q0,q1,q2,q3}));
//		pda.setStartState(q0);
//		pda.getFinalStateSet().add(q3);
//		
//		Terminal A = new Terminal("a");
//		Terminal B = new Terminal("b");
//		
//		
//		PDATransition t0 = new PDATransition(q0, q1, new SymbolString(A), 
//				new SymbolString(bos.toSymbolObject()), new SymbolString(A,bos.toSymbolObject()));
//		PDATransition t1 = new PDATransition(q1, q1, new SymbolString(A), new SymbolString(A), new SymbolString(A,A));
//		PDATransition t2 = new PDATransition(q1, q2, new SymbolString(B), new SymbolString(A), new SymbolString());
//		PDATransition t3 = new PDATransition(q2, q2, new SymbolString(B), new SymbolString(A), new SymbolString());
//		PDATransition t4 = new PDATransition(q2, q3, new SymbolString(), 
//				new SymbolString(bos.toSymbolObject()), new SymbolString());
//		
//		pda.getTransitions().addAll((Arrays.asList(new PDATransition[]{t0,t1,t2,t3,t4})));
//		pda.trimAlphabets();
//		
//		
//		SteppableAlgorithm converter = new PDAtoCFGConverter(pda);
//		converter.stepToCompletion();

//		Grammar gram = new Grammar(v,t,p,s);
//		StringGenerator gen = new StringGenerator(gram);
//		System.out.println("Strings: "+ UtilFunctions.createDelimitedString(gen.generateStringsOfLength(5),"\n"));

//		Grammar CFG = ((PDAtoCFGConverter) converter).getConvertedGrammar();
//		
//		LambdaProductionRemover lambda = new LambdaProductionRemover(CFG);
//		lambda.stepToCompletion();
//		Grammar ans = lambda.getTransformedGrammar();
//		
//		UnitProductionRemover unit = new UnitProductionRemover(ans);
//		unit.stepToCompletion();
//		ans = unit.getTransformedGrammar();
//		
//		UselessProductionRemover useless = new UselessProductionRemover(ans);
//		useless.stepToCompletion();
//		ans = useless.getTransformedGrammar();
//		
//		System.out.println(ans.toString());
//		
//		InputAlphabet in = new InputAlphabet();
//		for (char i = 'a'; i <= 'z'; i++){
//			input.add(new Symbol(Character.toString(i)));
//		}
//		in.add(new Symbol(Character.toString('a')));
//		in.add(new Symbol(Character.toString('b')));
//		RegularExpressionGrammar grammar = new RegularExpressionGrammar(in, new OperatorAlphabet());
//		grammar.trimAlphabets();
//		
//		StringGenerator gen2 = new StringGenerator(grammar);
//		System.out.println("Strings: "+ UtilFunctions.createDelimitedString(gen2.generateStringsBrute(),"\n"));
//		System.out.println("Strings2: "+UtilFunctions.createDelimitedString(gen2.generateContextFreeStrings(9),"\n"));
//		System.out.println("Strings3: "+UtilFunctions.createDelimitedString(gen2.generateStringsOfLength(5), "\n"));
//		
		XMLCodec codec = new XMLCodec();
		RegularExpression regex = (RegularExpression) codec.decode(new File(System.getProperties().getProperty("user.dir") +"/filetest/regEx.jff"));
		StringGenerator LLgen = new StringGenerator(regex);

		List<SymbolString> list = LLgen.generateStringsOfLength(8);
		System.out.println(UtilFunctions.createDelimitedString(list, "\n"));
	}
	
}
