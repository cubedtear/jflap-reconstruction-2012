package file.xml.graph;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import model.automata.Automaton;
import model.automata.State;
import model.automata.Transition;
import model.graph.TransitionGraph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import file.xml.StructureTransducer;
import file.xml.TransducerFactory;
import file.xml.XMLHelper;
import file.xml.formaldef.automata.AutomatonTransducer;

public class TransitionGraphTransducer extends
		StructureTransducer<TransitionGraph<? extends Transition<?>>> {

	private StatePointMapTransducer sMapTransducer = new StatePointMapTransducer();
	private ControlPointMapTransducer cpMapTransducer = new ControlPointMapTransducer();

	@Override
	public String getTag() {
		return TRANS_GRAPH_TAG;
	}

	@Override
	public TransitionGraph<? extends Transition<?>> fromStructureRoot(
			Element root) {
		Element auto_elem = XMLHelper.getChildArray(root, STRUCTURE_TAG).get(0);
		AutomatonTransducer trans = (AutomatonTransducer) StructureTransducer
				.getStructureTransducer(auto_elem);
		Automaton<?> a = (Automaton<?>) trans.fromStructureRoot(auto_elem);

		Element sMap_elem = XMLHelper.getChildArray(root, STATE_POINT_MAP).get(
				0);
		Map<Integer, Point2D> sMap = sMapTransducer
				.fromStructureRoot(sMap_elem);

		Element cpMap_elem = XMLHelper.getChildArray(root, CTRL_POINT_MAP).get(
				0);
		Map<Point, Point2D> cpMap = cpMapTransducer
				.fromStructureRoot(cpMap_elem);

		TransitionGraph<?> graph = new TransitionGraph(a);

		for (State s : a.getStates()) {
			graph.moveVertex(s, sMap.get(s.getID()));
		}
		for (Transition<?> t : a.getTransitions()) {
			State from = t.getFromState(), to = t.getToState();
			Point edge = new Point(from.getID(), to.getID());

			Point2D ctrl = cpMap.get(edge);
			graph.setControlPt(ctrl, from, to);
		}
		return graph;
	}

	@Override
	public Element appendComponentsToRoot(Document doc,
			TransitionGraph<? extends Transition<?>> graph, Element root) {
		Automaton<?> auto = graph.getAutomaton();
		AutomatonTransducer autoTrans = (AutomatonTransducer) TransducerFactory
				.getTransducerForStructure(auto);

		Map<Integer, Point2D> stateMap = new TreeMap<Integer, Point2D>();
		Map<Point, Point2D> ctrlMap = new HashMap<Point, Point2D>();

		for (State s : auto.getStates()) {
			Point2D p = graph.pointForVertex(s);
			stateMap.put(s.getID(), p);

			for (State v : graph.adjacent(s)) {
				Point fromTo = new Point(s.getID(), v.getID());
				p = graph.getControlPt(s, v);
				ctrlMap.put(fromTo, p);
			}
		}
		root.appendChild(autoTrans.toXMLTree(doc, auto));
		root.appendChild(sMapTransducer.toXMLTree(doc, stateMap));
		root.appendChild(cpMapTransducer.toXMLTree(doc, ctrlMap));
		return root;
	}

}
