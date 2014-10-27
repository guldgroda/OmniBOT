package OmniBOT;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ButtonPanel extends JPanel implements ActionListener {
	public boolean goToBluePressed,stopPressed,showInfoPressed,rotatePressed;
	
	public ButtonPanel(){
		super();
		//Set layout
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		//initialize 
		goToBluePressed = false;
		stopPressed = false;
		showInfoPressed = false;
		rotatePressed = false;
		
		//Create buttons
		final JButton goToBlue = new JButton("Go to target");
		final JButton showInfo = new JButton("Show Information");
		final JButton rotation = new JButton("Rotate");
		final JButton stop = new JButton("Stop and close");
		
		//add buttons to panel
		this.add(showInfo);
		this.add(goToBlue);
		this.add(rotation);
		this.add(stop);
		
		//Add actionlisteners
		rotation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!rotatePressed){
            		rotation.setText("Stop rotating");
            	} else {
            		rotation.setText("Rotate");
            	}
            	rotatePressed=!rotatePressed;
			}
        });
		showInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!showInfoPressed){
            		showInfo.setText("Remove information");
            	} else {
            		showInfo.setText("Show information");
            	}
            	showInfoPressed=!showInfoPressed;
			}
        });
		goToBlue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!goToBluePressed){
            		goToBlue.setText("Go back home");
            	} else {
            		goToBlue.setText("Go to target");
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
