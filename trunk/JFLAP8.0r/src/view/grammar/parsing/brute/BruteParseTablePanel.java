package view.grammar.parsing.brute;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import model.algorithms.testinput.parse.Parser;
import model.algorithms.testinput.parse.brute.UnrestrictedBruteParser;
import model.change.events.AdvancedChangeEvent;
import view.grammar.parsing.RunningView;

/**
 * Running View for Brute Parser, updates each step, the number of nodes, and
 * current sentential form.
 * 
 * @author Ian McMahon
 * 
 */
public class BruteParseTablePanel extends RunningView {

	public BruteParseTablePanel(UnrestrictedBruteParser parser) {
		super("Brute Parse Table", parser);
		JTable table = getTable();

		table.setDefaultRenderer(Object.class, createRenderer());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		TableColumnModel colModel = table.getColumnModel();

		TableColumn levels = colModel.getColumn(0);
		levels.setMinWidth(70);
		levels.setMaxWidth(100);

		TableColumn nodes = colModel.getColumn(1);
		nodes.setMinWidth(175);
		nodes.setMaxWidth(200);
	}

	@Override
	public AbstractTableModel createModel(Parser parser) {
		return new BruteTableModel((UnrestrictedBruteParser) parser);
	}

	private class BruteTableModel extends AbstractTableModel implements
			ChangeListener {

		private UnrestrictedBruteParser parser;
		private HashMap<Integer, Object[]> myData;

		public BruteTableModel(UnrestrictedBruteParser parser) {
			this.parser = parser;
			myData = new HashMap<Integer, Object[]>();
			parser.addListener(this);
		}

		@Override
		public String getColumnName(int column) {
			String[] names = new String[] { "Level", "Total Nodes",
					"Current Derivations" };
			return names[column];
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return parser.getLevel();
		}

		@Override
		public Object getValueAt(int row, int column) {
			int level = row + 1;
			if (column == 0)
				return level;
			return myData.get(level)[column - 1];
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (e instanceof AdvancedChangeEvent) {
				AdvancedChangeEvent event = (AdvancedChangeEvent) e;
				if (event.getType() == UnrestrictedBruteParser.LEVEL_CHANGED) {
					myData.put((Integer) event.getArg(0),
							new Object[] { event.getArg(1), event.getArg(2) });
				}
			}
			fireTableRowsInserted(myData.size()-1, myData.size()-1);
			BruteParseTablePanel.this.repaint();
		}
	}

	private DefaultTableCellRenderer createRenderer() {
		return new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				JLabel label = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				return label;
			};
		};
	}
}
