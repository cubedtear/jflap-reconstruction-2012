package view.automata.tools;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import model.automata.Automaton;
import model.automata.StartState;
import model.automata.State;
import model.automata.Transition;
import model.automata.acceptors.Acceptor;
import model.automata.acceptors.FinalStateSet;
import model.automata.turing.buildingblock.BlockTuringMachine;
import model.change.events.AddEvent;
import model.change.events.RemoveEvent;
import model.change.events.SetToEvent;
import model.change.events.StartStateSetEvent;
import model.undo.CompoundUndoRedo;
import model.undo.IUndoRedo;
import model.undo.UndoKeeper;
import util.Point2DAdv;
import view.EditingPanel;
import view.automata.AutomatonEditorPanel;
import view.automata.ControlMoveEvent;
import view.automata.Note;
import view.automata.StateMoveEvent;
import debug.JFLAPDebug;

/**
 * Tool for selection and editing of Automaton graphs.
 * 
 * @author Ian McMahon
 */
public class ArrowTool<T extends Automaton<S>, S extends Transition<S>> extends
		EditingTool<T, S> {
	private T myDef;

	private Object myObject;
	private Point2D myInitialPoint;
	private Point2D myInitialObjectPoint;
	private StateMenu myStateMenu;
	private EmptyMenu myEmptyMenu;
	private Rectangle mySelectionBounds;

	public ArrowTool(AutomatonEditorPanel<T, S> panel, T def) {
		super(panel);
		myDef = def;
		myObject = null;
		myStateMenu = new StateMenu();
		myEmptyMenu = new EmptyMenu();
	}

	@Override
	public String getToolTip() {
		return "Attribute Editor";
	}

	@Override
	public char getActivatingKey() {
		return 'a';
	}

	@Override
	public String getImageURLString() {
		return "/ICON/arrow.gif";
	}

	@Override
	public void mousePressed(MouseEvent e) {
		AutomatonEditorPanel<T, S> panel = getPanel();

		myObject = panel.objectAtPoint(e.getPoint());
		if (e.getSource() instanceof Note)
			myObject = e.getSource();

		if (SwingUtilities.isLeftMouseButton(e)) {
			myInitialPoint = e.getPoint();

			if (myObject != null) {
				boolean modifierDown = e.isShiftDown() || e.isControlDown();
				List<Object> selectionList = panel.getSelection();
				boolean isSelected = isSelected(selectionList);

				if (selectionList.size() > 1 && !isSelected && !modifierDown)
					panel.clearSelection();

				if (isSelected && modifierDown) {
					panel.deselectObject(myObject);
					return;
				}

				panel.selectObject(myObject);

				if (isStateClicked(e))
					myInitialObjectPoint = new Point2DAdv(
							panel.getPointForVertex((State) myObject));
				else if (isTransitionClicked(e))
					panel.editTransition((S) myObject, false);

				else if (myObject instanceof State[]) {
					State[] edge = (State[]) myObject;
					myInitialObjectPoint = panel.getControlPoint(edge);

					panel.selectAll(myDef.getTransitions()
							.getTransitionsFromStateToState(edge[0], edge[1]));
				} else if (myObject instanceof Note) {
					myInitialObjectPoint = ((Note) myObject).getPoint();
				}
			} else {
				panel.stopAllEditing();
				panel.clearSelection();
			}
		} else if (SwingUtilities.isRightMouseButton(e)) {
			panel.stopAllEditing();
			panel.clearSelection();
			if (isStateClicked(e))
				showStateMenu(e.getPoint());
			else
				showEmptyMenu(e.getPoint());
		}
	}

	private boolean isSelected(List<Object> selectedObjects) {
		if (myObject == null)
			return false;
		boolean contained = false;
		for (Object o : selectedObjects) {
			if (myObject.equals(o) || isEdgeEqual(o)) {
				contained = true;
			}
		}
		return contained;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		AutomatonEditorPanel<T, S> panel = getPanel();
		List<Object> selectedObjs = panel.getSelection();

		if (SwingUtilities.isLeftMouseButton(e)) {

			if (myObject == null) {
				// Drag a selection rectangle, selecting everything within its
				// bounds.
				int nowX = e.getPoint().x;
				int nowY = e.getPoint().y;
				int leftX = (int) myInitialPoint.getX();
				int topY = (int) myInitialPoint.getY();
				if (nowX < leftX)
					leftX = nowX;
				if (nowY < topY)
					topY = nowY;
				mySelectionBounds = new Rectangle(leftX, topY, Math.abs(nowX
						- (int) myInitialPoint.getX()), Math.abs(nowY
						- (int) myInitialPoint.getY()));
				panel.selectAllInBounds(mySelectionBounds);
				return;
			}
			if (isSelected(selectedObjs)) {
				Point drag = e.getPoint();

				if (myObject instanceof State) {
					// Move the state
					State s = (State) myObject;

					if (selectedObjs.size() > 1) {
						moveSelectedStates(e, s, selectedObjs);
					}
					panel.moveState(s, drag);
				} else if (myObject instanceof State[]) {
					// Move the control point
					State from = ((State[]) myObject)[0], to = ((State[]) myObject)[1];
					panel.moveCtrlPoint(from, to, drag);
				} else if (myObject instanceof Note) {
					Note n = (Note) myObject;
					if (!n.isEditable()) {
						int diffX = (int) (drag.x - myInitialPoint.getX());
						int diffY = (int) (drag.y - myInitialPoint.getY());

						int nowAtX = (int) (myInitialObjectPoint.getX() + diffX);
						int nowAtY = (int) (myInitialObjectPoint.getY() + diffY);
						drag = new Point(nowAtX, nowAtY);
						panel.moveNote(n, drag);
						myInitialObjectPoint = n.getPoint();
					}
				}
			}
		}
	}

	private void moveSelectedStates(MouseEvent e, State s,
			List<Object> selectedObjs) {
		AutomatonEditorPanel<T, S> panel = getPanel();
		Point2D pState = new Point2DAdv(panel.getPointForVertex(s));
		Point2D drag = e.getPoint();

		double dx = drag.getX() - pState.getX(), dy = drag.getY()
				- pState.getY();

		for (Object o : selectedObjs) {
			if (o instanceof State && !o.equals(s)) {
				Point2D current = panel.getPointForVertex((State) o);
				panel.moveState((State) o, new Point2DAdv(current.getX() + dx,
						current.getY() + dy));
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		AutomatonEditorPanel<T, S> panel = getPanel();
		UndoKeeper keeper = getKeeper();
		boolean modified = e.isControlDown() || e.isShiftDown();

		if (SwingUtilities.isLeftMouseButton(e) && myInitialPoint != null
				&& !myInitialPoint.equals(e.getPoint())) {
			List<Object> selectedObjs = panel.getSelection();
			if (isSelected(selectedObjs)) {

				if (myObject instanceof State) {
					Point2D pRelease = e.getPoint();
					StateMoveEvent moveEvent = new StateMoveEvent(panel, myDef,
							(State) myObject, myInitialObjectPoint, pRelease);

					if (selectedObjs.size() > 1) {
						CompoundUndoRedo comp = new CompoundUndoRedo(moveEvent);
						addMoveEvents(comp, pRelease);

						keeper.registerChange(comp);
					} else {
						// Clear the selection and notify the undo keeper
						if (!modified)
							panel.clearSelection();
						keeper.registerChange(moveEvent);
					}
				} else if (myObject instanceof State[]) {
					// Notify the undo keeper
					if (isOnlyObject(selectedObjs) && !modified)
						panel.clearSelection();
					keeper.registerChange(new ControlMoveEvent(panel,
							(State[]) myObject, myInitialObjectPoint, e
									.getPoint()));
				} else if (myObject instanceof Note) {
					panel.clearSelection();
				}
			}
		} else if (!modified && !(myObject instanceof Transition))
			panel.clearSelection();
		myInitialPoint = null;
		mySelectionBounds = null;
		myInitialObjectPoint = null;
		panel.repaint();

		if (!(myObject instanceof Transition))
			panel.requestFocus();
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof Note && !(e.isControlDown() || e.isShiftDown())) {
			Note n = (Note) e.getComponent();
			n.setEnabled(true);
			n.setEditable(true);
			n.setCaretColor(null);
			n.requestFocus();
		}
	}

	private void addMoveEvents(CompoundUndoRedo comp, Point2D pRelease) {
		double dx = pRelease.getX() - myInitialPoint.getX(), dy = pRelease
				.getY() - myInitialPoint.getY();
		AutomatonEditorPanel<T, S> panel = getPanel();
		List<Object> selectedObjs = panel.getSelection();

		for (Object o : selectedObjs)
			if (o instanceof State && !o.equals(myObject)) {
				Point2D pTo = new Point2DAdv(panel.getPointForVertex((State) o));
				Point2D pFrom = new Point2DAdv(pTo.getX() - dx, pTo.getY() - dy);

				comp.add(new StateMoveEvent(panel, myDef, (State) o, pFrom, pTo));
			}
		comp.add(new ClearSelectionEvent());
	}

	private boolean isOnlyObject(List<Object> selectedObjs) {
		for (Object o : selectedObjs) {
			if (o instanceof State)
				return false;
			if (o instanceof State[] && !isEdgeEqual(o))
				return false;
			if (o instanceof Transition
					&& !isEdgeEqual(new State[] {
							((Transition) o).getFromState(),
							((Transition) o).getToState() }))
				return false;
		}
		return true;
	}

	private void showStateMenu(Point2D point) {
		myStateMenu.show(getPanel(), (int) point.getX(), (int) point.getY());
	}

	private void showEmptyMenu(Point2D point) {
		myEmptyMenu.show(getPanel(), (int) point.getX(), (int) point.getY());
	}

	/**
	 * Returns true if the selected object is a State and the user clicked once.
	 */
	private boolean isStateClicked(MouseEvent e) {
		return e.getClickCount() == 1 && myObject instanceof State;
	}

	/**
	 * Returns true if the selected object is a Transition and the user double
	 * clicked.
	 */
	private boolean isTransitionClicked(MouseEvent e) {
		return e.getClickCount() == 2 && myObject instanceof Transition;
	}

	private boolean isEdgeEqual(Object o) {
		if (!(o instanceof State[] && myObject instanceof State[]))
			return false;
		State[] o1 = (State[]) myObject, o2 = (State[]) o;
		return o1[0].equals(o2[0]) && o1[1].equals(o2[1]);
	}

	@Override
	public void draw(Graphics g) {
		if (mySelectionBounds != null)
			g.drawRect(mySelectionBounds.x, mySelectionBounds.y,
					mySelectionBounds.width, mySelectionBounds.height);
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);

		if (!active) {
			myInitialPoint = null;
			myInitialObjectPoint = null;
			myObject = null;
			mySelectionBounds = null;
		}

	}

	private class StateMenu extends JPopupMenu {

		private JCheckBoxMenuItem makeFinal;
		private JCheckBoxMenuItem makeInitial;
		private JMenuItem changeLabel;
		private JMenuItem deleteLabel;
		private JMenuItem deleteAllLabels;
		private JMenuItem editBlock;
		private JMenuItem copyBlock;
		private JMenuItem setName;

		public StateMenu() {
			if (myDef instanceof Acceptor)
				addFinalButton();

			addInitialButton();
			addLabelButtons();

			if (myDef instanceof BlockTuringMachine)
				addBlockButtons();
			addSetNameButton();
		}

		private void addFinalButton() {
			makeFinal = new JCheckBoxMenuItem("Final");

			makeFinal.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					Acceptor<S> accept = (Acceptor<S>) myDef;
					FinalStateSet finalStates = accept.getFinalStateSet();
					UndoKeeper keeper = getKeeper();

					if (makeFinal.isSelected())
						keeper.applyAndListen(new AddEvent<State>(finalStates,
								(State) myObject));
					else
						keeper.applyAndListen(new RemoveEvent<State>(
								finalStates, (State) myObject));
				}
			});
			this.add(makeFinal);
		}

		private void addInitialButton() {
			makeInitial = new JCheckBoxMenuItem("Initial");

			makeInitial.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					UndoKeeper keeper = getKeeper();
					StartState start = myDef
							.getComponentOfClass(StartState.class);
					State internal = start.getState();

					if (makeInitial.isSelected())
						keeper.applyAndListen(new StartStateSetEvent(start,
								internal, (State) myObject));
					else
						keeper.applyAndListen(new StartStateSetEvent(start,
								internal, null));

				}
			});
			this.add(makeInitial);
		}

		private void addLabelButtons() {

			changeLabel = new JMenuItem("Change Label");
			changeLabel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("Need to implement this!");
				}
			});
			deleteLabel = new JMenuItem("Clear Label");
			deleteLabel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("Need to implement this!");
				}
			});
			deleteAllLabels = new JMenuItem("Clear All Labels");
			deleteAllLabels.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("Need to implement this!");
				}
			});
			add(changeLabel);
			add(deleteLabel);
			add(deleteAllLabels);
		}

		private void addBlockButtons() {
			editBlock = new JMenuItem("Edit Block");
			editBlock.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("Need to implement this!");
				}
			});
			copyBlock = new JMenuItem("Duplicate Block");
			copyBlock.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("Need to implement this!");
				}
			});
			add(editBlock);
			add(copyBlock);
		}

		private void addSetNameButton() {
			setName = new JMenuItem("Set Name");
			setName.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					State state = (State) myObject;
					String oldName = state.getName();
					String name = (String) JOptionPane.showInputDialog(
							getPanel(), "Input a new name, or \n"
									+ "set blank to remove the name",
							"New Name", JOptionPane.QUESTION_MESSAGE, null,
							null, oldName);
					if (name == null || name.equals(oldName))
						return;
					State newState = new State(name, state.getID());
					getKeeper()
							.applyAndListen(
									new SetToEvent<State>(state, state.copy(),
											newState));
				}
			});
			add(setName);
		}

		@Override
		public void show(Component invoker, int x, int y) {
			super.show(invoker, x, y);
			if (makeFinal != null) // only happens when def instanceof Acceptor
				makeFinal.setSelected(Acceptor.isFinalState(
						(Acceptor<S>) myDef, (State) myObject));
			makeInitial.setSelected(Automaton.isStartState(myDef,
					(State) myObject));
		}
	}

	private class ClearSelectionEvent implements IUndoRedo {

		@Override
		public boolean undo() {
			getPanel().clearSelection();
			return true;
		}

		@Override
		public boolean redo() {
			return undo();
		}

		@Override
		public String getName() {
			return "Clear selected objects";
		}

	}

	/**
	 * The contextual menu class for context clicks in blank space.
	 */
	private class EmptyMenu extends JPopupMenu {

		private JCheckBoxMenuItem stateLabels;
		private JMenuItem layoutGraph;
		private JMenuItem renameStates;
		private JMenuItem createNote;
		private JCheckBoxMenuItem autoZoom;
		private Point myPoint;

		public EmptyMenu() {
			addStateLabels();
			addLayoutGraph();
			addRenameStates();
			addCreateNote();
			addAutoZoom();
		}

		private void addStateLabels() {
			stateLabels = new JCheckBoxMenuItem("Display State Labels");
			stateLabels.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("NEEDS IMPLEMENTING!");
				}
			});
			add(stateLabels);
		}

		private void addLayoutGraph() {
			layoutGraph = new JMenuItem("Layout Graph");
			layoutGraph.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("NEEDS IMPLEMENTING!");
				}
			});
			this.add(layoutGraph);
		}

		private void addRenameStates() {
			renameStates = new JMenuItem("Rename States");
			renameStates.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("NEEDS IMPLEMENTING!");
				}
			});
			this.add(renameStates);
		}

		private void addCreateNote() {
			createNote = new JMenuItem("Create Note");
			createNote.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPanel().createAndAddNote(myPoint);
				}
			});
			this.add(createNote);
		}

		private void addAutoZoom() {
			autoZoom = new JCheckBoxMenuItem("Auto-Zoom");
			autoZoom.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JFLAPDebug.print("NEEDS IMPLEMENTING!");
				}
			});
			this.add(autoZoom);
		}

		public void show(Component comp, int x, int y) {
			// stateLabels.setSelected(getDrawer().doesDrawStateLabels());
			// adaptView.setSelected(getView().getAdapt());
			myPoint = new Point(x, y);
			super.show(comp, x, y);
		}
	}
}
