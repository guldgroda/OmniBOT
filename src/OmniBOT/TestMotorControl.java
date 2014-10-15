package OmniBOT;

import lejos.utility.Delay;

import org.opencv.core.Point;

public class TestMotorControl {

	public static void main(String[] args) {
		MotorControl mc = new MotorControl("10.0.1.1");
		
		//Try to draw a rectangle
		Point delta = new Point(100,0);
		double[] cartSpeeds = mc.deltaToCartSpeeds(delta);
		mc.setSpeedsRobot(cartSpeeds);
		Delay.msDelay(2000);
		
		delta = new Point(0,-100);
		cartSpeeds = mc.deltaToCartSpeeds(delta);
		mc.setSpeedsRobot(cartSpeeds);
		Delay.msDelay(2000);
		
		delta = new Point(-100,0);
		cartSpeeds = mc.deltaToCartSpeeds(delta);
		mc.setSpeedsRobot(cartSpeeds);
		Delay.msDelay(2000);
		
		delta = new Point(0,100);
		cartSpeeds = mc.deltaToCartSpeeds(delta);
		mc.setSpeedsRobot(cartSpeeds);
		Delay.msDelay(2000);
			
		mc.closeMotorPorts();
	}

}
