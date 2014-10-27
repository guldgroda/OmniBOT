package OmniBOT;

import java.rmi.RemoteException;

import org.opencv.core.Core;
import org.opencv.core.Point;

public class OmniBOTControl {
	public static void main(String[] args) {
		// Load the native openCV library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Create image processor
		Processor my_processor = new Processor();

		// Create Interface
		OmniBOTGUI gui = new OmniBOTGUI();

		// Create MotorControl
		// *BLUETOOTH/*
		MotorControl motorControl = new MotorControl("10.0.1.1");

		// *WIFI/*
		// MotorControl motorControl = new MotorControl("192.168.43.78");

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
					// detect robot
					my_processor.detectRobot();
					// detect target
					my_processor.detectTarget();

					// Debug
					System.out.println("Target: " + my_processor.targetCenter.x
							+ ", " + my_processor.targetCenter.y);
					System.out.println("RobotCenter: "
							+ my_processor.robotCenter.x + ", "
							+ my_processor.robotCenter.y);

					// paint circles & line to processedImage
					my_processor.paintRedCircle();
					my_processor.paintBlueCircle();
					my_processor.paintGreenCircle();
					my_processor.paintRobotCircle();

					if (gui.buttonPanel.showInfoPressed) {
						// Print size and fps information on picture
						my_processor.putSizeAndFPS(fps);
					}

				} else {
					// if no image was recorded, show error message and start
					// over
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
				double distanceFromTarget = 50;
				if (gui.buttonPanel.goToBluePressed) {
					// Set speeds to go to blue target, assuming red target is
					// robot.
					// Get delta between red and blue

					// If either robot or target has coordinates 0,0 they have
					// not been properly detected.
					// If either robot or target is 0,0 we don't want anything
					// to happen

					if (!((my_processor.robotCenter.x == 0 && my_processor.robotCenter.y == 0) || (my_processor.targetCenter.x == 0 && my_processor.targetCenter.y == 0))) {
						if (Math.sqrt(Math.pow(my_processor.robotCenter.x
								- my_processor.targetCenter.x, 2)
								+ Math.pow(my_processor.robotCenter.y
										- my_processor.targetCenter.y, 2)) > distanceFromTarget) {
							// Draw line from robotCenter to target
							my_processor.drawLine(my_processor.robotCenter,
									my_processor.targetCenter);
							Point delta = motorControl.getDelta(
									my_processor.robotCenter,
									my_processor.targetCenter);
							System.out.println("Delta: " + delta.x + ", "
									+ delta.y);
							my_processor.saveRobotAngle();
							double robotAngle = my_processor.robotAngle;
							System.out.println("Robot Angle: " + robotAngle);

							motorControl.setSpeedsRobot(motorControl
									.deltaToCartSpeeds(delta, robotAngle));
						} else {
							try {
								motorControl.a.stop(true);
								motorControl.b.stop(true);
								motorControl.c.stop(true);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} else {

					if (Math.sqrt(Math.pow(my_processor.robotCenter.x
							- my_processor.videoCenter.x, 2)
							+ Math.pow(my_processor.robotCenter.y
									- my_processor.videoCenter.y, 2)) > distanceFromTarget) {
						Point delta = motorControl.getDelta(
								my_processor.robotCenter,
								my_processor.videoCenter);
						my_processor.saveRobotAngle();
						double robotAngle = my_processor.robotAngle;
						motorControl.setSpeedsRobot(motorControl
								.deltaToCartSpeeds(delta, robotAngle));
					} else {
						try {
							motorControl.a.stop(true);
							motorControl.b.stop(true);
							motorControl.c.stop(true);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				if (gui.buttonPanel.stopPressed) {
					try {
						motorControl.a.stop(true);
						motorControl.b.stop(true);
						motorControl.c.stop(true);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					motorControl.closeMotorPorts();
					System.exit(0);
				}

				if (gui.buttonPanel.rotatePressed) {
					if (motorControl.rotationSpeed == 0) {
						motorControl.rotationSpeed = Math.PI / 4;
					}
				} else {
					if (motorControl.rotationSpeed != 0) {
						motorControl.rotationSpeed = 0;
					}
				}
				// Display the image
				gui.camera_panel
						.MatToBufferedImage(my_processor.processedImage);
				gui.camera_panel.repaint();

				// lets get endTime, then calculate elapsedTime, which can
				// then be used to get an approximate of the fps
				// This should be LAST in the loop
				long endTime = System.nanoTime();
				long elapsedTime = (endTime - startTime) / 1000000;
				fps = (int) (1000 / elapsedTime);

			}

		}

		return;
	}
}