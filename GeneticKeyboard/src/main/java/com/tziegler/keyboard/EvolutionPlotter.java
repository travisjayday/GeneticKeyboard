package com.tziegler.keyboard;

import java.awt.Dimension;
import java.awt.List;
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
	
	EvolutionPlotter() {
		fittestInGen = new ArrayList<Integer>(); 
	}
	
	void addFittestInGen(int f) {
		fittestInGen.add(f);
	}
	
	public void plotFittestInGen() {
		final XYSeries series = new XYSeries("Generation");
		for (int g = 0; g < fittestInGen.size(); g++) {
			series.add(g, fittestInGen.get(g));
		}
		XYSeriesCollection data = new XYSeriesCollection(series); 
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
		
		ApplicationFrame frame = new ApplicationFrame("name");
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
