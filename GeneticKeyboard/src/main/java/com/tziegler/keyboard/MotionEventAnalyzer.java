package com.tziegler.keyboard;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


public class MotionEventAnalyzer {
	static final String[] TEST_FILENAME = { "data/analyzertest1" }; 
	String[] testText; 
	Keyboard board;
	
	MotionEventAnalyzer() {
		testText = new String[TEST_FILENAME.length];
		for (int i = 0; i < TEST_FILENAME.length; i++) {
			try {
				String str = new String(Files.readAllBytes(Paths.get(TEST_FILENAME[i])));
				testText[i] = str; 
			} catch (IOException e) {
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
		Fingers.MotionEvents motionEvents;
		
		long lastKeyTime = 0; 
		
		GUI() {
			board = new Keyboard(); // qwerty board;
			fingers = new Fingers();
			motionEvents = new Fingers.MotionEvents();
			board.populateAbcToIndex();
			
			frame = new JFrame("Typing Test"); 
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
					
					Fingers.MotionEvent ev = fingers.getMotionEvent(board.asciiToIndex(c), c); 
					
					System.out.println("name: " + ev.name);
					long deltaTime = System.currentTimeMillis() - lastKeyTime; 
					System.out.println("delta_time: " + deltaTime);
					ev.cumuDuration += deltaTime;
					ev.timesPressed++;
					lastKeyTime = System.currentTimeMillis();
				}
			});
			panel.add(text);
			
			frame.getContentPane().add(panel);
			frame.pack();
		}
	}
	
	public void beginTest() {
		GUI gui = new GUI(); 

		for (int i = 0; i < TEST_FILENAME.length; i++) {
			Fingers.MotionEvents result;
			System.out.println("Beginning test " + i + " of " + TEST_FILENAME.length);
			gui.label.setText("<html><pre>" + testText[i].toString() + "</pre></html>");
			
			System.out.println("Start typing what you see. Type '@' to end.");
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gui.frame.setVisible(true);
			gui.frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
				    JAXBContext context;
					try {
						context = JAXBContext.newInstance(Fingers.MotionEvents.class);
					    Marshaller m = context.createMarshaller();
					    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					    m.marshal(gui.motionEvents, System.out);
					} catch (JAXBException e1) {
						e1.printStackTrace();
					}

				    
					System.out.println("finished");
				}
			});
		}
	}
}
