package OmniBOT;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel implements ActionListener {
	public boolean goToBluePressed,stopPressed;
	public ButtonPanel(){
		super();
		//Set layout
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		//initialize 
		goToBluePressed = false;
		stopPressed = false;
		
		//Create buttons
		final JButton goToBlue = new JButton("Go to blue");
		final JButton calibrate = new JButton("Calibrate compass");
		final JButton stop = new JButton("Stop");
		
		//add buttons to panel
		this.add(calibrate);
		this.add(goToBlue);
		this.add(stop);
		
		//Add actionlisteners
		goToBlue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!goToBluePressed){
            		goToBlue.setText("Stop going to blue");
            	} else {
            		goToBlue.setText("Go to blue");
            	}
            	goToBluePressed=!goToBluePressed;
			}
        }); 
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
            	stopPressed=!stopPressed;
			}
        }); 
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
