package model.grammar.parsing.cyk.gui;

import javax.swing.table.DefaultTableModel;

import model.symbols.Symbol;

@SuppressWarnings("serial")
public class CYKTableModel extends DefaultTableModel {

	public CYKTableModel (Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		
		
	}

	
	public void addVariable (int row, int col, Symbol varToAdd) {
		
	}
	

}
