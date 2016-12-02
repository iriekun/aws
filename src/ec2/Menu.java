package ec2;

import java.util.Scanner;

import ec2.ServiceClient;
public class Menu {
	static ServiceClient serviceEc2 = new ServiceClient();
	static Scanner sc = new Scanner(System.in);

	public static int returnMenu(){
		System.out.println("************MENU***************** ");
		System.out.println("Select option below: ");
		System.out.println("1) List all regions and endpoints");
		System.out.println("2) Create instance");
		System.out.println("3) List all instances");
		System.out.println("4) Stop an instance");
		System.out.println("5) Exit");
		System.out.println("********************************");

		int option = sc.nextInt();
		switch(option){
		case 1: serviceEc2.listAllRegion(); break;
		case 2: serviceEc2.createInstance(); break;
		case 3: serviceEc2.listAllInstances(); break;
		case 4: serviceEc2.stopInstance(); break;
		case 5: System.exit(0); break;
		default:{
			System.out.println("please enter 1 to 5");
			returnMenu();
		}
		}
		return option;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		returnMenu();
	}

}
