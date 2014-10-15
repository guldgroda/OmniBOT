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
	public Point blueCenter,greenCenter,redCenter;
	public float redSize, blueSize,greenSize;
	int red,blue,green;
	public Mat workingImage;
	public Mat processedImage;
	public VideoCapture capture;

	// Create a constructor method
	public Processor() {
		
		//initialize variables
		workingImage = new Mat();
		processedImage = new Mat();
		capture = new VideoCapture(0);
		red = 0;
		blue=1;
		green=2;
		setToZero();
		
	}
	
	private void setToZero(){
		redCenter = new Point();
		redSize = 0;
		blueCenter = new Point();
		blueSize = 0;
		greenCenter = new Point();
		greenSize = 0;
	}
	
	public Size getImageSize(){
		return new Size(workingImage.width(),workingImage.height());
	}
	
	public void readWebcam(){
		capture.read(workingImage);
		Size size = new Size(getImageSize().width / 2,
				getImageSize().height / 2);
		Imgproc.resize(workingImage, workingImage, size);
		processedImage=workingImage;
		setToZero();
	}

	public void drawLineBlueRed(){
		Core.line(processedImage, redCenter, blueCenter, new Scalar(0));
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
		Scalar hsv_min = new Scalar(110, 50, 50, 0);
		Scalar hsv_max = new Scalar(130, 255, 255, 0);
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
		Scalar hsv_min = new Scalar(40, 50, 50, 0);
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
		Scalar hsv_min = new Scalar(0, 50, 50, 0);
		Scalar hsv_max = new Scalar(10, 255, 255, 0);
		Scalar hsv_min2 = new Scalar(170, 50, 50, 0);
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
			}
		}
	}

	//detect red
	public void detectRedCircle(){
		detectRedCircle(workingImage);
	}
	
	private void detectRedCircle(Mat inputFrame) {
		detectCircles(redToBinary(inputFrame), red);
	}
	
	//detect green
	public void detectGreenCircle(){
		detectGreenCircle(workingImage);
	}
	
	private void detectGreenCircle(Mat inputFrame) {
		detectCircles(greenToBinary(inputFrame), green);
	}
	
	//Detect blue
	public void detectBlueCircle(){
		detectBlueCircle(workingImage);
	}
	
	private void detectBlueCircle(Mat inputFrame) {
		detectCircles(blueToBinary(inputFrame), blue);
	}
	
	//Paint red
	public void paintRedCircle(){
		processedImage=paintRedCircle(processedImage);
	}
	private Mat paintRedCircle(Mat inputFrame) {
		return paintCircle(inputFrame, redCenter, redSize, red);
	}
	
	//Paint blue
	public void paintBlueCircle(){
		processedImage=paintBlueCircle(processedImage);
	}
	private Mat paintBlueCircle(Mat inputFrame) {
		return paintCircle(inputFrame, blueCenter, blueSize, blue);
	}
	
	//Paint green
	public void paintGreenCircle(){
		processedImage=paintGreenCircle(processedImage);
	}
	private Mat paintGreenCircle(Mat inputFrame) {
		return paintCircle(inputFrame, greenCenter, greenSize, green);
	}
	
	//General paint-code
	private Mat paintCircle(Mat inputFrame, Point center, float radius,int color) {
		Scalar scalar;
		switch (color) {
		case 0:
			scalar = new Scalar(0, 0, 255);
			break;
		case 1:
			scalar = new Scalar(255,0,0);
			break;
		case 2:
			scalar = new Scalar(0,255,0);
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
	
	public void putSizeAndFPS(int fps){
		processedImage = putSizeAndFPS(processedImage,(int)getImageSize().width,(int)getImageSize().height, fps);
	}
	private Mat putSizeAndFPS(Mat inputFrame,int w, int h, int fps){
		Mat returnFrame = inputFrame;
		Core.putText(returnFrame, w+"x"+h+", "+fps+" fps", new Point(1,21), 2, 0.8, new Scalar(200,200,200,255));
		return returnFrame;
	}

}