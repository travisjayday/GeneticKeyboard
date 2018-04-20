package com.tziegler.keyboard.graphics;

import java.awt.Dimension;
import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFrame;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import com.tziegler.keyboard.Fingers;
import com.tziegler.keyboard.Keyboard;
import com.tziegler.keyboard.datacollector.DataMotionEvents;
import com.tziegler.keyboard.datacollector.DataMotionEvents.DataMotionEvent;

public class MotionEventPlot extends JFrame {
	public MotionEventPlot(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public MotionEventPlot(DataMotionEvents events) {
		displayDataMotionEvents(events); 
	}
	
	public MotionEventPlot() {
		
	}
	
	public static void motionDurationBasedEmpiricalEvents(DataMotionEvents ev) {
		Fingers.MotionEvents.TAP.setDuration(ev.getEventsMap().get("TAP").getDuration());
		Fingers.MotionEvents.LEAP_DTAP.setDuration(ev.getEventsMap().get("LEAP_DTAP").getDuration());
		Fingers.MotionEvents.FAR_DTAP.setDuration(ev.getEventsMap().get("FAR_DTAP").getDuration());
		Fingers.MotionEvents.NEAR_DTAP.setDuration(ev.getEventsMap().get("NEAR_DTAP").getDuration());
		Fingers.MotionEvents.HOP_TAP.setDuration(ev.getEventsMap().get("HOP_TAP").getDuration());
		Fingers.MotionEvents.LEAP_TAP.setDuration(ev.getEventsMap().get("LEAP_TAP").getDuration());
		Fingers.MotionEvents.PINKY_TAP.setDuration(ev.getEventsMap().get("PINKY_TAP").getDuration());
		Fingers.MotionEvents.SIDE_TAP.setDuration(ev.getEventsMap().get("SIDE_TAP").getDuration());
		Fingers.MotionEvents.SHIFT.setDuration(0);
		Fingers.MotionEvents.SPACE.setDuration(0);
		Fingers.MotionEvents.ENTER.setDuration(0);
	}
	
	public static DataMotionEvents loadAllMotionEvents(boolean display) { 
		System.out.println("Displaying all motion events...");
		
		ArrayList<DataMotionEvents> allDataFiles = new ArrayList<DataMotionEvents>(); 
		File dir = new File(MotionEventPlot.class.getResource("/datacollecting/data_motion_events").getFile()); 
		
		if (!dir.exists()) {
			System.out.println("error: unable to find paths");
		}
		else {
			System.out.println("found files");
		}
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(DataMotionEvents.class);
	
			Unmarshaller unMarshaller = context.createUnmarshaller(); 

			if (dir.listFiles().length == 0) {
				System.out.println("Fatal: no data files available");
				return new DataMotionEvents(); 
			}
			System.out.println(dir.listFiles());
			for (File fil : dir.listFiles()) {
				System.out.println("Loading file: " + fil.getName());
				allDataFiles.add((DataMotionEvents)unMarshaller.unmarshal(fil));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error");
		} 

		// average event
		DataMotionEvents avgEvents = new DataMotionEvents(); 
		avgEvents.setUserName("Average Across Users");
		Map<String, DataMotionEvent> avgEventMap = avgEvents.getEventsMap();
		

		for (DataMotionEvents filEv : allDataFiles) {
			if (display) new MotionEventPlot(filEv); 
			for (DataMotionEvent tap : filEv.getEventsMap().values()) {
				avgEventMap.get(tap.name).accumulateDuration(tap.getDuration()); 
				avgEventMap.get(tap.name).incTimesPressed();
			}
		}
		
		
		for (DataMotionEvent tap : avgEvents.getEventsMap().values()) {
			tap.averageDuration();
		}
		
		
		if (display) 
			new MotionEventPlot(avgEvents);
		
		System.out.println("Finished Loading Files");
		return avgEvents; 
	}
	
	public void displayDataMotionEvents(DataMotionEvents events) {
		DefaultCategoryDataset data = new DefaultCategoryDataset(); 
		
		for (DataMotionEvent e : events.getEventsMap().values()) {
			data.addValue(e.getDuration(), "", e.name);
		}
		
		displayMotionEventPlot(data, events.username); 
	}
	
	public void displayFingerMotionEvents() {
		DefaultCategoryDataset data = new DefaultCategoryDataset(); 
		
		data.addValue(Fingers.MotionEvents.TAP.getDuration(), "", "TAP");
		data.addValue(Fingers.MotionEvents.HOP_TAP.getDuration(), "", "HOP_TAP");
		data.addValue(Fingers.MotionEvents.LEAP_TAP.getDuration(), "", "LEAP_TAP");
		data.addValue(Fingers.MotionEvents.NEAR_DTAP.getDuration(), "", "NEAR_DTAP");
		data.addValue(Fingers.MotionEvents.FAR_DTAP.getDuration(), "", "FAR_DTAP");
		data.addValue(Fingers.MotionEvents.LEAP_DTAP.getDuration(), "", "LEAP_DTAP");
		data.addValue(Fingers.MotionEvents.SIDE_TAP.getDuration(), "", "SIDE_TAP");
		data.addValue(Fingers.MotionEvents.PINKY_TAP.getDuration(), "", "PINKY_TAP");
		
		displayMotionEventPlot(data, "times based on distance"); 
	}
	
	private void displayMotionEventPlot(DefaultCategoryDataset data, String name) {

		JFreeChart barChart = ChartFactory.createBarChart(
				"event times",
				"motion event", 
				"average duration", 
				data);
	    CategoryAxis domainAxis = barChart.getCategoryPlot().getDomainAxis();
	     domainAxis.setCategoryLabelPositions(
	        CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	    );
	     
	    ValueAxis valueAxis = barChart.getCategoryPlot().getRangeAxis(); 
	 
	    valueAxis.setUpperBound(500);
	        
		ChartPanel panel = new ChartPanel(barChart); 
	
		setContentPane(panel);
		setPreferredSize(new Dimension(640, 480));
		pack(); 
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(name);
		setVisible(true);
	}
}
