package com.tziegler.keyboard.datacollector;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataMotionEvents {
	
	public String username;
	
	public static class DataMotionEvent {
		public DataMotionEvent() {
			
		}
		DataMotionEvent(String s, int d) {
			name = s; 
			duration = d; 
		}
		public void setDuration(int d) {
			duration = d; 
		}
		public int getDuration() {
			return duration;
		}
		public void accumulateDuration(int d) {
			cumuDuration += d; 
		}
		public void incTimesPressed() {
			timesPressed++; 
		}
		public String name; 
		private int duration; 
		int timesPressed;
		int cumuDuration; 
		
		public void averageDuration() {
			duration = cumuDuration / timesPressed; 
		}
	}
	
	Map<String, DataMotionEvent> events = new LinkedHashMap<String, DataMotionEvent>(); 
	
	public DataMotionEvents() {
		 events.put("TAP", new DataMotionEvent("TAP", 9999));
		 events.put("HOP_TAP", new DataMotionEvent("HOP_TAP", 9999));
		 events.put("LEAP_TAP", new DataMotionEvent("LEAP_TAP", 9999));
		 events.put("NEAR_DTAP", new DataMotionEvent("NEAR_DTAP", 9999));
		 events.put("FAR_DTAP", new DataMotionEvent("FAR_DTAP", 9999));
		 events.put("LEAP_DTAP", new DataMotionEvent("LEAP_DTAP", 9999));
		 events.put("SIDE_TAP", new DataMotionEvent("SIDE_TAP", 9999));
		 events.put("PINKY_TAP", new DataMotionEvent("PINKY_TAP", 9999));
	}
	
   public Map<String, DataMotionEvent> getEventsMap() {
        return events;
    }
   
   public void setEventsMap(Map<String, DataMotionEvent> m) {
	   events = m;
   }
   
   public String getUserName() {
	   return username; 
   }
   
   public void setUserName(String n) {
	   username = n; 
   }
}
