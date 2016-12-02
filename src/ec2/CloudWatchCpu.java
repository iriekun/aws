package ec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.jfree.ui.RefineryUtilities;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

import chart.LineChart;

public class CloudWatchCpu {

	static AWSCredentials credentials;
	static AmazonCloudWatch cwClient ;
	
	public Map<Date, Double> getCpuUtilization(){
		 TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		 Map<Date,Double> map=new HashMap<Date,Double>();

		credentials =  new ProfileCredentialsProvider("default").getCredentials();
		cwClient = new AmazonCloudWatchClient(credentials); 
		cwClient.setEndpoint("monitoring.eu-west-1.amazonaws.com");
		long offsetInMilliseconds = 1000 * 60 * 60 * 24;

		Dimension dimension = new Dimension();
		dimension.setName("InstanceId");
		dimension.setValue("i-08c2f4b60d6b01010");

		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withStartTime(new Date(new Date().getTime() - offsetInMilliseconds)) //measurement start from last 24hour
				.withNamespace("AWS/EC2")
				.withPeriod(60*60*6) //unit: second //count every 1hour
				.withMetricName("CPUUtilization")
				.withStatistics("Average") 
				.withDimensions(Arrays.asList(dimension))
				.withEndTime(new Date()); //mesurement start from last 24hour

		GetMetricStatisticsResult getMetricStatisticsResult = cwClient.getMetricStatistics(request);

		List <Datapoint> datapoints = getMetricStatisticsResult.getDatapoints();
		for(Datapoint dp : datapoints){
			map.put(dp.getTimestamp(), dp.getAverage());
		}
		
		
		
      Map<Date, Double> treeMap = new TreeMap<Date, Double>(map);
//      for (Map.Entry<Date, Double> entry : treeMap.entrySet()) {
//          System.out.println("Key : " + entry.getKey()
//				+ " Value : " + entry.getValue());
//      }
		return treeMap;
	}
	public Map<Date, Double> getDiskRead(){
		 TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		 Map<Date,Double> map=new HashMap<Date,Double>();

		credentials =  new ProfileCredentialsProvider("default").getCredentials();
		cwClient = new AmazonCloudWatchClient(credentials); 
		cwClient.setEndpoint("monitoring.eu-west-1.amazonaws.com");
		long offsetInMilliseconds = 1000 * 60 * 60 * 24;

		Dimension dimension = new Dimension();
		dimension.setName("InstanceId");
		dimension.setValue("i-08c2f4b60d6b01010");

		GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
				.withStartTime(new Date(new Date().getTime() - offsetInMilliseconds)) //measurement start from last 24hour
				.withNamespace("AWS/EC2")
				.withPeriod(60*60) //unit: second //count every 1hour
				.withMetricName("DiskReadOps")
				.withStatistics("Average") 
				.withDimensions(Arrays.asList(dimension))
				.withEndTime(new Date()); //mesurement start from last 24hour

		GetMetricStatisticsResult getMetricStatisticsResult = cwClient.getMetricStatistics(request);

		List <Datapoint> datapoints = getMetricStatisticsResult.getDatapoints();
		for(Datapoint dp : datapoints){
			map.put(dp.getTimestamp(), dp.getAverage());
		}
		
		
		
     Map<Date, Double> treeMap = new TreeMap<Date, Double>(map);
//     for (Map.Entry<Date, Double> entry : treeMap.entrySet()) {
//         System.out.println("Key : " + entry.getKey()
//				+ " Value : " + entry.getValue());
//     }
		return treeMap;
	}
//	public static void main( String[ ] args ) 
//	{
//		credentials =  new ProfileCredentialsProvider("default").getCredentials();
//		cwClient = new AmazonCloudWatchClient(credentials); 
//		cwClient.setEndpoint("monitoring.eu-west-1.amazonaws.com");
//		long offsetInMilliseconds = 1000 * 60 * 60 * 24;
//		Dimension dimension = new Dimension();
//		dimension.setName("InstanceId");
//		dimension.setValue("i-08c2f4b60d6b01010");
//
//		GetMetricStatisticsRequest request2 = new GetMetricStatisticsRequest()
//				.withStartTime(new Date(new Date().getTime() - offsetInMilliseconds)) //measurement start from last 24hour
//				.withNamespace("AWS/EC2")
//				.withPeriod(60*60) //unit: second //count every 1hour
//				.withMetricName("DiskReadOps")
//				.withStatistics("Average") 
//				.withDimensions(Arrays.asList(dimension))
//				.withEndTime(new Date()); //mesurement start from last 24hour
//
//		GetMetricStatisticsResult getMetricStatisticsResult2 = cwClient.getMetricStatistics(request2);
//
//		List <Datapoint> datapoints2 = getMetricStatisticsResult2.getDatapoints();
//		for(Datapoint dp : datapoints2){
//			//map.put(dp.getTimestamp(), dp.getAverage());
//			System.out.println(dp.getTimestamp() + dp.getAverage().toString());
//			
//		}
//	}
	
}
