package s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;


public class ServiceClient {
	static Scanner sc = new Scanner(System.in);


	static AWSCredentials credentials =  new ProfileCredentialsProvider("default").getCredentials();
	static AmazonS3 s3Client = new AmazonS3Client(credentials);

	static Region usWest2 = Region.getRegion(Regions.US_WEST_2); //oregon 
	static Region euCentral1 = Region.getRegion(Regions.EU_CENTRAL_1 ); //frankfurt
	static Region saEast1 = Region.getRegion(Regions.SA_EAST_1); //singapore

	static String bucketName, bucketName2, bucketName3; 
	static String filename, bucketLocation;
	static int i=0;	
	static long t, t1, t2;

	public static void createBucket() throws IOException{
		System.out.println("creating buckets in Oregon, Frankfurt and Singapore...");
		s3Client.setRegion(usWest2);
		bucketName = "iriekun-bucket-" + UUID.randomUUID();
		s3Client.createBucket(bucketName);
		//	i++;
		s3Client.setRegion(euCentral1);
		bucketName2 = "iriekun-bucket-" + UUID.randomUUID();
		s3Client.createBucket(bucketName2);
		//	i++;
		s3Client.setRegion(saEast1);
		bucketName3 = "iriekun-bucket-" + UUID.randomUUID();
		s3Client.createBucket(bucketName3);
		//		i++;
		menu();
	}
	public static void listAllBucket() throws IOException{
		System.out.println("Listing buckets...");
		for (Bucket bucket : s3Client.listBuckets()) {
			//get bucket location
			bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucket.getName()));
			System.out.println(bucket.getName() + " - " +bucketLocation);
		}
		System.out.println();
		menu();
	}
	public static void listSpecificBucket() throws IOException{
		System.out.println("Enter 1 for US_WEST_2 | 2 for EU_CENTRAL_1 | 3 for SA_EAST_1");
		System.out.println("choose region by selection 1 to 3: ");
		int choice = sc.nextInt();
		String l=null;
		switch(choice){
		case 1: l="us-west-2"; //oregon
		break;
		case 2: l="eu-central-1"; //frankfurt
		break;
		case 3: l="ap-southeast-1"; //singapore
		break;
		default: {
			System.out.println("please enter 1 to 3");
			menu();
		}

		}
		System.out.println("You chose: "+ l);
		System.out.println("Listing buckets...");
		for (Bucket bucket : s3Client.listBuckets()) {
			bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucket.getName()));
			if(bucketLocation.equalsIgnoreCase(l)){
				System.out.println(bucket.getName() + " - " +bucketLocation);
			}
		}
		menu();
	}
	public static void deleteBucket() throws IOException, AmazonServiceException{
		System.out.println("Please enter bucket name: ");
		String bucketName = sc.next();
		System.out.println("Deleting a bucket...");

		ObjectListing object_listing = s3Client.listObjects(bucketName);
		//delete object inside bucket
		//bucket can't be deleted directly if there are objects inside the bucket
		while (true) {
			for (Iterator<?> iterator = object_listing.getObjectSummaries().iterator();
					iterator.hasNext();) {
				S3ObjectSummary summary = (S3ObjectSummary)iterator.next();
				s3Client.deleteObject(bucketName, summary.getKey());
			}
			// more object_listing to retrieve?
			if (object_listing.isTruncated()) {
				object_listing = s3Client.listNextBatchOfObjects(object_listing);
			} else {
				break;
			}
		};
		s3Client.deleteBucket(bucketName);
		menu();
	}
	public static void uploadFile() throws IOException, AmazonServiceException{
		System.out.println("Uploading a new object to s3Client from a file\n");
		System.out.println("Enter bucket name: ");
		String bucket_name = sc.next();
		System.out.println("Enter absolute file path to upload: ");
		String filePath = sc.next();
		File file = new File(filePath);
		filename = file.getName();
		t1 = System.currentTimeMillis();
		s3Client.putObject(new PutObjectRequest(bucket_name, filename, file));
		t2 = System.currentTimeMillis();
		t = t2 -t1;
		System.out.println("Total upload time = "+t+" ms");
		menu();
	}
	public static void DownloadFile() throws IOException, AmazonServiceException{
		System.out.println("Listing objects");
		System.out.println("Enter bucket name: ");
		String bucketName = sc.next();
		//list all object inside a bucket
		ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest()
				.withBucketName(bucketName)
				.withPrefix("iriekun-bucket"));
		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
			System.out.println(" - " + objectSummary.getKey() + "  " +
					"(size = " + objectSummary.getSize() + ")");
		}
		System.out.println("Enter object name: ");
		String filename = sc.next();
		System.out.println("Enter absolute file path to store: ");
		String filePath = sc.next();
		System.out.println("Downloading an object...");
		t1 = System.currentTimeMillis();
		S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, filename));
		t2 = System.currentTimeMillis();
		System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
		t = t2-t1;
		System.out.println("Total download time = "+t+" ms");
		writeFile(object.getObjectContent(), filename, filePath);
		menu();
	}
	public static void writeFile(InputStream is, String filename, String filePath) throws IOException, AmazonServiceException{
		
		OutputStream os = new FileOutputStream(filePath + "/"+filename);
		byte[] buffer = new byte[1024];
		int bytesRead;
		//read from is to buffer
		while((bytesRead = is.read(buffer)) !=-1){
			os.write(buffer, 0, bytesRead);
		}
		is.close();
		//flush OutputStream to write any buffered data to file
		os.flush();
		os.close();
		menu();
	}
	public static void deleteFile() throws AmazonServiceException, IOException{
		System.out.println("Enter bucket name: ");
		String bucketName = sc.next();
		System.out.println("Enter file name: ");
		String filename = sc.next();
		System.out.println("Deleting an object...");
		s3Client.deleteObject(bucketName, filename);
		menu();
	}
	public static int menu() throws IOException{
		System.out.println("*****MENU***** ");
		System.out.println("Select option below: ");
		System.out.println("1) Create bucket");
		System.out.println("2) List all buckets");
		System.out.println("3) List regional buckets");
		System.out.println("4) Delete bucket");
		System.out.println("5) Upload file");
		System.out.println("6) Download file");
		System.out.println("7) Delete file");
		System.out.println("8) Exit");

		int option = sc.nextInt();
		switch(option){
		case 1: createBucket(); break;
		case 2: listAllBucket(); break;
		case 3: listSpecificBucket(); break;
		case 4: deleteBucket(); break;
		case 5: uploadFile(); break;
		case 6: DownloadFile(); break;
		case 7: deleteFile(); break;
		case 8: System.exit(0); break;
		default:{
			System.out.println("please enter 1 to 8");
			menu();
		}
		}
		return option;

	}

	public static void main(String args[]) throws IOException{
		System.out.println("Welcome to ...");

		menu();

	}
}
