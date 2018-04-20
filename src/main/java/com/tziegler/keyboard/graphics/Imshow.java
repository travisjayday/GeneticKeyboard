package com.tziegler.keyboard.graphics; 

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.plaf.ButtonUI;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Imshow {

	static Imshow mImshow;
	public JFrame Window;
	private ImageIcon image;
	private JLabel label;
	// private MatOfByte matOfByte;
	private boolean SizeCustom;
	private int Height, Width;

	public  Imshow(String title) {
		Window = new JFrame();
		image = new ImageIcon();
		label = new JLabel();
		// matOfByte = new MatOfByte();
		label.setIcon(image);
		Window.getContentPane().add(label);
		Window.setResizable(false);
		Window.setTitle(title);
		SizeCustom = false;
		setCloseOption(0);
	}

	public  Imshow(String title, int height, int width) {
		SizeCustom = true;
		Height = height;
		Width = width;

		Window = new JFrame();
		image = new ImageIcon();
		label = new JLabel();
		// matOfByte = new MatOfByte();
		label.setIcon(image);
		Window.getContentPane().add(label);
		Window.setResizable(false);
		Window.setTitle(title);
		setCloseOption(0);

	}

	public  void showImage(Mat img) {
		if (SizeCustom) {
			Imgproc.resize(img, img, new Size(Height, Width));
		}
		// Highgui.imencode(".jpg", img, matOfByte);
		// byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		try {
			// InputStream in = new ByteArrayInputStream(byteArray);
			// bufImage = ImageIO.read(in);
			bufImage = toBufferedImage(img);
			image.setImage(bufImage);
			Window.pack();
			label.updateUI();
			Window.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// CREDITS TO DANIEL: http://danielbaggio.blogspot.com.br/ for the improved
	// version !

	public  BufferedImage toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster()
				.getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;

	}

	// Thanks to sutr90 for reporting the issue : https://github.com/sutr90

	public  void setCloseOption(int option) {

		switch (option) {
		case 0:
			Window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			break;
		case 1:
			Window.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			break;
		default:
			Window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}

	}

	/**
	 * Sets whether this window should be resizable or not, by default it is not
	 * resizable
	 * 
	 * @param resizable
	 *            <code>true</code> if the window should be resizable,
	 *            <code>false</code> otherwise
	 */
	public  void setResizable(boolean resizable) {
		Window.setResizable(resizable);
	}


	// Thanks to Jan Monterrubio for additional static methods for viewing images. 


	/**
	 * Displays the given {@link Mat} in a new instance of {@link Imshow}
	 * 
	 * @param mat
	 *            the {@link Mat} to display
	 */
	public static  void show(Mat mat, boolean sameWin) {
		show(mat, new Dimension(mat.rows(), mat.cols()), "", false,
				WindowConstants.EXIT_ON_CLOSE, sameWin);
	}

	/**
	 * Displays the given {@link Mat} in a new instance of {@link Imshow} with
	 * the given title as the title for the window
	 * 
	 * @param mat
	 *            the {@link Mat} to display
	 * @param frameTitle
	 *            the title for the frame
	 */
	public static  void show(Mat mat, String frameTitle, boolean sameWin) {
		show(mat, new Dimension(mat.rows(), mat.cols()), frameTitle, false,
				WindowConstants.EXIT_ON_CLOSE, sameWin);
	}

	/**
	 * Displays the given {@link Mat} in a new instance of {@link Imshow} with
	 * the given title as the title for the window and determines whether the
	 * frame is resizable or not
	 * 
	 * @param mat
	 *            the {@link Mat} to display
	 * @param frameTitle
	 *            the title for the frame
	 * @param resizable
	 *            whether the frame should be resizable or not
	 */
	public static  void show(Mat mat, String frameTitle, boolean resizable, boolean sameWin) {
		show(mat, new Dimension(mat.rows(), mat.cols()), frameTitle, resizable,
				WindowConstants.EXIT_ON_CLOSE, sameWin);
	}

	/**
	 * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
	 * set size
	 * 
	 * @param mat
	 *            the {@link Mat} to display
	 * @param frameSize
	 *            the size for the frame
	 */
	public static  void show(Mat mat, Dimension frameSize, boolean sameWin) {
		show(mat, frameSize, "", false, WindowConstants.EXIT_ON_CLOSE, sameWin);
	}

	/**
	 * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
	 * set size and given title
	 * 
	 * @param mat
	 *            the {@link Mat} to display
	 * @param frameSize
	 *            the size for the frame
	 * @param frameTitle
	 *            the title for the frame
	 */
	public static  void show(Mat mat, Dimension frameSize, String frameTitle, boolean sameWin) {
		show(mat, frameSize, frameTitle, false, WindowConstants.EXIT_ON_CLOSE, sameWin);
	}

	/**
	 * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
	 * set size and given title and whether it is resizable or not
	 * 
	 * @param mat
	 *            the {@link Mat} to display
	 * @param frameSize
	 *            the size for the frame
	 * @param frameTitle
	 *            the title for the frame
	 */
	public static  void show(Mat mat, Dimension frameSize, String frameTitle,
			boolean resizable, boolean sameWin) {
		show(mat, frameSize, frameTitle, resizable,
				WindowConstants.EXIT_ON_CLOSE, sameWin);
	}

	/**
	 * Displays the given {@link Mat} in a new instance of {@link Imshow} with a
	 * set size and given title and whether it is resizable or not, and with the
	 * close operation set
	 * 
	 * @param mat
	 *            the {@link Mat} to display
	 * @param frameSize
	 *            the size for the frame
	 * @param frameTitle
	 *            the title for the frame
	 * @param resizable
	 *            wether the frame is resizable or not
	 * @param closeOperation
	 *            the constant for the default close operation of the frame
	 */
	public static  void show(final Mat mat, Dimension frameSize, String frameTitle,
		boolean resizable, int closeOperation, boolean newWin) {
		
		if (mImshow == null || newWin) 
				mImshow = new Imshow(frameTitle, frameSize.height, frameSize.width);
		
		mImshow.setResizable(resizable);
		
		mImshow.Window.getRootPane().addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
		        // do stuff
				mImshow.Width = e.getComponent().getHeight();
				mImshow.Height = e.getComponent().getWidth();
				mImshow.showImage(mat.clone());
		    }

			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
        });
		/*
		 * This is a bad way to access the window, but due to legacy stuff I
		 * won't change the access patterns
		 */
		mImshow.Window.setDefaultCloseOperation(closeOperation);
		mImshow.showImage(mat.clone());
	}
}