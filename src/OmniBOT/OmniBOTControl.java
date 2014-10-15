package OmniBOT;

import java.rmi.RemoteException;

import org.opencv.core.Core;
import org.opencv.core.Point;

public class OmniBOTControl {
	public static void main(String[] args) {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Create image processor
		Processor my_processor = new Processor();

		// Create Interface
		OmniBOTGUI gui = null;
		gui = new OmniBOTGUI();

		// Create MotorControl
		MotorControl motorControl = new MotorControl("10.0.1.1");
		// Create variables

		// If VideoCapture is opened, start looping
		if (my_processor.capture.isOpened()) {

			// Set frame size
			my_processor.readWebcam();
			gui.setSize((int) my_processor.getImageSize().width + 40,
					(int) my_processor.getImageSize().height + 65);

			// We want to know performance, so we try to extract the fps. First
			// initialize
			int fps = 0;
			while (true) {
				// lets get a startTime for fps calculation
				long startTime = System.nanoTime();

				// Read from webcam
				my_processor.readWebcam();

				// Make sure we get an image, else display error message
				if (!my_processor.workingImage.empty()) {
					// detectRedCircle and save to my_processor variables
					my_processor.detectRedCircle();
					// detectBlueCircle and save to my_processor variables
					my_processor.detectBlueCircle();

					/*
					 * //Debug Point red = my_processor.redCenter; Point blue =
					 * my_processor.blueCenter;
					 * System.out.println("Red: "+red.x+", "+red.y);
					 * System.out.println("Blue: "+blue.x+", "+blue.y);
					 */

					// paint circles to processedImage
					my_processor.paintRedCircle();
					my_processor.paintBlueCircle();
					// my_processor.paintGreenCircle();

					// Print size and fps information on picture
					my_processor.putSizeAndFPS(fps);

					// Display the image
					gui.camera_panel
							.MatToBufferedImage(my_processor.processedImage);
					gui.camera_panel.repaint();

					// lets get endTime, then calculate elapsedTime, which can
					// then be used to get an approximate of the fps
					long endTime = System.nanoTime();
					long elapsedTime = (endTime - startTime) / 1000000;
					fps = (int) (1000 / elapsedTime);

				} else {
					// if no image was recorded, show error message and start
					// over
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
				
				 if(gui.buttonPanel.goToBluePressed){ 
					 //Draw line from red to blue
					 my_processor.drawLineBlueRed();
					 
					 //Set speeds to go to blue target, assuming red target is robot.
					 //Get delta between red and blue
					 Point delta = motorControl.getDelta(my_processor.redCenter, my_processor.blueCenter);
					//System.out.println("Delta: "+delta.x+", "+delta.y);
					 motorControl.setSpeedsRobot(motorControl.deltaToCartSpeeds(delta));
				 } 
				 
				 if(gui.buttonPanel.stopPressed){ 
					 try {
						 motorControl.a.stop(true); 
						 motorControl.b.stop(true);
						 motorControl.c.stop(true); 
					 } catch (RemoteException e) { //
						 e.printStackTrace(); 
					 }
				 
				 motorControl.closeMotorPorts(); }
				 

			}
		}
		return;
	}
}