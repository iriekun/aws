package ec2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

public class ServiceClient {
	Menu menu = new Menu();
	Scanner sc = new Scanner(System.in);
	
	private static AmazonEC2 ec2;
	private static String keypair;
	private static List<com.amazonaws.regions.Region> regionList;
	private static String endpoint;
	static ArrayList<String> instanceRequestIds = new ArrayList<String>();
	static List<String> instanceList = new ArrayList<String>();
	static String instanceId; //instance to stop 

	public ServiceClient(){	        
		// use client builders to have the SDK automatically detect the region your code is running in
		ec2 = AmazonEC2ClientBuilder.standard()
				//	.withCredentials(new EnvironmentVariableCredentialsProvider())
				.withRegion(Regions.EU_WEST_1).build();
	}
	public void listAllRegion(){
		regionList = RegionUtils.getRegions();
		System.out.println("List of regions and its endpoints: ");
		for(int i=0; i<regionList.size(); i++){
			System.out.println(regionList.get(i)+"		"+"ec2"+"."+regionList.get(i)+".amazon.com");
		}
		menu.returnMenu();
	}
	public void createInstance(){
		//create an instance
		RunInstancesRequest runInstancesRequest =
				new RunInstancesRequest();
		runInstancesRequest.withImageId("ami-0d77397e")
		.withInstanceType("t2.micro")
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName("newkeypair")
		.withSecurityGroups("mysecuritygroup");
		RunInstancesResult runInstancesResult =
				ec2.runInstances(runInstancesRequest);
		System.out.println("An instances is created!!!");
		menu.returnMenu();

	}
	public void listAllInstances(){
		System.out.println("List of all instances: ");
		//get instance state and status
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		DescribeInstancesResult result = ec2.describeInstances(request.withMaxResults(10));
		List<Reservation> reservations = result.getReservations();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();

			for (Instance instance : instances) {
				System.out.println(instance.getInstanceId()+" : "+instance.getState().getName());        

				//display instance status if instance state=running
				//because instance status can be retrieved only when instance is running otherwise throw errors
				if(instance.getState().getName().equals("running")){
					DescribeInstanceStatusRequest describeStatusRequest = new DescribeInstanceStatusRequest()
							.withInstanceIds(instance.getInstanceId());
					DescribeInstanceStatusResult describeStatusResult = ec2.describeInstanceStatus(describeStatusRequest);
					List<InstanceStatus> state = describeStatusResult.getInstanceStatuses();
					System.out.println(state.get(0).getInstanceStatus());


				}			  
			}
		}
		menu.returnMenu();
	}
	public void stopInstance(){
		System.out.println("Enter instance id:");
		String instanceId = sc.next();
		//stop the running instances
		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest().withInstanceIds(instanceId);
		StopInstancesResult stopInstancesResult = ec2.stopInstances(stopInstancesRequest);
		System.out.println("Instance "+instanceId+" is stopped! ");
		menu.returnMenu();
	}

	public static void main(String args[]) throws IOException{    
		System.out.println("Welcome to AWS EC2");





		//			keypair = "mykeypair";
		//			//check if keypair given is already exist
		//			DescribeKeyPairsRequest keyPairRequest = new DescribeKeyPairsRequest().withKeyNames(keypair);
		//			DescribeKeyPairsResult keyPairResult = ec2.describeKeyPairs(keyPairRequest);
		//			if(keyPairResult.getKeyPairs().isEmpty()){
		//				System.out.println("no keypair");
		//				//create keypair 
		//				CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
		//				createKeyPairRequest.withKeyName(keypair);
		//				CreateKeyPairResult createKeyPairResult =
		//						  ec2.createKeyPair(createKeyPairRequest);
		//				 KeyPair keyPair = new KeyPair();	    	
		//		         keyPair = createKeyPairResult.getKeyPair();           		    	
		//       
		//		         String privateKey = keyPair.getKeyMaterial();
		//		         File keyFile = new File("/Users/huolto/Download/"+keypair);
		//		         FileWriter fw = new FileWriter(keyFile);
		//		         fw.write(privateKey);
		//		         fw.close();
		//			}else{
		//				System.out.println("key exist");
		//			}




	}
}
