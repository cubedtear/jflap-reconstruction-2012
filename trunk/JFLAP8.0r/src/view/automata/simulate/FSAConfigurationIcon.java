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





package view.automata.simulate;

import java.awt.Component;
import java.awt.Graphics2D;

import model.algorithms.testinput.simulate.configurations.FSAConfiguration;
import model.automata.acceptors.fsa.FSATransition;
import model.automata.acceptors.fsa.FiniteStateAcceptor;
import model.symbols.SymbolString;

/**
 * This is a configuration icon for configurations related to finite state
 * automata. These sorts of configurations are defined only by the state that
 * the automata is current in, plus the input left.
 * 
 * @author Thomas Finley
 */

public class FSAConfigurationIcon extends ConfigurationIcon<FiniteStateAcceptor, FSATransition> {
	
	public FSAConfigurationIcon(FSAConfiguration configuration) {
		super(configuration);
	}

	@Override
	public int getIconHeight() {
		// TODO Auto-generated method stub
		return super.getIconHeight()*2;
	}
	/**
	 * This will paint a sort of "torn tape" object that shows the rest of the
	 * input.
	 * 
	 * @param c
	 *            the component this icon is drawn on
	 * @param g
	 *            the <CODE>Graphics2D</CODE> object to draw on
	 * @param width
	 *            the width the configuration is painted in
	 * @param height
	 *            the height that the configuration is painted in
	 */
	public void paintConfiguration(Component c, Graphics2D g, int width,
			int height) {
		super.paintConfiguration(c, g, width, height);
		FSAConfiguration config = (FSAConfiguration) getConfiguration();
		// Draw the torn tape with the rest of the input.
		Torn.paintSymbolString((Graphics2D) g, config.getPrimaryString(),
				RIGHT_STATE.x + 5.0f, ((float) height) * 0.5f, Torn.MIDDLE,
				width - RIGHT_STATE.x - 5.0f, false, true, config.getPrimaryPosition());
	}
}
