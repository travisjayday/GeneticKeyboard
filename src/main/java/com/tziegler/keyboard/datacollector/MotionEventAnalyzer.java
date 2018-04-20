package com.tziegler.keyboard.datacollector;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.tziegler.keyboard.Fingers;
import com.tziegler.keyboard.Keyboard;
import com.tziegler.keyboard.datacollector.DataMotionEvents.DataMotionEvent;
import com.tziegler.keyboard.graphics.MotionEventPlot;


public class MotionEventAnalyzer {
	static final String[] TEST_FILENAME = { "/datacollecting/analyzertest1" }; 
	String[] testText; 
	Keyboard board;
	
	public MotionEventAnalyzer() {
		testText = new String[TEST_FILENAME.length];
		for (int i = 0; i < TEST_FILENAME.length; i++) {
			try {
				InputStream in = getClass().getResourceAsStream(TEST_FILENAME[i]); 
				byte[] b = new byte[in.available()];
				in.read(b, 0, b.length);
				
				testText[i] = new String(b); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		board = new Keyboard(); // create qwerty keyboard
	}
	
	class GUI {
		JLabel label;
		JFrame frame; 
		Keyboard board; 
		Fingers fingers; 
		DataMotionEvents motionEvents;
		
		long lastKeyTime = 0; 
		
		GUI() {
			board = new Keyboard(); // qwerty board;
			fingers = new Fingers(); 
			board.setKeysQWERTY();
			motionEvents = new DataMotionEvents();
			
			frame = new JFrame("Typing Test"); 
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			
			JPanel panel = new JPanel(); 
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			
			label = new JLabel("Type Here"); 
			label.setFont(new Font("Sans", 0, 25));
			panel.add(label); 
			
			JTextField text = new JTextField(10); 
			text.setFont(new Font("Sans", 0, 50));
			
			text.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					
					char c = e.getKeyChar();
					System.out.println("pressed key: " + c);
					
					if (c == '@') {
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						return;
					}
	
					int idx = board.asciiToIndex(c); 
					if (idx == -1) // key was not found in board, ex: space , enter, 1, 3
						return; 
					
					Fingers.MotionEvent raw = fingers.getMotionEvent(idx, c); 
					DataMotionEvent dataEvent = motionEvents.events.get(raw.name);
					
					
					System.out.println("name: " + dataEvent.name);
					long deltaTime = System.currentTimeMillis() - lastKeyTime; 
					lastKeyTime = System.currentTimeMillis();
					
					if (deltaTime < 600) { 
						System.out.println("delta_time: " + deltaTime);
						dataEvent.cumuDuration += deltaTime; 
						dataEvent.timesPressed++; 
					}
					else {
						System.out.println("Skipped due to timeout");
					}
				}
			});
			panel.add(text);
			
			frame.getContentPane().add(panel);
			frame.pack();
		}
	}

	public void beginTest() {
		// get user name to store file later
		System.out.println("What's your name?");
		Scanner in = new Scanner(System.in); 
		String testerName = in.next();
		
		GUI gui = new GUI(); 

		//for (int i = 0; i < TEST_FILENAME.length; i++) {
			Fingers.MotionEvents result;
			//System.out.println("Beginning test " + i + " of " + TEST_FILENAME.length);
			System.out.println("beginning test");
			gui.label.setText("<html><pre>" + testText[0].toString() + "</pre></html>");
			
			System.out.println("Start typing what you see. Type 'yes', then hit enter to start. ");
			while (!in.hasNext());
			
			in.close(); 
			/*try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		gui.frame.setVisible(true);
		gui.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				
				gui.motionEvents.setUserName(testerName);
				
				int smallest = 9999;  
				// calculate relative durations and averages
				for (DataMotionEvent ev : gui.motionEvents.getEventsMap().values()) {
					if (ev.timesPressed == 0) {
						System.out.println("Aborting: not all motion events have been exausted!"); 
						System.out.println("Motion event: " + ev.name + " zero times pressed!"); 
						return;
					}
					ev.setDuration(ev.cumuDuration / ev.timesPressed); // average duration
					if (smallest > ev.getDuration())
						smallest = ev.getDuration(); 
				}
				
				for (DataMotionEvent ev : gui.motionEvents.getEventsMap().values()) {
					ev.setDuration(((int)(((double)ev.getDuration() / (double) smallest) * 100))); // normalize duration
				}
				
			    JAXBContext context;
				try {
					System.out.println("tap duration: " + gui.motionEvents.getEventsMap().get("TAP").getDuration());
					context = JAXBContext.newInstance(DataMotionEvents.class);
				    Marshaller m = context.createMarshaller();
				    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				    
				    File file = new File(MotionEventAnalyzer.class.getResource(
		    				"/datacollecting/data_motion_events/").getPath() 
				    		+ testerName 
		    				+ "_" + System.currentTimeMillis()
		    				+ ".xml"); 
				    
				    System.out.println("Writing to: " + file.getAbsolutePath());
				    
				    file.createNewFile();
				    FileOutputStream out = new FileOutputStream(file); 
				    
				    m.marshal(gui.motionEvents, out);
	
				    // display results
				    new MotionEventPlot(gui.motionEvents);    	
				    gui.frame.setVisible(false);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				System.out.println("finished");
				super.windowClosing(e);
				return; 
			}
		});
		System.out.println("end of func");
	}
}
