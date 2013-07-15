package view.automata.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.automata.turing.TapeAlphabet;
import model.automata.turing.TuringMachine;
import model.automata.turing.TuringMachineMove;
import model.automata.turing.buildingblock.Block;
import model.automata.turing.buildingblock.BlockSet;
import model.automata.turing.buildingblock.BlockTransition;
import model.automata.turing.buildingblock.BlockTuringMachine;
import model.automata.turing.buildingblock.library.CopyBlock;
import model.automata.turing.buildingblock.library.HaltBlock;
import model.automata.turing.buildingblock.library.MoveBlock;
import model.automata.turing.buildingblock.library.MoveUntilBlock;
import model.automata.turing.buildingblock.library.MoveUntilNotBlock;
import model.automata.turing.buildingblock.library.ShiftBlock;
import model.automata.turing.buildingblock.library.StartBlock;
import model.automata.turing.buildingblock.library.StartHaltBlock;
import model.automata.turing.buildingblock.library.WriteBlock;
import model.symbols.Symbol;
import universe.JFLAPUniverse;
import universe.preferences.JFLAPPreferences;
import util.JFLAPConstants;
import view.automata.BlockEditorPanel;
import view.environment.JFLAPEnvironment;
import file.FileJFLAPException;
import file.XMLFileChooser;
import file.xml.XMLCodec;

public class BlockStateTool extends
		StateTool<BlockTuringMachine, BlockTransition> {

	private static final int PADDING = 7; // Used to get dialog to popup in
											// correct location
	private JDialog dial;
	private JComboBox<String> moves;
	private JTextField symArea;

	public BlockStateTool(BlockEditorPanel panel, BlockTuringMachine def) {
		super(panel, def);
	}

	@Override
	public String getImageURLString() {
		return "/ICON/blocks.gif";
	}

	@Override
	public char getActivatingKey() {
		return 'b';
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			String[] options = new String[] { "Import from file",
					"Use library building block", "Create new block" };

			int n = JOptionPane.showOptionDialog(
					JFLAPUniverse.getActiveEnvironment(),
					"Choose Block creation type:", "Block Creation",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

			if (n == JOptionPane.YES_OPTION)
				importFromFile();
			else if (n == JOptionPane.NO_OPTION)
				promptBuiltinBlock();
			else if (n == JOptionPane.CANCEL_OPTION)
				createNewBlock();
			Block b = (Block) getState();
			if (b != null) {
				((BlockEditorPanel) getPanel()).addBlock(b, e.getPoint());
			}
			super.mouseReleased(e);
		} else
			super.mousePressed(e);
	}

	private void importFromFile() {
		XMLFileChooser chooser = new XMLFileChooser();

		int n = chooser.showOpenDialog(null);
		if (n != JFileChooser.APPROVE_OPTION)
			return;

		File f = chooser.getSelectedFile();
		XMLCodec codec = new XMLCodec();
		Object o = codec.decode(f);

		if (!(o instanceof TuringMachine))
			throw new FileJFLAPException(
					"Only Turing Machine files can be imported as building blocks!");
		TuringMachine machine = (TuringMachine) o;
		String name = f.getName();
		int last = name.lastIndexOf('.');
		name = name.substring(0, last);
		
		setState(new Block(machine, name, getNextID()));
	}

	private void promptBuiltinBlock() {
		JPanel panel = new JPanel(new GridLayout(4, 1));

		JPanel titlePanel = new JPanel();
		JLabel title = new JLabel("Select block type:");
		title.setFont(title.getFont().deriveFont(
				(float) (0.35 * JFLAPPreferences.getDefaultTextSize())));
		titlePanel.add(title);
		panel.add(titlePanel);

		panel.add(createTopButtonPanel());
		panel.add(createBottomButtonPanel());
		
		JPanel input = new JPanel(new BorderLayout());
		JPanel left = new JPanel();
		JLabel symbol = new JLabel("Symbol: ");
		symArea = new JTextField(JFLAPConstants.BLANK, 10);
		
		left.add(symbol);
		left.add(symArea);
		
		left.add(new JButton(new AddBlankSymbolAction()));
		
		JPanel right = new JPanel();
		JLabel dir = new JLabel("Direction: ");
		moves = new JComboBox<String>(new String[]{"R", "L"});
		right.add(dir);
		right.add(moves);
		
		input.add(left, BorderLayout.WEST);
		input.add(right, BorderLayout.EAST);
		
		panel.add(input);

		JFLAPEnvironment env = JFLAPUniverse.getActiveEnvironment();
		dial = new JDialog(env, "Built-in Blocks", true);
		dial.add(panel);

		Rectangle bounds = env.getBounds();
		Dimension boxSize = dial.getPreferredSize();
		dial.setLocation((int) (bounds.getCenterX() - boxSize.width / 2)
				- PADDING, (int) (bounds.getCenterY() - boxSize.height));

		dial.pack();
		dial.setVisible(true);

	}

	private void createNewBlock() {
		int id = getNextID();
		setState(new StartHaltBlock(JFLAPPreferences.getDefaultStateNameBase()
				+ id, id));
	}

	private JPanel createTopButtonPanel() {
		JPanel middlePanel = new JPanel();
		JButton start = new JButton(new StartBlockAction()), 
				move = new JButton(new MoveBlockAction()), 
				mUntil = new JButton(new MoveUntilAction()), 
				shift = new JButton(new ShiftBlockAction());
		
		start.setToolTipText("Initial State.");
		move.setToolTipText("Move head one space on tape in specified direction.");
		mUntil.setToolTipText("Move specified direction until symbol is found.");
		shift.setToolTipText("Delete symbol under head and shift tape one space in specified direction.");
		
		middlePanel.add(start);
		middlePanel.add(move);
		middlePanel.add(mUntil);
		middlePanel.add(shift);
		
		return middlePanel;
	}
	
	private JPanel createBottomButtonPanel() {
		JPanel bottomPanel = new JPanel();
		JButton halt = new JButton(new HaltBlockAction()), 
				write = new JButton(new WriteBlockAction()), 
				mUNot = new JButton(new MoveUntilNotAction()), 
				copy = new JButton(new CopyBlockAction());
		
		halt.setToolTipText("Final State.");
		write.setToolTipText("Write specified symbol under head.");
		mUNot.setToolTipText("Move specified direction until a symbol different from specified symbol is found.");
		copy.setToolTipText("Duplicates tape.");

		bottomPanel.add(halt);
		bottomPanel.add(write);
		bottomPanel.add(mUNot);
		bottomPanel.add(copy);
		return bottomPanel;
	}
	
	private int getNextID() {
		BlockSet blocks = getDef().getStates();
		return blocks.getNextUnusedID();
	}
	
	private TuringMachineMove getMove() {
		return TuringMachineMove.getMove((String) moves.getSelectedItem());
	}
	
	private TapeAlphabet getTape() {
		return getDef().getTapeAlphabet();
	}
	
	private Symbol getSymbol() {
		return new Symbol(symArea.getText());
	}
	
	private class DialogDisposeAction extends AbstractAction{
		public DialogDisposeAction(String name){
			super(name +" Block");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dial.dispose();
			dial = null;
		}
	}
	
	private class AddBlankSymbolAction extends AbstractAction{
		
		public AddBlankSymbolAction() {
			super("Set to Blank Symbol");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			symArea.setText(JFLAPConstants.BLANK);
		}
		
	}

	private class StartBlockAction extends DialogDisposeAction{

		public StartBlockAction(){
			super("Start");
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setState(new StartBlock(getNextID()));
			super.actionPerformed(arg0);
		}
		
	}
	
	private class MoveBlockAction extends DialogDisposeAction{
		public MoveBlockAction() {
			super("Move");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setState(new MoveBlock(getMove(), getTape(), getNextID()));
			super.actionPerformed(e);
		}
	}
	
	private class MoveUntilAction extends DialogDisposeAction{
		public MoveUntilAction() {
			super("Move Until");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setState(new MoveUntilBlock(getMove(), getSymbol(), getTape(), getNextID()));
			super.actionPerformed(e);
		}
	}
	
	private class ShiftBlockAction extends DialogDisposeAction{
		public ShiftBlockAction() {
			super("Shift");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setState(new ShiftBlock(getMove(), getTape(), getNextID()));
			super.actionPerformed(e);
		}
	}

	private class HaltBlockAction extends DialogDisposeAction{
		public HaltBlockAction() {
			super("Final");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setState(new HaltBlock(getNextID()));
			super.actionPerformed(e);
		}
	}
	
	private class WriteBlockAction extends DialogDisposeAction{
		public WriteBlockAction() {
			super("Write");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setState(new WriteBlock(getSymbol(), getTape(), getNextID()));
			super.actionPerformed(e);
		}
	}
	
	private class MoveUntilNotAction extends DialogDisposeAction{
		public MoveUntilNotAction() {
			super("Move Until Not");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setState(new MoveUntilNotBlock(getMove(), getSymbol(), getTape(), getNextID()));
			super.actionPerformed(e);
		}
	}
	
	private class CopyBlockAction extends DialogDisposeAction{
		public CopyBlockAction() {
			super("Copy");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setState(new CopyBlock(getTape(), getNextID()));
			super.actionPerformed(e);
		}
	}
}
