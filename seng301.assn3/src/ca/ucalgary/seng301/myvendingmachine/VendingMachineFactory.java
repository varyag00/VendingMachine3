package ca.ucalgary.seng301.myvendingmachine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.ucalgary.seng301.vendingmachine.Coin;
import ca.ucalgary.seng301.vendingmachine.IVendingMachineFactory;
import ca.ucalgary.seng301.vendingmachine.PopCan;
import ca.ucalgary.seng301.vendingmachine.VendingMachineStoredContents;
import ca.ucalgary.seng301.vendingmachine.hardware.CoinRack;
import ca.ucalgary.seng301.vendingmachine.hardware.DisabledException;
import ca.ucalgary.seng301.vendingmachine.hardware.PopCanRack;
import ca.ucalgary.seng301.vendingmachine.hardware.VendingMachine;
import ca.ucalgary.seng301.vendingmachine.parser.ParseException;
import ca.ucalgary.seng301.vendingmachine.parser.Parser;

/**
 * This class allows a set of scripts to be read in, executed, and evaluated for
 * correctness. It acts as the frontend of the vending machine system. The
 * backend is not yet implemented. For the methods in the class, each has a
 * "TODO" comment to indicate that you are to replace or to add its
 * implementation, with the exception of the constructor which you should
 * probably leave alone in your best interests.
 * 
 * <p>
 * The frontend includes a parser for the scripts that you do not need to worry
 * about. You have not been given its source code because it is a bit
 * complicated and not the point of the assignment.
 * 
 * <h2>Vending machines</h2>
 * 
 * Construction. The vending machines have a set of one or more selection
 * buttons and operate on a set of one or more coin kinds (these details cannot
 * be changed once the machine is constructed). Each coin kind has a unique,
 * positive integer value.
 * 
 * <p>
 * Configuration. Each of the selection buttons corresponds to a kind of pop.
 * The name of each kind of pop and its price can be specified (and changed).
 * Selection buttons can share the same name or the same price or both.
 * 
 * <p>
 * Loading and unloading. The machine can be loaded with a set of coins (for
 * change) and a set of pops (to be sold). These can be unloaded from the
 * machine at any time, along with any money that has been used to buy pops.
 * 
 * <p>
 * Purchasing. Purchasing occurs by inserting an appropriate number of coins
 * into the machine and pressing the appropriate button. If the value of the
 * coins is sufficient to pay for the pop, the pop is dispensed and any change
 * owing is returned. If the desired kind of pop is empty, nothing is returned
 * and the state does not change. If the cost is higher than the value entered,
 * nothing is returned and the state does not change. There is no coin return
 * button on this version of the machine.
 * 
 * <p>
 * Extracting pop and change. Pops and change are delivered to a delivery chute.
 * These need to be extracted explicitly, else they remain there, accumulating.
 * 
 * <P>
 * Checking. The contents extracted from the delivery chute and the contents
 * extracted from inside the machine can be checked against expectations. The
 * frontend is implemented to deal with this when the commands are issued to do
 * so.
 * 
 * <p>
 * Physical limits. This simulation does not need to be realistic, in that no
 * physical limits are imposed. An effectively boundless number of coins and
 * pops can exist, be moved around, and be stored.
 * 
 * <h2>The Scripts</h2>
 * 
 * Scripts support a simple language consisting of 9 commands.
 * 
 * <p>
 * In the syntax below, tokens are specified as surrounded by quotation marks
 * and as the special token kinds &lt;STRING&gt; and &lt;INTEGER&gt;. Zero or
 * more characters of whitespace (i.e., blank spaces, tabs, carriage returns,
 * etc.) can appear between tokens, as in Java. A &lt;STRING&gt; represents a
 * string literal, which is any sequence of characters surrounded by quotation
 * marks; a backslash is used as an escape character to permit certain special
 * characters to be used in a manner identical to Java. An &lt;INTEGER&gt;
 * represents an integer literal which is either the numeral 0 or any numeral in
 * the range 1-9 followed by zero or more numerals each in the range 0-9.
 *
 * <h3>The construct command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <p>
 * 
 * <pre>
 * "construct" "(" &lt;INTEGER&gt; { "," &lt;INTEGER&gt; } ";" &lt;INTEGER&gt; ";" &lt;INTEGER&gt; ";" &lt;INTEGER&gt; ";" &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * <p>
 * This command takes a sequence of comma-separated integers each representing a
 * valid coin kind; there must be at least one valid coin kind. The integer
 * represents the value of the coin kind; each coin kind must have a unique
 * value. Each value must be a positive integer. Four, semicolon-separated
 * integers after this list represent: (1) the number of selection buttons, (2)
 * the maximum capacity of each coin rack, (3) the maximum capacity of each pop
 * can rack, and (4) the maximum capacity of the coin receptacle, storage bin,
 * and delivery chute; each must be a positive integer.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The purpose of this command is to create a vending machine object with the
 * specified characteristics. The created vending machine remains current until
 * and unless another vending machine is created. It is an error to issue any
 * other command prior to construction of a vending machine.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1; 1; 1; 1) // 1 coin kind of value "1"; 1 selection button; each capacity is 1
 * </pre>
 * 
 * <pre>
 * construct(10, 1, 3; 5; 10; 20; 50) // 3 coin kinds of values "10", "1", "3" <b>in that order</b>; 5 selection buttons, etc.
 * </pre>
 * 
 * <pre>
 * construct(1, 3, 10; 5; 10; 20; 50) // 3 coin kinds of values "1", "3", "10" <b>in that order</b>; 5 selection buttons
 * </pre>
 * 
 * <pre>
 * construct(1 ; 1; 10; 20; 50) configure(" " ; 1)
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * construct() // error: no coin kinds, no selection buttons, no capacities
 * </pre>
 * 
 * <pre>
 * construct(0; 0) // error: coin values are not positive, selection button count is not positive; no capacities
 * </pre>
 * 
 * <pre>
 * construct(1 2 3; 4; 10; 20; 50) // error: commas missing
 * </pre>
 * 
 * <pre>
 * construct(1, 2, 3; 4; 10; 20; 50 // error: closing parenthesis missing
 * </pre>
 * 
 * <pre>
 * configure("" ; 100) construct(1; 1; 10; 20; 50) // command issue prior to first construct
 * </pre>
 * 
 * <h3>The configure command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "configure" "(" &lt;STRING&gt; { "," &lt;STRING&gt; } ";" &lt;INTEGER&gt; { "," &lt;INTEGER&gt;} ")"
 * </pre>
 * 
 * This command takes a sequence of comma-separated strings each of which is
 * surrounded by double quotation marks (e.g., "this is a string"). A semicolon
 * follows these strings, followed by a sequence of comma-separated integers.
 * There must be at least one string and one integer present. Each integer must
 * be positive.
 * 
 * <p>
 * <b>Purpose:</b>
 * 
 * <p>
 * The purpose of this command is to configure a vending machine with the names
 * of the products and the prices that correspond to each selection button. It
 * is an error if the number of strings or the number of integers specified is
 * not identical to the number of selection buttons specified in the most recent
 * construct command.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * configure(""; 1)
 * </pre>
 * 
 * <pre>
 * configure("", ""; 1, 1)
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * configure() // error: no strings
 * </pre>
 * 
 * <pre>
 * configure(a) // error: no quotation marks
 * </pre>
 * 
 * <pre>
 * construct(1; 1; 10; 20; 50) configure("", "") // error: too many strings
 * </pre>
 * 
 * <h3>The load command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "load" "(" &lt;INTEGER&gt; { "," &lt;INTEGER&gt; } ";" &lt;INTEGER&gt; { "," &lt;INTEGER&gt; } ")"
 * </pre>
 * 
 * This command takes a sequence of comma-separated integers; there must be at
 * least one of them. Then, a semicolon indicates the start of another sequence
 * of comma-separated integers. All these values must be non-negative. It is an
 * error if the number of integers in the first sequence is not identical to the
 * number of coin kinds for the current vending machine. It is an error if the
 * number of integers in the second sequence is not identical to the number of
 * selection buttons for the current vending machine.
 * 
 * <p>
 * <b>Purpose:</b>
 * 
 * <p>
 * The purpose of this command is to load the vending machine with a set of
 * coins for change and with a set of pop cans.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1; 10; 20; 50) load(0; 0) // 0 coins of value "1", 0 pops of kind 1
 * </pre>
 * 
 * <pre>
 * construct(3, 2, 1; 2; 10; 20; 50) load(1, 0, 0; 1, 1) // 1 coin of value "3", none of "2" and none of "1"; 1 pop of kind 1, 1 pop of kind 2
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * load(0) // error: two sets of sequences are required
 * </pre>
 * 
 * <pre>
 * load(-1; -1) // error: negative values are not allowed
 * </pre>
 * 
 * <pre>
 * construct(3, 2, 1; 2; 10; 20; 50) load(1, 0, 0; 1) // error: the quantity of pop kind 2 is not specified
 * </pre>
 * 
 * <h3>The unload command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "unload" "(" ")"
 * </pre>
 * 
 * <p>
 * <b>Purpose:</b>
 * 
 * <p>
 * This command causes the total value of remaining unused coins, total value of
 * payment coins, and individual names of unsold pops to be unloaded from the
 * interior of the machine (for checking).
 * 
 * <h3>The extract command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "extract" "(" ")"
 * </pre>
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * This command causes the current contents of the delivery chute to be removed
 * from the vending machine (for checking).
 * 
 * <h3>The insert command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "insert" "(" &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * An error will occur if the integer is not positive. The coin will immediately
 * be deposited in the delivery chute if its value does not correspond to a coin
 * kind supported by the current vending machine.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The purpose of this command is to insert a coin of the specified value into
 * the machine.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1; 10; 20; 50) insert(1)
 * </pre>
 * 
 * <pre>
 * construct(5; 1; 10; 20; 50) insert(1) // the coin is immediately returned
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * insert(0) // error: non-positive coin
 * </pre>
 * 
 * <pre>
 * insert() // error: no coin
 * </pre>
 * 
 * <h3>The press command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "press" "(" &lt;INTEGER&gt; ")"
 * </pre>
 * 
 * The integer has to be non-negative. It is an error if the integer is greater
 * than or equal to the number of pop selection buttons for the current vending
 * machine.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The purpose of this command is to simulate the press of a pop selection
 * button.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * construct(1; 1; 10; 20; 50) press(0)
 * </pre>
 * 
 * <p>
 * <b>Incorrect examples:</b>
 * 
 * <pre>
 * press() // Error: no button indicated
 * </pre>
 * 
 * <pre>
 * press(-1) // Error: number is negative
 * </pre>
 * 
 * <pre>
 * construct(1 ; 1; 10; 20; 50) press(1) // Error: button number is out of range
 * </pre>
 * 
 * <h3>The CHECK_DELIVERY command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "CHECK_DELIVERY" "(" &lt;INTEGER&gt; { "," &lt;STRING&gt; } ")"
 * </pre>
 * 
 * This command is used to check whether your vending machine behaves as
 * expected. This command does not communicate with your vending machine, but
 * checks whether it has already delivered what is expected.
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * The integer indicates the expected total value of all coins delivered (for
 * example, as change). The sequence of strings indicates the kinds of pop that
 * are expected to have been delivered.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * CHECK_DELIVERY(0)
 * </pre>
 * 
 * <pre>
 * CHECK_DELIVERY(0, &quot;Coke&quot;)
 * </pre>
 * 
 * <h3>The CHECK_TEARDOWN command</h3>
 * 
 * <p>
 * <b>Syntax:</b>
 * 
 * <pre>
 * "CHECK_TEARDOWN" "(" &lt;INTEGER&gt; ";" &lt;INTEGER&gt; [ ";" &lt;STRING&gt; { "," &lt;STRING&gt; } ] ")"
 * </pre>
 * 
 * <p>
 * <b>Purpose:</b>
 * <p>
 * This command is used to check what is still inside the vending machine. The
 * first number indicates the total value of the change still remaining for use.
 * The second number indicates the total value of the coins entered as payment.
 * The two numbers are separated by a semicolon. A sequence of comma-separated
 * strings can follow these numbers. If present, it is separated by a semicolon.
 * Each string represents a kind of pop that is expected to be found.
 * 
 * <p>
 * <b>Correct examples:</b>
 * 
 * <pre>
 * CHECK_TEARDOWN(0; 0; "Coke", "Water")
 * </pre>
 * 
 * <pre>
 * CHECK_TEARDOWN(1; 100)
 * </pre>
 */
public class VendingMachineFactory implements IVendingMachineFactory {
    private VendingMachine vm = null;

/* TODO:
 * 1. eliminate the ____VendingMachineFactory____ and the ____entire parser package____ from the Assignment 2 solution, 
 * in such a way that the remainder works properly when the test suite drives it 
 * (i.e., your test cases will interact directly with my hardware classes)
 * 
 * NOTE: Your JUnit tests should install the logic in the hardware and then all interaction should take place with the hardware.  
 * In other words, after installing the logic, your JUnit code won't touch it again.
 * 
 * 	"So we are in fact instantiating the hardware in the "construct" JUnit method as opposed to creating a "setUp" method at the start of the JUnit class.

	That is the only way that I can think of satisfying the multiple arguments for the "VendingMachine". 
	If we were to instantiate the hardware in the "setUp" method we have nothing to pass as arguments."
 * 
 * 
 * THIS REGION WILL BE UNNECESSARY WHEN REMOVING VENDINGMACHINEFACTORY */
    public static void main(String[] args) throws ParseException, FileNotFoundException {
	int count = 0;
	String[] goodScripts =
	    {"T01-good-insert-and-press-exact-change", "T02-good-insert-and-press-change-expected", "T03-good-teardown-without-configure-or-load",
	     "T04-good-press-without-insert", "T05-good-scrambled-coin-kinds", "T06-good-extract-before-sale", "T07-good-changing-configuration",
	     "T08-good-approximate-change", "T09-good-hard-for-change", "T10-good-invalid-coin", "T11-good-extract-before-sale-complex",
	     "T12-good-approximate-change-with-credit", "T13-good-need-to-store-payment"};

	for(String script : goodScripts)
	    try {
		count++;
		new VendingMachineFactory(script);
	    }
	    catch(RuntimeException | DisabledException t) {
		t.printStackTrace();
		System.err.println();
	    }

	String[] badScripts =
	    {"U01-bad-configure-before-construct", "U02-bad-costs-list", "U03-bad-names-list", "U04-bad-non-unique-denomination", "U05-bad-coin-kind",
	     "U06-bad-button-number", "U07-bad-empty-name"};

	for(String script : badScripts)
	    try {
		count++;
		new VendingMachineFactory(script);
	    }
	    catch(RuntimeException | DisabledException t) {
		t.printStackTrace();
		System.err.println();
	    }

	System.err.println(count + " scripts executed");
    }

    public VendingMachineFactory(String path) throws ParseException, FileNotFoundException, DisabledException {
	Parser p = new Parser(new FileReader(path));
	p.register(this);
	p.setDebug(true);
	p.process(path);
    }

    @Override
    public List<Object> extract() {
	return Arrays.asList(vm.getDeliveryChute().removeItems());
    }

    @Override
    public void insert(int value) throws DisabledException {
	vm.getCoinSlot().addCoin(new Coin(value));
    }

    @Override
    public void press(int value) {
	vm.getSelectionButton(value).press();
    }

    @Override
    public void construct(List<Integer> coinKinds, int selectionButtonCount, int coinRackCapacity, int popCanRackCapacity, int receptacleCapacity) {
	int[] ck = new int[coinKinds.size()];
	int i = 0;
	for(Integer coinKind : coinKinds)
	    ck[i++] = coinKind;
	vm = new VendingMachine(ck, selectionButtonCount, coinRackCapacity, popCanRackCapacity, receptacleCapacity);
	new VendingMachineLogic(vm);
    }

    @Override
    public void configure(List<String> popNames, List<Integer> popCosts) {
	vm.configure(popNames, popCosts);
    }

    @Override
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

    @Override
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
}
