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

public class LineChart2 extends ApplicationFrame
{
	public LineChart2( String applicationTitle , String chartTitle )
	{
		super(applicationTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(
				chartTitle,
				"Time","Disk Reads (bytes)",
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
		 Map<Date, Double> map = new TreeMap<Date, Double>(cw.getDiskRead());
	      for (Map.Entry<Date, Double> entry : map.entrySet()) {
	          System.out.println("Key : " + entry.getKey()
					+ " Value : " + entry.getValue());
	          dataset.addValue(entry.getValue().byteValue(), "cpu" , entry.getKey().toString().substring(4,16));
	      }
	
		return dataset;
	}

	public static void main( String[ ] args ) 
	{
		LineChart2 chart = new LineChart2(
				"Cloud Watch" ,
				"CPU Utilization");

		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
	}
}