package OmniBOT;

import lejos.utility.Delay;

import org.opencv.core.Point;

public class TestMotorControl {

	public static void main(String[] args) {
		MotorControl mc = new MotorControl("10.0.1.1");
		Point a = new Point(12,12);
		Point b = new Point(12,112);
		
		while(true){
			Point delta = mc.getDelta(a, b);
		}
		//double[] cartSpeeds = mc.deltaToCartSpeeds(delta);
		//mc.setSpeedsRobot(cartSpeeds);
		
		//Delay.msDelay(5000);
		//mc.closeMotorPorts();
	}

}
