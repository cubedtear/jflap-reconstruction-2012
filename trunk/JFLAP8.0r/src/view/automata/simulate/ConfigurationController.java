package view.automata.simulate;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import debug.JFLAPDebug;

import view.automata.AutomatonEditorPanel;

import model.algorithms.testinput.simulate.Configuration;
import model.algorithms.testinput.simulate.ConfigurationChain;
import model.algorithms.testinput.simulate.SingleInputSimulator;
import model.algorithms.testinput.simulate.configurations.tm.BlockTMConfiguration;
import model.algorithms.testinput.simulate.configurations.tm.TMConfiguration;
import model.automata.Automaton;
import model.automata.turing.MultiTapeTuringMachine;
import model.automata.turing.TuringMachine;
import model.automata.turing.buildingblock.Block;

/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */

/**
 * This is an intermediary object between the simulator GUI and the automaton
 * simulators.
 * 
 * @author Thomas Finley
 */

public class ConfigurationController extends JToolBar {

	private static final String NO_CONFIGURATION_ERROR = "Select at least one configuration!";
	private static final String NO_CONFIGURATION_ERROR_TITLE = "No Configuration Selected";
	private static final String FOCUS_CONFIGURATION_ERROR = "JFLAP can only focus on one configuration at a time!";
	private static final String FOCUS_CONFIGURATION_ERROR_TITLE = "Too many myConfigPanel selected";
	private ConfigurationPanel myConfigPanel;
	private SimulatorPanel mySimPanel;
	private SingleInputSimulator mySimulator;
	private Map<ConfigurationChain, TraceWindow> configurationToTraceWindow;

	/**
	 * Instantiates a new configuration controller.
	 * 
	 * @param pane
	 *            the pane from which we retrieve myConfigPanel
	 * @param simulator
	 *            the automaton simulator
	 * @param drawer
	 *            the drawer of the automaton
	 * @param component
	 *            the component in which the automaton is displayed
	 */

	public ConfigurationController(ConfigurationPanel configPanel,
			SimulatorPanel simPanel) {
		myConfigPanel = configPanel;
		mySimPanel = simPanel;
		mySimulator = simPanel.getSimulator();
		initView();
		update();
		changeSelection();
	}

	private void initView() {
		configurationToTraceWindow = new HashMap<ConfigurationChain, TraceWindow>();
		this.add(new TooltipAction("Step", "Moves existing valid "
				+ "myConfigPanel to the next " + "myConfigPanel.") {
			public void actionPerformed(ActionEvent e) {
				step();
			}
		});

		this.add(new TooltipAction("Reset", "Resets the simulation to "
				+ "start conditions.") {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		/*
		 * Add Focus and Defocus buttons only if it is a Turing machine.
		 */
		if (isTuringMachine()) {
			this.add(new AbstractAction("Focus") {
				public void actionPerformed(ActionEvent e) {
					focus();
				}
			});

			this.add(new AbstractAction("Defocus") {
				public void actionPerformed(ActionEvent e) {
					defocus();
				}
			});
		}
		this.add(new AbstractAction("Freeze") {
			public void actionPerformed(ActionEvent e) {
				freeze();
			}
		});

		this.add(new AbstractAction("Thaw") {
			public void actionPerformed(ActionEvent e) {
				thaw();
			}
		});

		this.add(new AbstractAction("Trace") {
			public void actionPerformed(ActionEvent e) {
				trace();
			}
		});

		this.add(new AbstractAction("Remove") {
			public void actionPerformed(ActionEvent e) {
				remove();
			}
		});
		
	}

	/**
	 * This sets the configuration pane to have the initial configuration for
	 * this input.
	 */
	public void reset() {
		mySimulator.reset();
		update();
	}

	/**
	 * This method should be called when the simulator pane that this
	 * configuration controller belongs to is removed from the environment. This
	 * will remove all of the open configuration trace windows.
	 */
	public void cleanup() {
		for (TraceWindow win : configurationToTraceWindow.values())
			win.dispose();
		configurationToTraceWindow.clear();
	}

	/**
	 * The step method takes all myConfigPanel from the configuration pane, and
	 * replaces them with "successor" transitions.
	 * 
	 * @param blockStep
	 */
	public void step() {
		mySimulator.step();
		update();
	}

	/**
	 * Freezes selected myConfigPanel.
	 */
	public void freeze() {
		List<Configuration> configs = myConfigPanel.getSelected();
		if (configs.isEmpty()) {
			JOptionPane.showMessageDialog(myConfigPanel,
					NO_CONFIGURATION_ERROR, NO_CONFIGURATION_ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (ConfigurationChain chain : mySimulator.getChains()) {
			Configuration config = chain.getCurrentConfiguration();

			if (configs.contains(config)
					&& myConfigPanel.getState(config) == ConfigurationButton.NORMAL) {
				mySimulator.freezeConfigurationChain(chain);
				myConfigPanel.setFrozen(config);
			}
		}
		myConfigPanel.deselectAll();
		myConfigPanel.repaint();
	}

	/**
	 * Removes the selected myConfigPanel.
	 */
	public void remove() {
		List<Configuration> configs = myConfigPanel.getSelected();

		if (configs.isEmpty()) {
			JOptionPane.showMessageDialog(myConfigPanel,
					NO_CONFIGURATION_ERROR, NO_CONFIGURATION_ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (ConfigurationChain chain : mySimulator.getChains()) {
			Configuration config = chain.getCurrentConfiguration();

			if (configs.contains(chain.getCurrentConfiguration())) {
				myConfigPanel.remove(config);
				mySimulator.removeConfigurationChain(chain);
			}
		}
		myConfigPanel.validate();
		myConfigPanel.repaint();
	}

	/**
	 * Zooms in on selected configuration
	 */
	public void focus() {
		List<Configuration> configs = myConfigPanel.getSelected();
		if (configs.isEmpty()) {
			JOptionPane.showMessageDialog(myConfigPanel,
					NO_CONFIGURATION_ERROR, NO_CONFIGURATION_ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
			return;
		} else if (configs.size() > 1) {
			JOptionPane.showMessageDialog(myConfigPanel,
					FOCUS_CONFIGURATION_ERROR, FOCUS_CONFIGURATION_ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		Configuration toFocus = configs.get(0);
		myConfigPanel.setFocused(toFocus);
		mySimPanel.repaint();
	}

	/**
	 * Sets the drawer to draw the selected configurations' states as selected,
	 * or to draw all configurations' states as selected in the event that there
	 * are no selected configurations. In this case, the selection refers to the
	 * selection of states within the automata, though
	 */
	public void changeSelection() {
		AutomatonEditorPanel panel = mySimPanel.getEditorPanel();
		panel.clearSelection();

		List<Configuration> configs = myConfigPanel.getConfigurations();
		for (Configuration current : configs) {
			panel.selectObject(current.getState());
		}
		mySimPanel.repaint();
	}

	// private boolean setFocusIfNeeded(Configuration current, boolean
	// foundFocused) {
	// Configuration parentConfig = current.getParent();
	// if (parentConfig == null)
	// return foundFocused;
	//
	// if (parentConfig.getFocused()) {
	// current.setFocused(true);
	// if (!foundFocused) {
	// myConfigPanel.setFocused(current);
	// current.setFocused(true);
	//
	// foundFocused = true;
	// }
	// }
	// return foundFocused;
	// }

	public void defocus() {
		for (Configuration config : myConfigPanel.getConfigurations()) {
			if (myConfigPanel.isFocused(config)) {
				myConfigPanel.defocus(config);
			}
		}
	}

	/**
	 * Thaws the selected myConfigPanel.
	 */
	public void thaw() {
		List<Configuration> configs = myConfigPanel.getSelected();

		if (configs.isEmpty()) {
			JOptionPane.showMessageDialog(myConfigPanel,
					NO_CONFIGURATION_ERROR, NO_CONFIGURATION_ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (ConfigurationChain chain : mySimulator.getChains()) {
			Configuration config = chain.getCurrentConfiguration();

			if (configs.contains(config))
				mySimulator.thawConfigurationChain(chain);
		}
		update();
	}

	/**
	 * Given the selected myConfigPanel, shows their "trace."
	 */
	public void trace() {
		List<Configuration> configs = myConfigPanel.getSelected();
		if (configs.isEmpty()) {
			JOptionPane.showMessageDialog(myConfigPanel,
					NO_CONFIGURATION_ERROR, NO_CONFIGURATION_ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (ConfigurationChain chain : mySimulator.getChains()) {
			if (configs.contains(chain.getCurrentConfiguration())) {
				TraceWindow window = (TraceWindow) configurationToTraceWindow
						.get(chain);
				if (window == null) {
					configurationToTraceWindow.put(chain,
							new TraceWindow(chain));
				} else {
					window.update(chain);
					window.toFront();
				}
			}
		}
	}

	private void update() {
		myConfigPanel.clear();

		for (ConfigurationChain chain : mySimulator.getChains()) {
			Configuration current = chain.getCurrentConfiguration();

			int configState = ConfigurationButton.NORMAL;
			if (chain.isAccept())
				configState = ConfigurationButton.ACCEPT;
			else if (chain.isReject())
				configState = ConfigurationButton.REJECT;
			else if (chain.isFrozen())
				configState = ConfigurationButton.FREEZE;

			myConfigPanel.add(current, configState);
			mySimPanel.getEditorPanel().selectObject(current.getState());
		}

		myConfigPanel.revalidate();
		myConfigPanel.repaint();
		// Change them darned selections.
		changeSelection();
		mySimPanel.update();
	}

	/**
	 * This method is used to find out if we need the <b>Focus</b> and
	 * <b>Defocus</b> buttons in the simulator.
	 * 
	 * @return <code>true</code> if the automaton is a turing machine,
	 *         <code>false</code> otherwise
	 * @author Jinghui Lim
	 */
	private boolean isTuringMachine() {
		return getAutomaton() instanceof MultiTapeTuringMachine;
	}

	private Automaton getAutomaton() {
		return mySimPanel.getEditorPanel().getAutomaton();
	}
}
// /**
// * @param blockStep
// */
// public void setBlock(boolean step) {
// blockStep = step;
//
// }
//
// private boolean blockStep = false;