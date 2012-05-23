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





package model.grammar.parsing.brute;

import java.util.EventListener;

/**
 * The listener to a brute force parser accepts brute force events generated by
 * a brute force parser.
 * 
 * @author Thomas Finley
 */

public interface BruteParserListener extends EventListener {
	/**
	 * A brute parser will call this method when a brute parser's state changes.
	 * 
	 * @param event
	 *            the brute parse event generated by a parser
	 */
	public void bruteParserStateChange(BruteParserEvent event);
}
