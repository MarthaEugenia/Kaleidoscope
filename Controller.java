package Kaleidoscope;

/**
 * Basic "Kaleidoscope" animation, making use of the
 * Model-View-Controller design pattern and the Timer 
 * and Observer/Observable classes.
 */

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;

import javax.swing.*;
import javax.swing.event.*;

/**
 * The Controller sets up the GUI and handles all the controls (buttons, menu
 * items, etc.)
 * 
 * @author David Matuszek
 * @author Theresa Breiner
 * @author Martha Trevino
 */
@SuppressWarnings("serial")
public class Controller extends JFrame {
	JPanel buttonPanel = new JPanel();
	JPanel slidePanel = new JPanel();
	JButton runButton = new JButton("Run");
	JButton stopButton = new JButton("Stop");
	JButton colorButton = new JButton("Change colors");
	JButton resetButton = new JButton("Reset");

	JRadioButton reflect4button = new JRadioButton("4 reflections");
	JRadioButton reflect8button = new JRadioButton("8 reflections");
	JRadioButton reflect12button = new JRadioButton("12 reflections");
	JRadioButton reflect16button = new JRadioButton("16 reflections");
	JRadioButton reflect24button = new JRadioButton("24 reflections");

	JCheckBox ballButton = new JCheckBox("Circles");
	JCheckBox rectangleButton = new JCheckBox("Rectangles");
	JCheckBox triangleButton = new JCheckBox("Triangles");
	JCheckBox roundRectButton = new JCheckBox("Round Rectangles");
	JCheckBox diamondButton = new JCheckBox("Diamonds");

	Timer timer;

	static final int SPEED_MIN = 0;
	static final int SPEED_MAX = 200;
	static final int SPEED_INIT = 100;
	JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, SPEED_MIN, SPEED_MAX,
			SPEED_INIT);

	/**
	 * The Model is the object that does all the computations. It is completely
	 * independent of the Controller and View objects.
	 */
	ArrayList<Model> modelsList = new ArrayList<Model>();
	final int MODELS_NUM = 60;

	/** The View object displays what is happening in the Model. */
	View view;

	/**
	 * Runs the kaleidoscope program.
	 * 
	 * @param args
	 *            Ignored.
	 */
	public static void main(String[] args) {
		Controller c = new Controller();
		c.init();
		c.display();
	}

	/**
	 * Sets up communication between the components.
	 */
	private void init() {

		for (int i = 0; i < MODELS_NUM; i++) {
			Model toAdd = new Model();
			if (i % 5 == 0)
				toAdd.setShapeType("ball");
			else if (i % 5 == 1)
				toAdd.setShapeType("rectangle");
			else if (i % 5 == 2)
				toAdd.setShapeType("triangle");
			else if (i % 5 == 3)
				toAdd.setShapeType("roundRect");
			else
				toAdd.setShapeType("diamond");

			modelsList.add(i, toAdd);
		}

		view = new View(modelsList); // The view needs to know what model to
										// look at

		for (int i = 0; i < MODELS_NUM; i++) {
			modelsList.get(i).addObserver(view);// The models need to give
												// permission to be observed
		}
	}

	/**
	 * Displays the GUI.
	 */
	private void display() {
		layOutComponents();
		attachListenersToComponents();
		setSize(600, 700);
		setVisible(true);
		setMinimumSize(new Dimension(500, 500));
		setTitle("Kaleidoscope");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Arranges the various components in the GUI.
	 */
	private void layOutComponents() {
		setLayout(new BorderLayout());
		this.add(BorderLayout.SOUTH, buttonPanel);
		buttonPanel.setLayout(new GridLayout(0, 5));

		// Buttons
		buttonPanel.add(runButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(colorButton);
		stopButton.setEnabled(false);

		// Ticks for speed slider
		speedSlider.setMajorTickSpacing(50);
		speedSlider.setMinorTickSpacing(10);
		speedSlider.setPaintTicks(true);

		// Create the labels for speed slider
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(SPEED_MIN, new JLabel("Slow"));
		labelTable.put(SPEED_MAX, new JLabel("Fast"));
		speedSlider.setLabelTable(labelTable);

		// Add speed slider
		speedSlider.setPaintLabels(true);
		buttonPanel.add(speedSlider);

		// Radio buttons for reflections
		ButtonGroup group = new ButtonGroup();
		group.add(reflect4button);
		group.add(reflect8button);
		group.add(reflect12button);
		group.add(reflect16button);
		group.add(reflect24button);

		buttonPanel.add(reflect4button);
		buttonPanel.add(reflect8button);
		buttonPanel.add(reflect12button);
		buttonPanel.add(reflect16button);
		buttonPanel.add(reflect24button);

		reflect8button.setSelected(true);

		// Check boxes for type of figures
		buttonPanel.add(ballButton);
		buttonPanel.add(rectangleButton);
		buttonPanel.add(triangleButton);
		buttonPanel.add(roundRectButton);
		buttonPanel.add(diamondButton);

		triangleButton.setSelected(true);

		// Add the view
		this.add(BorderLayout.CENTER, view);
	}

	/**
	 * Attaches listeners to the components, and schedules a Timer.
	 */
	private void attachListenersToComponents() {
		// The Run button tells the Model to start
		runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				runButton.setEnabled(false);
				stopButton.setEnabled(true);
				for (int i = 0; i < MODELS_NUM; i++) {
					modelsList.get(i).start();
				}

			}
		});
		// The Stop button tells the Model to pause
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				runButton.setEnabled(true);
				stopButton.setEnabled(false);
				for (int i = 0; i < MODELS_NUM; i++) {
					modelsList.get(i).pause();
				}
			}
		});
		// The Reset button restarts the JFrame
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				dispose();

				Controller c = new Controller();
				c.init();
				c.display();

			}
		});
		// The Color button sets new colors for the figures
		colorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				for (int i = 0; i < MODELS_NUM; i++) {
					modelsList.get(i).setColor();
				}
				view.repaint();
			}
		});
		// The Speed Slider changes the speed of the figures
		speedSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int percent = (int) source.getValue();
					for (int i = 0; i < MODELS_NUM; i++) {
						modelsList.get(i).setSpeed(percent);
					}
				}
			}
		});
		// The Reflect buttons change the number of reflections
		reflect4button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setReflections(4);
			}
		});
		reflect8button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setReflections(8);
			}
		});
		reflect12button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setReflections(12);
			}
		});
		reflect16button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setReflections(16);
			}
		});
		reflect24button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setReflections(24);
			}
		});
		// Check boxes to change the figures that will be displayed
		ballButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setBall(ballButton.isSelected());
			}
		});
		rectangleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setRectangle(rectangleButton.isSelected());
			}
		});
		triangleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setTriangle(triangleButton.isSelected());
			}
		});
		roundRectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setRoundRect(roundRectButton.isSelected());
			}
		});
		diamondButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.setDiamond(diamondButton.isSelected());
			}
		});
		// When the window is resized, the Model is given the new limits
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				for (int i = 0; i < MODELS_NUM; i++) {
					modelsList.get(i).setLimits(view.getWidth(),
							view.getHeight());
				}
			}
		});
	}
}