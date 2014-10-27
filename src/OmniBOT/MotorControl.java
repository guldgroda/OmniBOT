package OmniBOT;

import java.rmi.RemoteException;

import org.opencv.core.Point;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;

public class MotorControl {
	static RMIRegulatedMotor a;
	static RMIRegulatedMotor b;
	static RMIRegulatedMotor c;
	RemoteEV3 ev3;
	double[][] transformation1;
	double maxSpeed;
	double rotationSpeed;
	
	
	public MotorControl(String ip) {
		transformation1 = new double[][] { { 0, 27.7778, -1.2639 }, { 24.0563, -13.8889, -1.2639 }, { -24.0563, -13.8889, -1.2639 } };
		ev3=null;
		try {
			ev3 = new RemoteEV3(ip);
		} catch (Exception e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Sound.beep();
		Sound.beep();
		TextLCD lcd = ev3.getTextLCD();
		ev3.setDefault();
		lcd.clear();
		lcd.drawString("Connected", 0, 1);
		a = ev3.createRegulatedMotor("A", 'N');
		b = ev3.createRegulatedMotor("B", 'N');
		c = ev3.createRegulatedMotor("C", 'N');
		
		maxSpeed = 0.15; // i m/s
		rotationSpeed=0;
	}
	
	public void setSpeedsRobot(double[] cartSpeeds){
		try {
			LCD.clear();
			LCD.drawString("Set speed", 0, 1);
			System.out.println("cartSpeeds: "+cartSpeeds[0]+", "+cartSpeeds[1]+", "+cartSpeeds[2]);
			double[] motorSpeeds = transformCartSpeedstoMotorSpeeds(cartSpeeds, transformation1);
			System.out.println("motorSpeeds: "+motorSpeeds[0]+", "+motorSpeeds[1]+", "+motorSpeeds[2]);
			setMotorSpeeds(motorSpeeds);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void closeMotorPorts(){
		try {
			a.stop(true);
			b.stop(true);
			c.stop(true);

			LCD.clear();
			LCD.drawString("Closing ports", 0, 1);
			a.close();
			b.close();
			c.close();
			LCD.clear();
			LCD.drawString("Ports Closed", 0, 1);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void setMotorSpeeds(double[] motorSpeeds) throws RemoteException {
		int aSpeed = (int) Math.round(motorSpeeds[0]);
		int bSpeed = (int) Math.round(motorSpeeds[1]);
		int cSpeed = (int) Math.round(motorSpeeds[2]);

		a.setSpeed(aSpeed);
		b.setSpeed(bSpeed);
		c.setSpeed(cSpeed);

		if (aSpeed >= 0)
			a.forward();
		else
			a.backward();
		if (bSpeed >= 0)
			b.forward();
		else
			b.backward();
		if (cSpeed >= 0)
			c.forward();
		else
			c.backward();
	}

	private static double[] transformCartSpeedstoMotorSpeeds(
			double[] cartSpeeds, double[][] transformation) {
		double[] motorSpeeds = new double[3];
		for (int i = 0; i < 3; i++) {
			motorSpeeds[i] = 0;
		}
		for (int i = 0; i < 3; i++) {
			motorSpeeds[0] += transformation[0][i] * cartSpeeds[i];
			motorSpeeds[1] += transformation[1][i] * cartSpeeds[i];
			motorSpeeds[2] += transformation[2][i] * cartSpeeds[i];
		}
		for (int i = 0; i < 3; i++) {
			motorSpeeds[i] = (double) Math.toDegrees(motorSpeeds[i]);
		}
		return motorSpeeds;
	}
	
	public double[] deltaToCartSpeeds(Point delta, double robotAngle){
		//We only want the robot to move with maxSpeed, so we calculate a speedFactor to multiply the delta components with to get a magnitude maxSpeed.
		double speedFactor = maxSpeed/Math.sqrt((delta.x*delta.x+delta.y*delta.y));
		//Debug print
		System.out.println("Speed factor= "+speedFactor);
		//Save our desired speeds in video cartesian coordinates
		double[] videoSpeeds = {delta.x*speedFactor,delta.y*speedFactor,rotationSpeed};
		
		//Define transformation matrix from video coordinates to robot coordinates
		double[][] transformation2={{Math.cos(robotAngle),-Math.sin(robotAngle),0},{-Math.sin(robotAngle),-Math.cos(robotAngle),0},{0,0,1}};
		
		double[] returnSpeeds = {0,0,0};
		//Do the matrix multiplication and save returnSpeeds.
		for (int i = 0; i < 3; i++) {
			returnSpeeds[0] += transformation2[0][i] * videoSpeeds[i];
			returnSpeeds[1] += transformation2[1][i] * videoSpeeds[i];
			returnSpeeds[2] += transformation2[2][i] * videoSpeeds[i];
		}
		return returnSpeeds;
	}
	
	public Point getDelta(Point a, Point b){
		double dx = b.x-a.x;
		double dy = b.y-a.y;
		System.out.println("Delta: "+dx+", "+dy);
		return new Point(dx,dy);
		
	}

}