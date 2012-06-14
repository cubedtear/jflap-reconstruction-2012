package oldnewstuff.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

public interface JFLAPGUIResources {

	
	public static final Border DEF_PANEL_BORDER = BorderFactory.createLineBorder(Color.BLACK, 3);
	public final static Font DEF_PANEL_FONT = new Font("Dialog", 1, 15);
	public static final Color DEFAULT_SWING_BG = UIManager.getColor ( "Panel.background" ),
								  SPECIAL_SYMBOL = new Color(235, 235, 150),
								  BAR_SELECTED = new Color(140, 175, 255);
	
	//STATE CONSTANTS//
	/** The default radius of a state. */
	public static final int STATE_RADIUS = 20;
	
	//TRANSITION CONSTANTS//
	/** The stroke object that draws the lines. */
	public static Stroke TRANSITION_STROKE = new BasicStroke(2.4f);
	/** The color for the line. */
	public static java.awt.Color TRANSITION_COLOR = new Color(.5f, .5f, .5f, .5f);
	/** The angle for the arrow heads. */
	public static double ARROW_ANGLE = Math.PI / 10;
	/** The length of the arrow head edges. */
	public static double ARROW_LENGTH = 15;
	
//	public static final ColoredStroke HIGHLIGHT_STROKE = new ColoredStroke(6.0f, new Color(255, 0,0, 128));
	
//	public static final ColoredStroke THIN_HIGHLIGHT_STROKE = new ColoredStroke(3.0f, new Color(255, 0,0, 128));
	/** The number of characters to draw in each step. */
	public static final int CHARS_PER_STEP = 4;
	/**Radius of the transition control point**/
	public static final int CONTROL_POINT_RADIUS = 5;
	
	
	
	public static final double AUTO_BEND_HEIGHT = 16.123;
	
	/**
	 * Default font size of a grammar
	 */
	public static Font DEFAULT_FONT = new Font("TimesRoman", Font.PLAIN, 20);
	public static int DEFAULT_LABEL_PADDING = 2;
	/** The base color for states. */
	public static final Color DEFAULT_STATE_COLOR = new Color(255, 255, 150);
	public static final Color STATE_SELECTED_COLOR = new Color(100, 200, 200);
	public static final int EDITOR_CELL_WIDTH = 60;
//	public static final Note BASE_NOTE = new Note("Edit me");
	public static final int EDITOR_CELL_HEIGHT = 17;
	public static final String TAB_CHANGED = "TAB_CHANGED";
	public static final int CONFIGURATION_PADDING = 5;
	public static final int INITAL_LOOP_HEIGHT = 40;
}
