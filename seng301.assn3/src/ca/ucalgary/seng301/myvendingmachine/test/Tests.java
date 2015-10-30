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
	VendingMachineLogic vml; 								//TODO: do what you gotta do with the logic
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
		
		/*	extract()	*/									//TODO: not finished, format is [<change value>, <pops>] FIND OUT
		List<Object> extractObjOutput = extract(); 		
		for (Object obj : extractObjOutput){
			if(obj.toString() != null)
				extractActualStringOutput.add(((PopCan) obj).getName());
		}

		/*	CHECK_DELIVERY(0, "Coke")	*/
		//TODO int return value
		
		extractExpectedStringOutput.add("Coke");
		assertArrayEquals(extractExpectedStringOutput.toArray(), extractActualStringOutput.toArray());
		
		/*	unload()	*/
		vmsc = unload();
		
		/*	CHECK_TEARDOWN(315; 0; "water", "stuff")	*/
		//must check 3 things: 
		
		//List<List<Coin>> unusedCoinsForChange 
		
		//EXAMPLE: {{25,25,25}, {100,100}}
		List<List<Integer>> expectedUnusedCoinsForChange = new ArrayList<List<Integer>>();
		List<List<Integer>> actualUnusedCoinsForChange = new ArrayList<List<Integer>>();
		
		//get actual
//		for (List<Coin> llCoin : vmsc.unusedCoinsForChange){
//			//create new List<Coin>
//			List<Integer> temp = new ArrayList<Integer>();
//			for (Coin c : llCoin){
//				temp.add(c.getValue());
//			}
////			if (temp.size() == 0)
////				temp.add(0);
//			
//			actualUnusedCoinsForChange.add(temp);
//		}
//		
//		//create expected
//		List<Integer> coinVal = new ArrayList<Integer>();
//		coinVal.add(315);
//		expectedUnusedCoinsForChange.add(coinVal);
//		
//		//compare lists
//		List<Integer> actualList;
//		List<Integer> expectedList;
//		for (int i = 0; i < actualUnusedCoinsForChange.size(); i++){
//			actualList = actualUnusedCoinsForChange.get(i);
//			expectedList = expectedUnusedCoinsForChange.get(i);
//			assertArrayEquals(expectedList.toArray(), actualList.toArray());
//		}
		
		//have a sum
		
		int expectedSum = 315;
		int actualSum = 0;
		
		for (List<Coin> llCoin : vmsc.unusedCoinsForChange){
			
		for (Coin c : llCoin){
			actualSum += c.getValue();
		}
		
		assertEquals(expectedSum, actualSum);
	}
		
		
		//comparing List<Coin> paymentCoinsInStorageBin
		List<Integer> expectedPaymentCoinsInStorageBin = new ArrayList<Integer>();
		List<Integer> actualPaymentCoinsInStorageBin = new ArrayList<Integer>();
		
		for (Coin c : vmsc.paymentCoinsInStorageBin){
			actualPaymentCoinsInStorageBin.add(c.getValue());
		}
		if (actualPaymentCoinsInStorageBin.size() == 0){
			actualPaymentCoinsInStorageBin.add(0);
		}
		
		expectedPaymentCoinsInStorageBin.add(0);
		
		assertArrayEquals(expectedPaymentCoinsInStorageBin.toArray(), actualPaymentCoinsInStorageBin.toArray());
		
		
		
		//List<List<PopCan>> unsoldPopCans
		
		//Ex
		List<List<PopCan>> expectedUnsoldPopCans;
		List<List<PopCan>> actualUnsoldPopCans;
		
		
	}

}
