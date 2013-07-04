package view.automata.undoing;

import java.awt.Point;

import view.automata.AutomatonEditorPanel;
import view.automata.Note;

import model.undo.IUndoRedo;

public class NoteMoveEvent extends SingleNoteEvent{
	
	private Point from;
	private Point to;

	public NoteMoveEvent(AutomatonEditorPanel panel, Note n, Point old){
		super(panel, n);
		from = old;
		to = n.getLocation();
		
		from.x = (int) Math.max(from.x, 0);
		from.y = (int) Math.max(from.y, 0);
		to.x = (int) Math.max(to.x, 0);
		to.y = (int) Math.max(to.y, 0);
	}

	@Override
	public boolean undo() {
		return move(from) && setText();
	}

	@Override
	public boolean redo() {
		return move(to) && super.redo();
	}

	@Override
	public String getName() {
		return "Move note";
	}
	
	private boolean move(Point dest){
		getNote().setLocation(dest);
		getPanel().clearSelection();
		return true;
	}

}
