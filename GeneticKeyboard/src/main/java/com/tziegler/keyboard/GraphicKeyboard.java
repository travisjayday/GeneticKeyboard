package com.tziegler.keyboard;

import java.awt.Dimension;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class GraphicKeyboard {
	
	static final int FONT = Core.FONT_HERSHEY_COMPLEX;
	static final int THICK = 2;

	static Point[] keyPos = {
			new Point(253, 211),	// 0
			new Point(386, 211),	// 1
			new Point(519, 211),	// 2
			new Point(652, 211),	// 3
			new Point(773, 211),	// 4
			new Point(906, 211),	// 5
			new Point(1039, 211),	// 6
			new Point(1172, 211),	// 7
			new Point(1305, 211),	// 8
			new Point(1438, 211),	// 9
			new Point(1571, 211),	// 10
			new Point(1705, 211),	// 11
			new Point(287, 344),	// 12
			new Point(420, 344),	// 13
			new Point(553, 344),	// 14
			new Point(674, 344),	// 15
			new Point(807, 344),	// 16
			new Point(940, 344),	// 17
			new Point(1073, 344),	// 18
			new Point(1206, 344),	// 19
			new Point(1339, 344),	// 20
			new Point(1472, 344),	// 21
			new Point(1605, 344),	// 22
			new Point(353, 477),	// 23
			new Point(486, 477),	// 24
			new Point(619, 477),	// 25
			new Point(740, 477),	// 26
			new Point(873, 477),	// 27
			new Point(1006, 477),	// 28
			new Point(1139, 477),	// 29
			new Point(1272, 477),	// 30
			new Point(1405, 477),	// 31
			new Point(1538, 477), 	// 32
	};

	private static int distance(Point p1, Point p2) {
		return (int)(Math.sqrt(Math.pow((double)(p1.x - p2.x), 2.0) + Math.pow((double)(p1.y - p2.y), 2.0))); 
	}
	
	// helper function to show motion event distances based on above points
	static void motionDurationBasedOnDistances() {
		Fingers.MotionEvents.TAP.setDuration(distance(keyPos[0], keyPos[0])); 			// q, q
		Fingers.MotionEvents.HOP_TAP.setDuration(distance(keyPos[0], keyPos[12])); 		// q, a
		Fingers.MotionEvents.LEAP_TAP.setDuration(distance(keyPos[0], keyPos[23])); 	// q, z
		Fingers.MotionEvents.NEAR_DTAP.setDuration(distance(keyPos[15], keyPos[4]));  	// f, t
		Fingers.MotionEvents.FAR_DTAP.setDuration(distance(keyPos[15], keyPos[27])); 	// f, b 
		Fingers.MotionEvents.LEAP_DTAP.setDuration(distance(keyPos[3], keyPos[27]));	// r, b 
		Fingers.MotionEvents.SIDE_TAP.setDuration(distance(keyPos[15], keyPos[16])); 	// f, g
		Fingers.MotionEvents.PINKY_TAP.setDuration(distance(keyPos[21], keyPos[11])); 	// ;, ]
		Fingers.MotionEvents.SHIFT.setDuration(0); 
		Fingers.MotionEvents.SPACE.setDuration(0); 
		Fingers.MotionEvents.ENTER.setDuration(0); 
		
		if (Keyboard.DEBUG) {
			System.out.println("Motion Event: \t Duration in ms");
			System.out.println("TAP: \t" + Fingers.MotionEvents.TAP.getDuration());
			System.out.println("HOP_TAP: \t" + Fingers.MotionEvents.HOP_TAP.getDuration());
			System.out.println("LEAP_TAP: \t" + Fingers.MotionEvents.LEAP_TAP.getDuration());
			System.out.println("NEAR_DTAP: \t" + Fingers.MotionEvents.NEAR_DTAP.getDuration());
			System.out.println("FAR_DTAP: \t" + Fingers.MotionEvents.FAR_DTAP.getDuration());
			System.out.println("LEAP_DTAP: \t" + Fingers.MotionEvents.LEAP_DTAP.getDuration());
			System.out.println("SIDE_TAP: \t" + Fingers.MotionEvents.SIDE_TAP.getDuration());
			System.out.println("PINKY_TAP: \t" + Fingers.MotionEvents.PINKY_TAP.getDuration());
			System.out.println("SHIFT: \t" + Fingers.MotionEvents.SHIFT.getDuration());
			System.out.println("SPACE: \t" + Fingers.MotionEvents.SPACE.getDuration());
			System.out.println("ENTER: \t" + Fingers.MotionEvents.ENTER.getDuration());
		}
	}
	
	static void showKeyboard(Key[] keys, String title, boolean newWin) {
		Mat img = Imgcodecs.imread("blank_keyboard.png"); 
		for (int i = 0; i < keyPos.length; i++) {

			if (keys[i].hasShift()) {
				Imgproc.putText(
						img, 
						"" + keys[i].getMainChar(), 
						new Point(keyPos[i].x, keyPos[i].y + 25), 
						Core.FONT_HERSHEY_DUPLEX, 
						1.0, 
						new Scalar(0, 0, 0), 
						2); 
				Imgproc.putText(
						img, 
						"" + keys[i].getShifted(), 
						new Point(keyPos[i].x, keyPos[i].y - 25), 
						FONT, 
						1.0, 
						new Scalar(0, 0, 0),
						THICK); 
			}
			else {
				Imgproc.putText(
						img, 
						"" + keys[i].getMainChar(), 
						keyPos[i], 
						FONT, 
						1.4, 
						new Scalar(0, 0, 0), 
						THICK); 
			}
		}
		Imshow.show(img, new Dimension(400, 900), title, false, newWin); 
	}

	static void showKeyboard(Key[] keys)  {
		showKeyboard(keys, "win", false); 
	}
}
