package com.tziegler.keyboard;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Fingers {

	int[] fingers; 
	int[] keyToFinger; 	// stores physical key index to finger mapping
	final int[] fingerHomes = { 12, 13, 14, 15, 18, 19, 20, 21, 22 }; 	// home rows of fingers
	int lastFinger = -1; 	// finger index who perfomed last motionEvent
	public static final boolean DEBUG = Keyboard.DEBUG;
	
	public static class MotionEvent {
		MotionEvent(String s, int d) {
			name = s; 
			duration = d; 
		}
		public void setDuration(int d) {
			duration = d; 
		}
		public int getDuration() {
			return duration;
		}
		String name; 
		private int duration; 
		int timesPressed;
		int cumuDuration; 
		
		void averageDuration() {
			duration = cumuDuration / timesPressed; 
		}
	}
	
	@XmlRootElement
	public static class MotionEvents {
		@XmlElement
		  public static MotionEvent TAP = new MotionEvent("TAP", 100); 
		  public static MotionEvent HOP_TAP = new MotionEvent("HOP_TAP", 200); 
		  public static MotionEvent LEAP_TAP = new MotionEvent("LEAP_TAP", 200); 
		  public static MotionEvent NEAR_DTAP = new MotionEvent("NEAR_DTAP", 300); 
		  public static MotionEvent FAR_DTAP = new MotionEvent("FAR_DTAP", 400); 
		  public static MotionEvent LEAP_DTAP = new MotionEvent("LEAP_DTAP", 600); 
		  public static MotionEvent SIDE_TAP = new MotionEvent("SIDE_TAP", 200); 
		  public static MotionEvent PINKY_TAP = new MotionEvent("PINKY_TAP", 500); 
		  public static MotionEvent SHIFT = new MotionEvent("SHIFT", 20); 
		  public static MotionEvent SPACE = new MotionEvent("ENTER", 20); 
		  public static MotionEvent ENTER = new MotionEvent("ENTER", 20); 
	}

	Fingers() {
		fingers = new int[8];
		for (int i = 0; i < 4; i++) 
			fingers[i] = 12 + i; 
		for (int i = 4; i < 8; i++) 
			fingers[i] = 14 + i; 
		
		keyToFinger = new int[33]; 
		int f = 0; // finger 0 - 8
		int c = 0; 
		for (int i = 0; i < 33; i++) {
			c = i < 12? i : i < 23? i - 12 : i - 23; // set c to column index 0 - 11 regardless of row
			
			// for new row, set finger 0 
			if (c == 0) 
				f = 0; 
			
			keyToFinger[i] = f; 
			f++; 
			if (c == 3 || c == 5 || c >= 9) 
				f--; 
		}
		
		/*for (int i = 0; i < 32; i++) {
			System.out.println("Physical key: " + i + " -> finger: " + keyToFinger[i]); 
		}*/
	}
	
	public int getMotionEventTime(int physicalKeyTo, boolean needShift, char c) {
		int t = 0;
		
		t = getMotionEvent(physicalKeyTo, c).duration;
		
		if (needShift) {
			t += MotionEvents.SHIFT.duration; 
			if (DEBUG) System.out.println("    shift: \t+" + MotionEvents.SHIFT + "ms");
		}
		
		return t;
	}
	
	// returns a motion event. char is used to see if it is enter or space
	MotionEvent getMotionEvent(int physicalKeyTo, char c) {
		
		int execFinger = keyToFinger[physicalKeyTo]; // finger that executes the motion
		
		// if this finger is a different finger than last event, return the previous finger to home
		if (lastFinger != -1 && lastFinger != execFinger) { 
			if (DEBUG) System.out.println("[" + lastFinger + "] return \t[" + fingers[lastFinger] + "] -> [" + fingerHomes[lastFinger] + "]");
			fingers[lastFinger] = fingerHomes[lastFinger];
		}
		
		lastFinger = execFinger; 
		
		if (c == ' ') {
			if (lastFinger != -1 && fingers[lastFinger] != fingerHomes[lastFinger]) {
				if (DEBUG) System.out.println("[" + lastFinger + "] return \t[" + fingers[lastFinger] + "] -> [" + fingerHomes[lastFinger] + "]");
				fingers[lastFinger] = fingerHomes[lastFinger];
				lastFinger = -1;
			}
			if (DEBUG) System.out.println("    space: \t+" + MotionEvents.SPACE + "ms");
			return MotionEvents.SPACE;
		}
		else if (c == '\n') {
			if (DEBUG) System.out.println("    enter: \t+" + MotionEvents.ENTER + "ms");
			return MotionEvents.ENTER; 
		}
		
		

		if (fingers[execFinger] == physicalKeyTo) {				// RM dir A->A
			// perform tap
			lastFinger = -1; 	// all fingers are in home position; 
			return tap(execFinger, physicalKeyTo); 
		}
		else if (fingers[execFinger] == physicalKeyTo - 11 ||  	// for hop-tap: dir H->D or D->H
				fingers[execFinger] == physicalKeyTo + 11) {	// for far-dtap: dir H->U or U->H

			// perform hop-tap or far-dtap
			if (fingers[execFinger] < 11 || physicalKeyTo < 11) {
				// perform far-dtap
				return nearDTap(execFinger, physicalKeyTo); 
			}
			else {
				// perform hop-tap
				return hopTap(execFinger, physicalKeyTo); 
			}
		}
		else if (fingers[execFinger] == physicalKeyTo - 12 -1 ||
				fingers[execFinger] == physicalKeyTo + 12 + 1) {
			return farDTap(execFinger, physicalKeyTo); 
		}		
		else if (fingers[execFinger] == physicalKeyTo + 12  ||	// for hop-tap: dir H->U or U->H
				fingers[execFinger] == physicalKeyTo - 12) {	// for far-dtap: dir H->D or D->H
			
			// perform hop-tap or far-dtap 
			if (fingers[execFinger] >= 23 || physicalKeyTo >= 23) {
				// perform far-dtap
				return farDTap(execFinger, physicalKeyTo);
			}
			else {
				// perform hop-tap
				return hopTap(execFinger, physicalKeyTo); 
			}
		}
		else if (fingers[execFinger] == physicalKeyTo + 23 ||	// RM dir D->U
				fingers[execFinger] == physicalKeyTo - 23 ||
				fingers[execFinger] == physicalKeyTo + 23 -1 ||
				fingers[execFinger] == physicalKeyTo - 23 + 1) {	// RM dir U->D
			// perform leap-tap
			return leapTap(execFinger, physicalKeyTo);
		}
		else if (fingers[execFinger] == physicalKeyTo + 1 ||
				fingers[execFinger] == physicalKeyTo - 1) {
			// perform side-tap
			return sideTap(execFinger, physicalKeyTo); 
		}
		else if (fingers[execFinger] == physicalKeyTo - 23 -1 || 
				fingers[execFinger] == physicalKeyTo + 23 + 1) {
			return leapDTap(execFinger, physicalKeyTo); 
		}
		else if (fingers[execFinger] == physicalKeyTo + 10 || 
				fingers[execFinger] == physicalKeyTo - 10) {
			return nearDTap(execFinger, physicalKeyTo); 
		}
		else {
			// perform pinky-tap;   either from P->] or ;->]
			//System.err.println("unknown motion event");
			//System.err.println("from: [" + fingers[execFinger] + " to " + physicalKeyTo);
			return pinkyTap(execFinger, physicalKeyTo);
		}
	}
	
	MotionEvent tap(int finger, int to) {
		if (DEBUG) System.out.println("[" + finger + "] tap \t[" + fingers[finger] + "] -> [" + to + "]"); 
		fingers[finger] = to; 
		return MotionEvents.TAP; 
	}
	
	MotionEvent farDTap(int finger, int to) {
		if (DEBUG) System.out.println("[" + finger + "] far-dtap \t[" + fingers[finger] + "] -> [" + to + "]");
		fingers[finger] = to; 
		return MotionEvents.FAR_DTAP;
	}
	
	MotionEvent hopTap(int finger, int to) {
		if (DEBUG) System.out.println("[" + finger + "] hop-tap \t[" + fingers[finger] + "] -> [" + to + "]");
		fingers[finger] = to; 
		return MotionEvents.HOP_TAP; 
	}
	
	MotionEvent leapTap(int finger, int to) {
		if (DEBUG) System.out.println("[" + finger + "] leap-tap \t[" + fingers[finger] + "] -> [" + to + "]");
		fingers[finger] = to; 
		return MotionEvents.LEAP_TAP;
	}
	
	MotionEvent nearDTap(int finger, int to) {
		if (DEBUG) System.out.println("[" + finger + "] near-dtap \t[" + fingers[finger] + "] -> [" + to + "]"); 
		fingers[finger] = to; 
		return MotionEvents.NEAR_DTAP;
	}
	
	MotionEvent sideTap(int finger, int to) {
		if (DEBUG) System.out.println("[" + finger + "] side-tap \t[" + fingers[finger] + "] -> [" + to + "]");
		fingers[finger] = to; 
		return MotionEvents.SIDE_TAP;
	}
	
	MotionEvent leapDTap(int finger, int to) {
		if (DEBUG) System.out.println("[" + finger + "] leap-dtap \t[" + fingers[finger] + "] -> [" + to + "]"); 
		fingers[finger] = to; 
		return MotionEvents.LEAP_DTAP;
	}
	
	MotionEvent pinkyTap(int finger, int to) {
		//System.out.println("finger idx: " + finger);
		if (DEBUG) System.out.println("[" + finger + "] pinky-tap \t[" + fingers[finger] + "] -> [" + to + "]"); 
		fingers[finger] = to; 
		return MotionEvents.PINKY_TAP;
	}
}







