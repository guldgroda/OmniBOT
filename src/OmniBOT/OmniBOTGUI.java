package OmniBOT;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class OmniBOTGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String window_name;
	public My_Panel camera_panel;
	public ButtonPanel buttonPanel;
	public OmniBOTGUI() {
		// Create Interface
		super();
		window_name = "OmniBOT - Computer Vision Controlled Robot";
		this.setName(window_name);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 400);
		camera_panel = new My_Panel();
		buttonPanel = new ButtonPanel();
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.add(camera_panel);
		this.add(buttonPanel);
		this.setVisible(true);
	}
	
}
