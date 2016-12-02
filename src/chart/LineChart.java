package chart;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import ec2.CloudWatchCpu;

public class LineChart extends ApplicationFrame
{
	public LineChart( String applicationTitle , String chartTitle )
	{
		super(applicationTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(
				chartTitle,
				"Time","CPU Utilization (percentage)",
				createDataset(),
				PlotOrientation.VERTICAL,
				true,true,false);

		ChartPanel chartPanel = new ChartPanel( lineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension(1000 , 500 ) );
		setContentPane( chartPanel );
	}

	private DefaultCategoryDataset createDataset()
	{
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		CloudWatchCpu cw = new CloudWatchCpu();
		 Map<Date, Double> map = new TreeMap<Date, Double>(cw.getCpuUtilization());
	      for (Map.Entry<Date, Double> entry : map.entrySet()) {
	          System.out.println("Key : " + entry.getKey()
					+ " Value : " + entry.getValue());
	          dataset.addValue(entry.getValue(), "cpu" , entry.getKey().toString().substring(4,16));
	      }
	
		return dataset;
	}

	public static void main( String[ ] args ) 
	{
		LineChart chart = new LineChart(
				"Cloud Watch" ,
				"CPU Utilization");

		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
	}
}