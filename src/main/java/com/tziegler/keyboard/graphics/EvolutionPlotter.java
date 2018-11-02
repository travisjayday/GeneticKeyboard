package com.tziegler.keyboard.graphics;

import java.awt.Dimension;
import java.awt.List;
import java.io.*;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class EvolutionPlotter {

	ArrayList<Integer> fittestInGen; 
	ArrayList<Integer> avgFitnessInGen; 
	ApplicationFrame lastFrame = null; 
	BufferedWriter writer = null;
	
	public EvolutionPlotter() {
		fittestInGen = new ArrayList<Integer>(); 
		avgFitnessInGen = new ArrayList<Integer>(); 
	}
	
	public void addFittestInGen(int f) {
		fittestInGen.add(f);
	}
	
	public void addAvgFitInGen(int f) {
		avgFitnessInGen.add(f); 
	}
	
	public void plotFittestInGen(boolean includeAvg, String name) {
		if (lastFrame != null)
			lastFrame.dispose();
		
		final XYSeries series = new XYSeries("Fittest");
		final XYSeries avgSeries = new XYSeries("Average"); 
		
		for (int g = 0; g < fittestInGen.size(); g++) {
			series.add(g, fittestInGen.get(g));
			if (includeAvg) avgSeries.add(g, avgFitnessInGen.get(g));
		}
		XYSeriesCollection data = new XYSeriesCollection(); 
		data.addSeries(series);
		if (includeAvg) data.addSeries(avgSeries);
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Generation vs. Fitness",
				"Gen", 
				"Fit", 
				data,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
		);
		
		ChartPanel panel = new ChartPanel(chart);
		panel.setPreferredSize(new Dimension(640, 480));
		
		ApplicationFrame frame = new ApplicationFrame(name);
		lastFrame = frame; 
	
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void writeToFile(String fname) {
		try { 
			writer = new BufferedWriter(new FileWriter(fname));
			for (int i = 0; i < fittestInGen.size(); i++) {
				writer.append(fittestInGen.get(i) + ", " + avgFitnessInGen.get(i) + "\n");
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
