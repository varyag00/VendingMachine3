/* Name: 	J. Daniel Gonzalez
 * UCID:	10058656
 * Class:	SENG 301
 * Ass:		3
 */

//TODO: completely remove the Parser package and the VendingMachineFactory Class

package ca.ucalgary.seng301.myvendingmachine.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.ucalgary.seng301.myvendingmachine.VendingMachineLogic;
import ca.ucalgary.seng301.vendingmachine.Coin;
import ca.ucalgary.seng301.vendingmachine.PopCan;
import ca.ucalgary.seng301.vendingmachine.VendingMachineStoredContents;
import ca.ucalgary.seng301.vendingmachine.hardware.CoinRack;
import ca.ucalgary.seng301.vendingmachine.hardware.DisabledException;
import ca.ucalgary.seng301.vendingmachine.hardware.PopCanRack;
import ca.ucalgary.seng301.vendingmachine.hardware.VendingMachine;

public class Tests {
						
	/*	Attributes	*/
	
	VendingMachine vm;
	List<Integer> constructCoinArgs;
	int selButtCount;
	int coinRackCap;
	int popCanRackCap;
	int receptCap;
	VendingMachineStoredContents vmsc;
	
	List<String> configPopNamesArgs;
	List<Integer> configPopCostArgs;
	
	List<Integer> loadCoinCounts;
	List<Integer> loadPopCounts;
	
	List<String> extractActualStringOutput;
	List<String> extractExpectedStringOutput;
	
	int extractActualChangeValue;
	int extractExpectedChangeValue;
	List<Object> extractObjOutput;
	
	int actualChangeSum;
	int expectedChangeSum;
 	
	int expectedPaymentCoinsInStorageBin;
	int actualPaymentCoinsInStorageBin;
	
	ArrayList<String> actualUnsoldPopCans;
	ArrayList<String> expectedUnsoldPopCans;
	
	/* Setup & TearDown Methods	*/
	
	@Before
	public void setUp() throws Exception {
		constructCoinArgs = new ArrayList<Integer>();
		configPopNamesArgs = new ArrayList<String>(); 
		configPopCostArgs = new ArrayList<Integer>();
		configPopNamesArgs = new ArrayList<String>(); 
		configPopCostArgs = new ArrayList<Integer>();
		loadCoinCounts = new ArrayList<Integer>();
		loadPopCounts = new ArrayList<Integer>();
		extractActualStringOutput = new ArrayList<String>();
		extractExpectedStringOutput = new ArrayList<String>();
		extractActualChangeValue = 0;
		actualChangeSum = 0;
		expectedPaymentCoinsInStorageBin = 0;
		actualPaymentCoinsInStorageBin = 0;
		actualUnsoldPopCans = new ArrayList<String>(); 		
		expectedUnsoldPopCans = new ArrayList<String>();
	}

	@After
	public void tearDown() throws Exception {
		constructCoinArgs.clear();
		configPopNamesArgs.clear();
		configPopCostArgs.clear();
		configPopNamesArgs.clear();
		configPopCostArgs.clear();
		loadCoinCounts.clear();
		loadPopCounts.clear();
		extractActualStringOutput.clear();
		extractExpectedStringOutput.clear();
	}

	/*	VendingMachineFactory methods required to test hardware	*/
	
	//construct method
    public void construct(List<Integer> coinKinds, int selectionButtonCount, int coinRackCapacity, int popCanRackCapacity, int receptacleCapacity) {
		int[] ck = new int[coinKinds.size()];
		int i = 0;
		for(Integer coinKind : coinKinds)
		    ck[i++] = coinKind;
		vm = new VendingMachine(ck, selectionButtonCount, coinRackCapacity, popCanRackCapacity, receptacleCapacity);
		new VendingMachineLogic(vm);
    }
	
    //configure method
    public void configure(List<String> popNames, List<Integer> popCosts) {
    	vm.configure(popNames, popCosts);
    }
    
    //load method
    public void load(List<Integer> coinCounts, List<Integer> popCanCounts) {
		int numberOfCoinRacks = vm.getNumberOfCoinRacks();
		int numberOfPopCanRacks = vm.getNumberOfPopCanRacks();
	
		if(coinCounts.size() != numberOfCoinRacks)
		    throw new IllegalArgumentException("The size of the coinCounts list must be identical to the number of coin racks in the machine");
		if(popCanCounts.size() != numberOfPopCanRacks)
		    throw new IllegalArgumentException("The size of the popCanCounts list must be identical to the number of pop can racks in the machine");
	
		int i = 0;
		for(Integer coinCount : coinCounts) {
		    CoinRack cr = vm.getCoinRack(i);
		    for(int count = 0; count < coinCount; count++)
			cr.loadWithoutEvents(new Coin(vm.getCoinKindForRack(i)));
		    i++;
		}
	
		i = 0;
		for(Integer popCanCount : popCanCounts) {
		    PopCanRack pcr = vm.getPopCanRack(i);
		    for(int count = 0; count < popCanCount; count++)
			pcr.loadWithoutEvents(new PopCan(vm.getPopKindName(i)));
		    i++;
		}
    }
    
    //unload method
    public VendingMachineStoredContents unload() {
		VendingMachineStoredContents contents = new VendingMachineStoredContents();
		contents.paymentCoinsInStorageBin.addAll(vm.getStorageBin().unloadWithoutEvents());
		for(int i = 0; i < vm.getNumberOfPopCanRacks(); i++) {
		    PopCanRack pcr = vm.getPopCanRack(i);
		    contents.unsoldPopCans.add(new ArrayList<>(pcr.unloadWithoutEvents()));
		}
		for(int i = 0; i < vm.getNumberOfCoinRacks(); i++) {
		    CoinRack cr = vm.getCoinRack(i);
		    contents.unusedCoinsForChange.add(new ArrayList<>(cr.unloadWithoutEvents()));
		}
		return contents;
    }
    
    //extract
    public List<Object> extract() {
    	return Arrays.asList(vm.getDeliveryChute().removeItems());
    }
    
    //insert
    public void insert(int value) throws DisabledException {
    	vm.getCoinSlot().addCoin(new Coin(value));
    }
    
    //press method
    public void press(int value) {
    	vm.getSelectionButton(value).press();
    }
    
    
    /*	 Tests	*/
    
	@Test
	public void T01() throws DisabledException {
		
		/*	construct(5, 10, 25, 100; 3; 10; 10; 10)	*/
		constructCoinArgs.add(5);
		constructCoinArgs.add(10);
		constructCoinArgs.add(25);
		constructCoinArgs.add(100);
		
		selButtCount = 3;
		coinRackCap = 10;
		popCanRackCap = 10;
		receptCap = 10;
		
		construct(constructCoinArgs, selButtCount, coinRackCap, popCanRackCap, receptCap);
		
		/*	configure("Coke", "water", "stuff"; 250, 250, 205)	*/
		configPopNamesArgs.add("Coke");
		configPopNamesArgs.add("water");
		configPopNamesArgs.add("stuff");
		
		configPopCostArgs.add(250);
		configPopCostArgs.add(250);
		configPopCostArgs.add(205);
		
		configure(configPopNamesArgs, configPopCostArgs);
		
		/*	load(1, 1, 2, 0; 1, 1, 1) 	*/
		loadCoinCounts.add(1);
		loadCoinCounts.add(1);
		loadCoinCounts.add(2);
		loadCoinCounts.add(0);
		
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		
		load(loadCoinCounts, loadPopCounts);
		
		/*	insert(100)	*/
		insert(100); 				
		
		/*	insert(100)	*/
		insert(100); 				
		
		/*	insert(25)	*/
		insert(25); 				

		/*	insert(25)	*/
		insert(25); 				

		/*	press(0)	*/
		press(0);
		
		/*	extract()	*/									
		
		extractObjOutput = extract(); 		

		/*	CHECK_DELIVERY(0, "Coke")	*/
		
		extractExpectedChangeValue = 0; 				//not updating actual change before calling helper funciton might mess up future tests
		extractExpectedStringOutput.add("Coke");
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
				
		/*	unload()	*/
		
		vmsc = unload();
		
		/*	CHECK_TEARDOWN(315; 0; "water", "stuff")	*/
		
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 315;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		
		expectedPaymentCoinsInStorageBin = 0;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);
		
		
		//checking List<List<PopCan>> unsoldPopCans 					
				
		expectedUnsoldPopCans.add("water");
		expectedUnsoldPopCans.add("stuff");
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
	}

	@Test
	public void T02() throws DisabledException {
		
		//construct(5, 10, 25, 100; 3; 10; 10; 10)
		constructCoinArgs.add(5);
		constructCoinArgs.add(10);
		constructCoinArgs.add(25);
		constructCoinArgs.add(100);
		
		selButtCount = 3;
		coinRackCap = 10;
		popCanRackCap = 10;
		receptCap = 10;
		
		construct(constructCoinArgs, selButtCount, coinRackCap, popCanRackCap, receptCap);
		
		//configure("Coke", "water", "stuff"; 250, 250, 205)
		configPopNamesArgs.add("Coke");
		configPopNamesArgs.add("water");
		configPopNamesArgs.add("stuff");
		
		configPopCostArgs.add(250);
		configPopCostArgs.add(250);
		configPopCostArgs.add(205);
		
		configure(configPopNamesArgs, configPopCostArgs);
		
		//load(1, 1, 2, 0; 1, 1, 1)
		loadCoinCounts.add(1);
		loadCoinCounts.add(1);
		loadCoinCounts.add(2);
		loadCoinCounts.add(0);
		
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		
		load(loadCoinCounts, loadPopCounts);
		
		//insert(100)
		insert(100);
		
		//insert(100)
		insert(100);
		
		//insert(100)
		insert(100);
		
		//press(0)
		press(0);	
				
		//extract()
		extractObjOutput = extract(); 		
				
		//CHECK_DELIVERY(50, "Coke")
		extractExpectedChangeValue = 50; 				//not updating actual change before calling helper funciton might mess up future tests
		extractExpectedStringOutput.add("Coke");
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
				
		//unload()
		vmsc = unload();
				
		//CHECK_TEARDOWN(315; 0; "water", "stuff")
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 315;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		
		expectedPaymentCoinsInStorageBin = 0;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);
		
		
		//checking List<List<PopCan>> unsoldPopCans 					
				
		expectedUnsoldPopCans.add("water");
		expectedUnsoldPopCans.add("stuff");
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
		
	}

	@Test
	public void T03() throws DisabledException {
	//		construct(5, 10, 25, 100; 3; 10; 10; 10)
		constructCoinArgs.add(5);
		constructCoinArgs.add(10);
		constructCoinArgs.add(25);
		constructCoinArgs.add(100);
		
		selButtCount = 3;
		coinRackCap = 10;
		popCanRackCap = 10;
		receptCap = 10;
		
		construct(constructCoinArgs, selButtCount, coinRackCap, popCanRackCap, receptCap);
			
	//		extract()
		extractObjOutput = extract(); 		

			
	//		CHECK_DELIVERY(0)
		extractExpectedChangeValue = 0; 				
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
			
	//		unload()
		vmsc = unload();

			
	//		CHECK_TEARDOWN(0; 0)
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 0;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		
		expectedPaymentCoinsInStorageBin = 0;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);
		
		
		//checking List<List<PopCan>> unsoldPopCans 					
				
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
	}
	
	@Test
	public void T04() throws DisabledException {
//		construct(5, 10, 25, 100; 3; 10; 10; 10)
		constructCoinArgs.add(5);
		constructCoinArgs.add(10);
		constructCoinArgs.add(25);
		constructCoinArgs.add(100);
		
		selButtCount = 3;
		coinRackCap = 10;
		popCanRackCap = 10;
		receptCap = 10;
		
		construct(constructCoinArgs, selButtCount, coinRackCap, popCanRackCap, receptCap);
		
//		configure("Coke", "water", "stuff"; 250, 250, 205)
		configPopNamesArgs.add("Coke");
		configPopNamesArgs.add("water");
		configPopNamesArgs.add("stuff");
		
		configPopCostArgs.add(250);
		configPopCostArgs.add(250);
		configPopCostArgs.add(205);
		
		configure(configPopNamesArgs, configPopCostArgs);
		

//		load(1, 1, 2, 0; 1, 1, 1)
		loadCoinCounts.add(1);
		loadCoinCounts.add(1);
		loadCoinCounts.add(2);
		loadCoinCounts.add(0);
		
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		
		load(loadCoinCounts, loadPopCounts);
		
//		press(0)
		press(0);
		
//		extract()
		extractObjOutput = extract(); 		

		
//		CHECK_DELIVERY(0)
		extractExpectedChangeValue = 0; 				//not updating actual change before calling helper funciton might mess up future tests
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		unload()
		vmsc = unload();
		
//		CHECK_TEARDOWN(65; 0; "Coke", "water", "stuff")
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 65;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		
		expectedPaymentCoinsInStorageBin = 0;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);
		
		
		//checking List<List<PopCan>> unsoldPopCans 					
				
		expectedUnsoldPopCans.add("Coke");
		expectedUnsoldPopCans.add("water");
		expectedUnsoldPopCans.add("stuff");
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
	}
	
	@Test
	public void T05() throws DisabledException {
//		construct(100, 5, 25, 10; 3; 2; 10; 10)
		//construct(5, 10, 25, 100; 3; 10; 10; 10)
		constructCoinArgs.add(100);
		constructCoinArgs.add(5);
		constructCoinArgs.add(25);
		constructCoinArgs.add(10);
		
		selButtCount = 3;
		coinRackCap = 2;
		popCanRackCap = 10;
		receptCap = 10;
		
		construct(constructCoinArgs, selButtCount, coinRackCap, popCanRackCap, receptCap);
		
//		configure("Coke", "water", "stuff"; 250, 250, 205)
		configPopNamesArgs.add("Coke");
		configPopNamesArgs.add("water");
		configPopNamesArgs.add("stuff");
		
		configPopCostArgs.add(250);
		configPopCostArgs.add(250);
		configPopCostArgs.add(205);
		
		configure(configPopNamesArgs, configPopCostArgs);
		
//		load(0, 1, 2, 1; 1, 1, 1)
		loadCoinCounts.add(0);
		loadCoinCounts.add(1);
		loadCoinCounts.add(2);
		loadCoinCounts.add(1);
		
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		
		load(loadCoinCounts, loadPopCounts);
		
//		press(0)
		press(0);
		
//		extract()
		extractObjOutput = extract(); 		

//		CHECK_DELIVERY(0)
		extractExpectedChangeValue = 0; 				//not updating actual change before calling helper funciton might mess up future tests
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		insert(100)
		insert(100);
		
//		insert(100)
		insert(100);
		
//		insert(100)
		insert(100);
		
//		press(0)
		press(0);
		
//		extract()
		extractObjOutput = extract(); 		
		
//		CHECK_DELIVERY(50, "Coke")
		extractExpectedChangeValue = 50; 				//not updating actual change before calling helper funciton might mess up future tests
		extractExpectedStringOutput.add("Coke");
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		unload()
		vmsc = unload();
		
//		CHECK_TEARDOWN(215; 100; "water", "stuff")
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 215;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		
		expectedPaymentCoinsInStorageBin = 100;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);
		
		//checking List<List<PopCan>> unsoldPopCans 					
		expectedUnsoldPopCans.add("water");
		expectedUnsoldPopCans.add("stuff");
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
	}
	
	@Test
	public void T06() throws DisabledException {
		
//		construct(100, 5, 25, 10; 3; 10; 10; 10)
		constructCoinArgs.add(100);
		constructCoinArgs.add(5);
		constructCoinArgs.add(25);
		constructCoinArgs.add(10);
		
		selButtCount = 3;
		coinRackCap = 10;
		popCanRackCap = 10;
		receptCap = 10;
		
		construct(constructCoinArgs, selButtCount, coinRackCap, popCanRackCap, receptCap);
		
//		configure("Coke", "water", "stuff"; 250, 250, 205)
		configPopNamesArgs.add("Coke");
		configPopNamesArgs.add("water");
		configPopNamesArgs.add("stuff");
		
		configPopCostArgs.add(250);
		configPopCostArgs.add(250);
		configPopCostArgs.add(205);
		
		configure(configPopNamesArgs, configPopCostArgs);
		
//		load(0, 1, 2, 1; 1, 1, 1)
		loadCoinCounts.add(0);
		loadCoinCounts.add(1);
		loadCoinCounts.add(2);
		loadCoinCounts.add(1);
		
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		
		load(loadCoinCounts, loadPopCounts);
		
//		press(0)
		press(0);
		
//		extract()
		extractObjOutput = extract(); 		
		
//		CHECK_DELIVERY(0)
		extractExpectedChangeValue = 0; 				
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		insert(100)
		insert(100);
		
//		insert(100)
		insert(100);		
		
//		insert(100)
		insert(100);
		
//		extract()
		extractObjOutput = extract(); 		
		
//		CHECK_DELIVERY(0)
		extractExpectedChangeValue = 0; 				
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		unload()
		vmsc = unload();
		
//		CHECK_TEARDOWN(65; 0; "Coke", "water", "stuff")
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 65;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		
		expectedPaymentCoinsInStorageBin = 0;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);

		//checking List<List<PopCan>> unsoldPopCans 					
			
		expectedUnsoldPopCans.add("Coke");
		expectedUnsoldPopCans.add("water");
		expectedUnsoldPopCans.add("stuff");
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
	}
	
	@Test
	public void T07() throws DisabledException {
//		construct(5, 10, 25, 100; 3; 10; 10; 10)
		constructCoinArgs.clear();
		constructCoinArgs.add(5);
		constructCoinArgs.add(10);
		constructCoinArgs.add(25);
		constructCoinArgs.add(100);
		
		selButtCount = 3;
		coinRackCap = 10;
		popCanRackCap = 10;
		receptCap = 10;
		
		construct(constructCoinArgs, selButtCount, coinRackCap, popCanRackCap, receptCap);

//		configure("A", "B", "C"; 5, 10, 25)
		configPopNamesArgs.clear();
		configPopCostArgs.clear();
		configPopNamesArgs.add("A");
		configPopNamesArgs.add("B");
		configPopNamesArgs.add("C");
		
		configPopCostArgs.add(5);
		configPopCostArgs.add(10);
		configPopCostArgs.add(25);
		
		configure(configPopNamesArgs, configPopCostArgs);
		
//		load(1, 1, 2, 0; 1, 1, 1)
		loadCoinCounts.clear();
		loadPopCounts.clear();
		loadCoinCounts.add(1);
		loadCoinCounts.add(1);
		loadCoinCounts.add(2);
		loadCoinCounts.add(0);
		
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		
		load(loadCoinCounts, loadPopCounts);
		
//		configure("Coke", "water", "stuff"; 250, 250, 205)
		configPopNamesArgs.clear();
		configPopCostArgs.clear();
		configPopNamesArgs.add("Coke");
		configPopNamesArgs.add("water");
		configPopNamesArgs.add("stuff");
		
		configPopCostArgs.add(250);
		configPopCostArgs.add(250);
		configPopCostArgs.add(205);
		
		configure(configPopNamesArgs, configPopCostArgs);

//		press(0)
		press(0);
		
//		extract()
		extractObjOutput = extract(); 		

//		CHECK_DELIVERY(0)
		extractExpectedChangeValue = 0; 				
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		insert(100)
		insert(100);
		
//		insert(100)
		insert(100);
		
//		insert(100)
		insert(100);
		
//		press(0)
		press(0);

//		extract()
		extractObjOutput = extract(); 		
		
//		CHECK_DELIVERY(50, "A")
		extractExpectedStringOutput.clear();
		extractExpectedChangeValue = 50; 				
		extractExpectedStringOutput.add("A");
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		unload()
		vmsc = unload();

//		CHECK_TEARDOWN(315; 0; "B", "C")
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 315;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		expectedPaymentCoinsInStorageBin = 0;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);

		//checking List<List<PopCan>> unsoldPopCans 					
		expectedUnsoldPopCans.clear();
		expectedUnsoldPopCans.add("B");
		expectedUnsoldPopCans.add("C");
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
		
//		load(1, 1, 2, 0; 1, 1, 1)
		loadCoinCounts.clear();
		loadPopCounts.clear();
		loadCoinCounts.add(1);
		loadCoinCounts.add(1);
		loadCoinCounts.add(2);
		loadCoinCounts.add(0);
		
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		loadPopCounts.add(1);
		
		load(loadCoinCounts, loadPopCounts);
		
//		insert(100)
		insert(100);
		
//		insert(100)
		insert(100);
		
//		insert(100)
		insert(100);
		
//		press(0)
		press(0);
		
//		extract()
		extractObjOutput = extract(); 		

//		CHECK_DELIVERY(50, "Coke")
		extractExpectedChangeValue = 50; 				
		extractExpectedStringOutput.add("Coke");
		extractTestHelper();	
		
		//check if change value is correct
		assertEquals(extractExpectedChangeValue, extractActualChangeValue);
		//check if pop names are correct
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
//		unload()
		vmsc = unload();

//		CHECK_TEARDOWN(315; 0; "water", "stuff")
		//checking List<List<Coin>> unusedCoinsForChange 
		expectedChangeSum = 315;
		unloadUnusedCoinsHelper();
		
		//check if the sum of the unused change is correct
		assertEquals(expectedChangeSum, actualChangeSum);
		
		
		expectedPaymentCoinsInStorageBin = 0;
		unloadPaymentCoinsInStorageBinHelper();

		//check if the sum of the Coins in the storagebin is correct
		assertEquals(expectedPaymentCoinsInStorageBin, actualPaymentCoinsInStorageBin);

		//checking List<List<PopCan>> unsoldPopCans 					
		expectedUnsoldPopCans.add("water");
		expectedUnsoldPopCans.add("stuff");
		unloadUnsoldPopCansHelper();
		
		//check if the unsold PopCans is correct
		assertArrayEquals(expectedUnsoldPopCans.toArray(), actualUnsoldPopCans.toArray());
		
	}
	
	@Test
	public void T08() throws DisabledException {
		
		
	}
	
	@Test
	public void T09() throws DisabledException {
		
		
	}
	
	@Test
	public void T10() throws DisabledException {
		
		
	}
	
	@Test
	public void T11() throws DisabledException {
		
		
	}
	
	@Test
	public void T12() throws DisabledException {
		
		
	}
	
	@Test
	public void T13() throws DisabledException {
		
		
	}
	
	
	/*	Test Helper Methods	*/

	//updates values of extractActualChangeValue and extractActualStringOutput for extract method
	public void extractTestHelper(){
		for (Object obj : extractObjOutput){ 
			if(obj.getClass().equals(PopCan.class)) 
				extractActualStringOutput.add(((PopCan) obj).getName());
			else if(obj.getClass().equals(Coin.class))
				extractActualChangeValue += ((Coin) obj).getValue();
		}
	}
	
	//updates the value of actualChangeSum for unload method
	public void unloadUnusedCoinsHelper(){
		for (List<Coin> llCoin : vmsc.unusedCoinsForChange){
			
			for (Coin c : llCoin){
				actualChangeSum += c.getValue();
			}		
		}
	}

	//updates the value of actualPaymentCoinsInStorageBin for unload method
	public void unloadPaymentCoinsInStorageBinHelper(){
		for (Coin c : vmsc.paymentCoinsInStorageBin){
			actualPaymentCoinsInStorageBin += c.getValue();
		}
	}
	
	//updates the value of actualUnsoldPopCans for unload method 
	public void unloadUnsoldPopCansHelper(){

		for (List<PopCan> lpc : vmsc.unsoldPopCans){
			if (lpc.size() > 0){						//TODO: might need to change this to an inner loop to add each pop
				actualUnsoldPopCans.add(lpc.get(0).getName());		//add first element's name
			}
		}
	}
}
