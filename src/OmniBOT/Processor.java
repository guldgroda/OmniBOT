package OmniBOT;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class Processor {
	public Point blueCenter, greenCenter, redCenter, targetCenter, robotCenter,
			videoCenter;
	public float redSize, blueSize, greenSize;
	int red, blue, green, robot, nbrNoFindRed, nbrNoFindGreen, nbrNoFindBlue,
			nbrNoFindLimit;
	public Mat workingImage;
	public Mat processedImage;
	public VideoCapture capture;
	double robotAngle;

	// Create a constructor method
	public Processor() {

		// initialize variables
		workingImage = new Mat();
		processedImage = new Mat();
		capture = new VideoCapture(0); // Choose camera (0 = default)
		red = 0;
		blue = 1;
		green = 2;
		robot = 3;
		nbrNoFindRed = 0;
		nbrNoFindGreen = 0;
		nbrNoFindBlue = 0;
		nbrNoFindLimit = 5;
		setToZero();

	}

	public void detectTarget() {
		detectBlueCircle();
		targetCenter = blueCenter;
	}

	public void detectRobot() {
		detectRedCircle();
		detectGreenCircle();
		robotCenter = getRobotCenter(greenCenter, redCenter);
	}

	public Point getRobotCenter(Point a, Point b) {
		if (!(a.x == 0 && a.y == 0) || (b.x == 0 && b.y == 0)) {
			return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
		} else {
			return new Point(0, 0);
		}
	}

	public void saveRobotAngle() {
		if (nbrNoFindGreen == 0 && nbrNoFindRed == 0) {
			if (!(greenCenter.x == 0 && greenCenter.y == 0 || robotCenter.x == 0
					&& robotCenter.y == 0)) {
				robotAngle = getRobotAngle(greenCenter, robotCenter);
			}
		}
	}

	public double getRobotAngle(Point greenCenter, Point robotCenter) {
		// First quadrant
		double robotAngle;
		if (greenCenter.x > robotCenter.x && greenCenter.y < robotCenter.y) {
			double angle = Math.atan2(robotCenter.y - greenCenter.y,
					greenCenter.x - robotCenter.x);
			robotAngle = (3 * Math.PI) / 2 + angle;
		}
		// Second quadrant
		else if (greenCenter.x < robotCenter.x && greenCenter.y < robotCenter.y) {
			double angle = Math.atan2(robotCenter.x - greenCenter.x,
					robotCenter.y - greenCenter.y);
			robotAngle = angle;
		}
		// third
		else if (greenCenter.x < robotCenter.x && greenCenter.y > robotCenter.y) {
			double angle = Math.atan2(greenCenter.y - robotCenter.y,
					robotCenter.x - greenCenter.x);
			robotAngle = angle + Math.PI / 2;
		}
		// forth
		else {
			double angle = Math.atan2(greenCenter.x - robotCenter.x,
					greenCenter.y - robotCenter.y);
			robotAngle = Math.PI + angle;
		}
		return robotAngle;
	}

	private void setToZero() {
		redCenter = new Point();
		redSize = 0;
		blueCenter = new Point();
		blueSize = 0;
		greenCenter = new Point();
		greenSize = 0;
		targetCenter = new Point();
		robotCenter = new Point();
		videoCenter = new Point();
		robotAngle = 0;
	}

	// Get the current ImageSize
	public Size getImageSize() {
		return new Size(workingImage.width(), workingImage.height());
	}

	// Resize this image to wantedWidth??? (wantedWidth is the defining length)
	public void readWebcam() {
		capture.read(workingImage);
		Size size = getImageSize();
		double wantedWidth = 1200;
		double scaleFactor = wantedWidth / size.width;
		size = new Size(size.width * scaleFactor, size.height * scaleFactor);

		Imgproc.resize(workingImage, workingImage, size);
		processedImage = workingImage;
		videoCenter = new Point(workingImage.width() / 2,
				workingImage.height() / 2);
	}

	public void drawLine(Point a, Point b) {
		//if (!((a.x == 0 && a.y == 0) || (b.x == 0 && b.y == 0))) {
			Core.line(processedImage, a, b, new Scalar(0), 4);
		//}
	}

	private Mat blueToBinary(Mat inputframe) {
		// Setting variables and constants
		Mat hsv_image = new Mat();
		Mat returnFrame = new Mat();
		Mat array255 = new Mat(inputframe.height(), inputframe.width(),
				CvType.CV_8UC1);
		array255.setTo(new Scalar(255));
		Mat distance = new Mat(inputframe.height(), inputframe.width(),
				CvType.CV_8UC1);
		List<Mat> lhsv = new ArrayList<Mat>(3);
		inputframe.copyTo(hsv_image);
		inputframe.copyTo(returnFrame);
		Imgproc.cvtColor(inputframe, hsv_image, Imgproc.COLOR_BGR2HSV);

		// Threshold values
		Scalar hsv_min = new Scalar(100, 100, 100, 0);
		Scalar hsv_max = new Scalar(150, 255, 255, 0);
		Mat thresholded = new Mat();
		Mat thresholded2 = new Mat();

		// Actual processing
		Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);
		Core.split(hsv_image, lhsv); // We get 3 2D one channel Mats
		Mat S = lhsv.get(1);
		Mat V = lhsv.get(2);
		Core.subtract(array255, S, S);
		Core.subtract(array255, V, V);
		S.convertTo(S, CvType.CV_32F);
		V.convertTo(V, CvType.CV_32F);
		Core.magnitude(S, V, distance);
		Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), thresholded2);
		Core.bitwise_and(thresholded, thresholded2, thresholded);
		Imgproc.GaussianBlur(thresholded, returnFrame, new Size(9, 9), 0, 0);

		return returnFrame;
	}

	private Mat greenToBinary(Mat inputframe) {
		// Setting variables and constants
		Mat hsv_image = new Mat();
		Mat returnFrame = new Mat();
		Mat array255 = new Mat(inputframe.height(), inputframe.width(),
				CvType.CV_8UC1);
		array255.setTo(new Scalar(255));
		Mat distance = new Mat(inputframe.height(), inputframe.width(),
				CvType.CV_8UC1);
		List<Mat> lhsv = new ArrayList<Mat>(3);
		inputframe.copyTo(hsv_image);
		inputframe.copyTo(returnFrame);
		Imgproc.cvtColor(inputframe, hsv_image, Imgproc.COLOR_BGR2HSV);

		// Threshold values
		Scalar hsv_min = new Scalar(40, 100, 100, 0);
		Scalar hsv_max = new Scalar(80, 255, 255, 0);
		Mat thresholded = new Mat();
		Mat thresholded2 = new Mat();

		// Actual processing
		Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);
		// We get 3 2D one channel Mats
		Core.split(hsv_image, lhsv);
		Mat S = lhsv.get(1);
		Mat V = lhsv.get(2);
		Core.subtract(array255, S, S);
		Core.subtract(array255, V, V);
		S.convertTo(S, CvType.CV_32F);
		V.convertTo(V, CvType.CV_32F);
		Core.magnitude(S, V, distance);
		Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), thresholded2);
		Core.bitwise_and(thresholded, thresholded2, thresholded);
		Imgproc.GaussianBlur(thresholded, returnFrame, new Size(9, 9), 0, 0);

		return returnFrame;
	}

	private Mat redToBinary(Mat inputframe) {
		// Setting variables and constants
		Mat hsv_image = new Mat();
		Mat returnFrame = new Mat();
		Mat array255 = new Mat(inputframe.height(), inputframe.width(),
				CvType.CV_8UC1);
		array255.setTo(new Scalar(255));
		Mat distance = new Mat(inputframe.height(), inputframe.width(),
				CvType.CV_8UC1);
		List<Mat> lhsv = new ArrayList<Mat>(3);
		inputframe.copyTo(hsv_image);
		inputframe.copyTo(returnFrame);
		Imgproc.cvtColor(inputframe, hsv_image, Imgproc.COLOR_BGR2HSV);

		// Threshold values
		Scalar hsv_min = new Scalar(0, 100, 100, 0);
		Scalar hsv_max = new Scalar(15, 255, 255, 0);
		Scalar hsv_min2 = new Scalar(165, 100, 100, 0);
		Scalar hsv_max2 = new Scalar(180, 255, 255, 0);
		Mat thresholded = new Mat();
		Mat thresholded2 = new Mat();

		// Actual processing
		Core.inRange(hsv_image, hsv_min, hsv_max, thresholded);
		Core.inRange(hsv_image, hsv_min2, hsv_max2, thresholded2);
		Core.bitwise_or(thresholded, thresholded2, thresholded);
		Core.split(hsv_image, lhsv); // We get 3 2D one channel Mats
		Mat S = lhsv.get(1);
		Mat V = lhsv.get(2);
		Core.subtract(array255, S, S);
		Core.subtract(array255, V, V);
		S.convertTo(S, CvType.CV_32F);
		V.convertTo(V, CvType.CV_32F);
		Core.magnitude(S, V, distance);
		Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), thresholded2);
		Core.bitwise_and(thresholded, thresholded2, thresholded);
		Imgproc.GaussianBlur(thresholded, returnFrame, new Size(9, 9), 0, 0);

		return returnFrame;
	}

	private void detectCircles(Mat inputFrame, int color) {
		Mat circles = new Mat();
		Imgproc.HoughCircles(inputFrame, circles, Imgproc.CV_HOUGH_GRADIENT, 2,
				inputFrame.height() / 4, 500, 50, 0, 0);

		int rows = circles.rows();
		int elemSize = (int) circles.elemSize(); // Returns 12 (3 * 4bytes in a
		// float)
		float[] data = new float[rows * elemSize / 4];
		if (data.length > 0) {
			circles.get(0, 0, data); // Points to the first element and reads
			// the whole thing
			// into data2
			for (int i = 0; i < data.length; i = i + 3) {
				Point center = new Point(data[i], data[i + 1]);
				float radius = data[i + 2];
				if (center.x != 0 && center.y != 0) {
					switch (color) {
					case 0:
						redCenter = center;
						redSize = radius;
						break;
					case 1:
						blueCenter = center;
						blueSize = radius;
						break;
					case 2:
						greenCenter = center;
						greenSize = radius;
						break;
					}
				} else {
					switch (color) {
					case 0:
						nbrNoFindRed++;
						if (nbrNoFindRed > nbrNoFindLimit) {
							redCenter = new Point(0, 0);
							redSize = 0;
						}
						break;
					case 1:
						nbrNoFindBlue++;
						if (nbrNoFindBlue > nbrNoFindLimit) {
							blueCenter = new Point(0, 0);
							blueSize = 0;
						}
						break;
					case 2:
						nbrNoFindGreen++;
						if (nbrNoFindGreen > nbrNoFindLimit) {
							greenCenter = new Point(0, 0);
							greenSize = 0;
						}
						break;
					}
				}

			}
		}
	}

	// detect red
	public void detectRedCircle() {
		detectRedCircle(workingImage);
	}

	private void detectRedCircle(Mat inputFrame) {
		detectCircles(redToBinary(inputFrame), red);
	}

	// detect green
	public void detectGreenCircle() {
		detectGreenCircle(workingImage);
	}

	private void detectGreenCircle(Mat inputFrame) {
		detectCircles(greenToBinary(inputFrame), green);
	}

	// Detect blue
	public void detectBlueCircle() {
		detectBlueCircle(workingImage);
	}

	private void detectBlueCircle(Mat inputFrame) {
		detectCircles(blueToBinary(inputFrame), blue);
	}

	// Paint red
	public void paintRedCircle() {
		processedImage = paintRedCircle(processedImage);
	}

	private Mat paintRedCircle(Mat inputFrame) {
		return paintCircle(inputFrame, redCenter, redSize, red);
	}

	// Paint blue
	public void paintBlueCircle() {
		processedImage = paintBlueCircle(processedImage);
	}

	private Mat paintBlueCircle(Mat inputFrame) {
		return paintCircle(inputFrame, blueCenter, blueSize, blue);
	}

	// Paint green
	public void paintGreenCircle() {
		processedImage = paintGreenCircle(processedImage);
	}

	private Mat paintGreenCircle(Mat inputFrame) {
		return paintCircle(inputFrame, greenCenter, greenSize, green);
	}

	public void paintRobotCircle() {
		processedImage = paintRobotCircle(processedImage);
	}

	private Mat paintRobotCircle(Mat inputFrame) {
		return paintCircle(inputFrame, robotCenter, getRobotSize(), robot);
	}

	private float getRobotSize() {
		return (redSize + greenSize) * 2;
	}

	// General paint-code
	private Mat paintCircle(Mat inputFrame, Point center, float radius,
			int color) {
		Scalar scalar;
		switch (color) {
		case 0:
			scalar = new Scalar(0, 0, 255);
			break;
		case 1:
			scalar = new Scalar(255, 0, 0);
			break;
		case 2:
			scalar = new Scalar(0, 255, 0);
			break;
		default:
			scalar = new Scalar(0, 0, 0); // black
			break;

		}

		Mat returnFrame = inputFrame;
		Core.ellipse(returnFrame, center, new Size((double) radius,
				(double) radius), 0, 0, 360, scalar, 4, 8, 0);
		return returnFrame;
	}

	public void putSizeAndFPS(int fps) {
		processedImage = putSizeAndFPS(processedImage,
				(int) getImageSize().width, (int) getImageSize().height, fps);
	}

	private Mat putSizeAndFPS(Mat inputFrame, int w, int h, int fps) {
		Mat returnFrame = inputFrame;
		Core.putText(returnFrame, w + "x" + h + ", " + fps + " Hz", new Point(
				1, 21), 2, 0.8, new Scalar(200, 200, 200, 255));
		return returnFrame;
	}

}