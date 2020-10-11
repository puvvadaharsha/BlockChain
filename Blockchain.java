/*--------------------------------------------------------

1. Name / Date: Harsha Puvvada May 27, 2020

2. Java version used, if not the official version for the class:

I am using the most up to date java version.

3. Precise command-line compilation examples / instructions:

Make sure to have the gson jar file. If not program wont compile. 

One version of the JSON jar file here:
https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/

To Compile:
>> compile javac -cp "gson-2.8.2.jar": Blockchain.java

4. Precise examples / instructions to run this program:
Program will run automatically. Dont touch the windows or move away. It seems very finicky and causes issues if I move away.
It will automatically create the necessary JSON file as well.

Run the script below. I have provided the one I used in case yours doesnt work.

>> run script osascript myScript.scpt

Contents of script in case yours doesnt work:
tell application "Terminal"

   activate

   set targetWindow to 0

   do script "cd /Users/harshapuvvada/Desktop/Blockchain" in window 0

   do script "javac -cp \"gson-2.8.2.jar\" *.java" in window 0

   do script "java -cp \"gson-2.8.2.jar\": Blockchain 0" in window 0

   delay 0.3

   tell application "System Events" to keystroke "t" using command down

   do script "java -cp \"gson-2.8.2.jar\": Blockchain 1" in front window

   delay 0.3

   tell application "System Events" to keystroke "t" using command down

   do script "java -cp \"gson-2.8.2.jar\": Blockchain 2" in front window

end tell


5. List of files needed for running the program.

checklist-block.html
Blockchain.java
BlockchainLog.txt
BlockchainLedgerSample.json
BlockInput0.txt, BlockInput1.txt, BlockInput2.txt
gson-2.8.2.jar

5. Notes:
I have a bug that sometimes theres an error in the public key server section but it does not affect the program. This happens very rarely only when i move away
from the window screen.
To stop the program, please use Ctrl + C. I did not have time to implement a way to kill the program from the console. 

6. Sources used:
Professor Clark Elliott's helper code on the course website.

https://mkyong.com/java/how-to-parse-json-with-gson/
http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
https://www.mkyong.com/java/java-digital-signatures-example/ (not so clear)
https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
https://www.programcreek.com/java-api-examples/index.php?api=java.security.SecureRandom
https://www.mkyong.com/java/java-sha-hashing-example/
https://stackoverflow.com/questions/19818550/java-retrieve-the-actual-value-of-the-public-key-from-the-keypair-object
https://www.java67.com/2014/10/how-to-pad-numbers-with-leading-zeroes-in-Java-example.html
https://www.quickprogrammingtips.com/java/how-to-generate-sha256-hash-in-java.html
https://dzone.com/articles/generate-random-alpha-numeric
http://www.javacodex.com/Concurrency/PriorityBlockingQueue-Example

----------------------------------------------------------*/

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import com.google.gson.*;

class Blockchain {

    public static int pnum; // stores process number
    public static int publicKeyPort; // receives public keys
    public static int unverifiedBlockPort; // receives unferified blocks
    public static int blockChainPort; // receives updated blockchain
    public static String serverName = "localHost"; //connects to local host
    public static String inputFileName = ""; //stores the input file to create blocks from
    public static Gson gson = new Gson();  //creates a gson object to use for making things into JSON format
    public static Wallet wallet; //stores all the keys
    public static BlockBank bank; //stores all the blocks

    public static void main(String[] args) {
        // default to process 0. Setting up the process number based on console input
        if (args.length < 1) {
            pnum = 0;
        } else {
            switch (args[0]) {
                case "0":
                    pnum = 0;
                    break;
                case "1":
                    pnum = 1;
                    break;
                case "2":
                    pnum = 2;
                    break;
                default:
                    // if bad input defaults to process 0
                    pnum = 0;
                    break;
            }
        }

        //creates the ports numbers and the input file name based on the process number
        publicKeyPort = 4710 + pnum;
        unverifiedBlockPort = 4820 + pnum;
        blockChainPort = 4930 + pnum;
        inputFileName = "BlockInput" + pnum + ".txt";

        //prints out a summary of the port numbers and the input file name
        System.out.println(String.format("\n++++++++++++++++++++++++++\nProcess number %d running.", pnum));
        System.out.println(String.format(
                "Public Key Port: %d\nUnverified Block Port:%d\nBlock Chain Port: %d\n++++++++++++++++++++++++++\n",
                publicKeyPort, unverifiedBlockPort, blockChainPort));

        // ###############################################################################
        // Generate public and private key pair for this process.
        try {
            wallet = new Wallet(new Random().nextLong()); //takes in a random seed to kick start the process
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create thread for listening for public keys
        PublicKeyLoop PL = new PublicKeyLoop();
        Thread t = new Thread(PL);
        t.start();

        sendPublicKeysOut();//triggers and sends the keys to the other two processes.

        // sleep while process does not have all three keys
        while (wallet.PublicKeyDict.size() < 3) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException x) {
                System.out.println("Main thread sleep failed");
                x.printStackTrace();
            }
        }
        wallet.printPublicKeys(); //print all the public keys of all three processes
        // sleep for a little bit so that all the processes have all the keys
        try {
            Thread.sleep(1000);
        } catch (InterruptedException x) {
            System.out.println("Main thread sleep failed");
            x.printStackTrace();
        }
        System.out.println("\nI have gotten all three keys!\n");

        // ###############################################################################
        // create a bank object to read the file and create unverified blocks and send
        // them over to the other processes.
        bank = new BlockBank();

        //give input file name to bank to read and create blocks from
        try {
            bank.readFile(inputFileName);
        } catch (Exception e) {
            System.out.println("Reading input file failed.");
            e.printStackTrace();
        }

        //print out the list of unverified blocks
        bank.printMyUnverifiedBlocks();
        bank.printUnverifiedPriorityQueue();//print out the sorted list of blocks

        // create loop to listen for unverified blocks
        UnverifiedBlockLoop UBL = new UnverifiedBlockLoop();
        Thread t1 = new Thread(UBL);
        t1.start();

        //sleep for a little so all three servers are up
        try {Thread.sleep(1000);} catch (Exception e) {} 

        //send this processes unverified blocks over to the others
        sendUnverifiedBlocks();

        try {Thread.sleep(5000);} catch (Exception e) {} // make sure all blocks are in.
        bank.printAllUnverifiedBlocks(); //print the full list of all blocks

        // ###############################################################################
        // start competing with other processes to verify and create blockchain.
        System.out.println("\n\n####################################################################################\n");

        //start listening for blockchains incoming
        BlockchainServer bs = new BlockchainServer();
        Thread t2 = new Thread(bs);
        t2.start();

        //make sure all the blockchain servers are up and running. Stagger them cos process 0 starts much faster than the others
        //this ensures they start at around the same time
        try {
            if (pnum == 0) {
                Thread.sleep(4000);
            } else if (pnum == 1) {
                Thread.sleep(2000);
            } else {
                Thread.sleep(0);
            }
        } catch (InterruptedException x) {
            System.out.println("Sending unverified blocks sleep failed");
            x.printStackTrace();
        }

        //start verifying the unverified blocks
        new Thread(new VerificationWorker(bank.allUnverifiedBlocks)).start();

        //sleep to make sure all the ledgers are updated
        try {Thread.sleep(2000);} catch (Exception e) {} 

        //write the final update ledger if process 0
        if(pnum == 0){
            writeToFile();
        }
    }

    //this method makes a gson object that beautifies the JSON so its more readable and then writes it to file.
    public static void writeToFile(){
        Gson pretty = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("BlockchainLedger.json")) {
            pretty.toJson(VerificationWorker.currentLedger, writer);
        }catch (IOException e) {e.printStackTrace();}
    }

    //This method sends all the unverified blocks over.
    private static void sendUnverifiedBlocks() {
        System.out.println("Sending my unverified blocks to the other processes.");

        //store the port numbers for listening to unverified blocks
        ArrayList<Integer> ports = new ArrayList<Integer>(Arrays.asList(4820, 4821, 4822));
        ports.remove(pnum); // so that it wont send unverified blocks to itself

        //this part is just to stagger than a little
        for (int i : ports) {
            // sleep statements so that they dont all send at once
            try {
                if (pnum == 0) {
                    Thread.sleep(0);
                } else if (pnum == 1) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(2000);
                }
            } catch (InterruptedException x) {
                System.out.println("Sending unverified blocks sleep failed");
                x.printStackTrace();
            }

            try {
                // create a new socket with other process listener
                Thread.sleep(2500);
                Socket sock = new Socket(serverName, i);
                PrintStream toServer;
                try {
                    // create input stream with process
                    toServer = new PrintStream(sock.getOutputStream());

                    // send public key in JSON format to process
                    toServer.println(bank.getUnverifiedBlocksInJSON());
                    toServer.flush();
                    toServer.close();
                    sock.close(); //close once its done

                } catch (Exception x) {
                    System.out.println("error in sendUnverifiedBlocks()");
                    x.printStackTrace();
                }
            } catch (Exception c) {
                System.out.println("Failed to send Unverified Blocks to process");
                c.printStackTrace();
            }
        }
    }
    //This method sends out the public keys to the other processes  
    private static void sendPublicKeysOut() {
        System.out.println("Sending my public key to the other processes.");

        //create an array to hold the public key ports
        ArrayList<Integer> ports = new ArrayList<Integer>(Arrays.asList(4710, 4711, 4712));
        ports.remove(pnum); // so that it wont send a public key to itself

        for (int i : ports) {
            // sleep statements so that they dont all send at once
            try {
                if (pnum == 0) {
                    Thread.sleep(0);
                } else if (pnum == 1) {
                    Thread.sleep(1000);
                } else {
                    Thread.sleep(2000);
                }
            } catch (InterruptedException x) {
                System.out.println("Sending keys sleep failed");
                x.printStackTrace();
            }

            try {

                // create a new socket with other process listener
                Thread.sleep(1000);
                Socket sock = new Socket(serverName, i);
                PrintStream toServer;

                try {
                    // create input stream with process
                    toServer = new PrintStream(sock.getOutputStream());

                    // send public key in JSON format to process
                    toServer.println(wallet.getPublicKeyJSON());
                    toServer.flush();
                    toServer.close();
                    sock.close(); //close when done

                } catch (Exception x) {
                    System.out.println("error in sendPublicKey()");
                    x.printStackTrace();
                }
            } catch (Exception c) {
                System.out.println("Failed to send public key to process");
                c.printStackTrace();
            }
        }
    }
}

//this class verfies blocks and sends the updated ledger to the other processes
class VerificationWorker extends Thread {
    PriorityBlockingQueue<BlockRecord> allBlocks; //stores all the unverified blocks
    public static  LinkedBlockingQueue<BlockRecord> currentLedger = new  LinkedBlockingQueue<BlockRecord>(); //this stores the most updated ledger
    public static ArrayList<Integer> ports = new ArrayList<Integer>(Arrays.asList(4930, 4931, 4932)); //create an array to hold the ports for listening to blocks

    public BlockRecord top; //this is the block that the process is working on now.

    //constructor that takes in the list of unverified blocks and removes itself of the port array so that it wont send it to itself
    public VerificationWorker(PriorityBlockingQueue<BlockRecord> pq) {
        allBlocks = new PriorityBlockingQueue<BlockRecord>(pq);
        ports.remove(Blockchain.pnum);
        top = allBlocks.poll(); //take the first unverified block of the queue
    }

    //this method takes out the next block in line and prints a summary about that block
    public void setToNextBlock(){
        if(allBlocks.iterator().hasNext()){
            top = allBlocks.poll();
            System.out.print("\nMoving to next Block\n");
            top.printBlock();
        }
    }

    //prints the current blockchain ledger. It has the winning seed and the process that verified it.
    //This allows the user to see that all three processes compete to verify blocks
    public static void printLedger(){
        System.out.println("Printing current ledger");
        for(BlockRecord b:currentLedger){
            System.out.println(String.format("%s %s %s %s %s %s", b.getUUID(), b.getTimeStamp(), b.getFirstName(),
                b.getLastName(), b.getRandomSeed(), b.getVerificationProcessID()));
        }
    }

    //this method checks if that block is already in the ledger and if so, it move the current block to the next one for verification
    public boolean inLedger(String uuid) {
        //checks based on the unique id of each block
        for(BlockRecord b:currentLedger){
            if (uuid.equals(b.getUUID())) {
                setToNextBlock();
                return true;
            }
        }
        return false;
    }

    //this method creates a random string that will be used as the seed for guessing the answer to the puzzle
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder(); //use a stringbuilder to generate the string
        String choices = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; //These are the choices
        ;
        while (count-- != 0) { //count is the size of the random seed that the user chooses
            int character = (int) (Math.random() * choices.length()); //it gets the character at that position
            builder.append(choices.charAt(character)); //it appends selected character to the new string
        }
        return builder.toString();
    }

    //simple code to conver a byte array to hex values which is needed for verifiying if the puzzle is solved
    public static String byteToHex(byte[] input) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : input) {
            builder.append(String.format("%02x", b)); //this formats it into hex and iterates over the bytes in the array
        }
        return builder.toString();
    }

    public void run() {
        //keep looping as long as all 13 blocks arent verified
        while(currentLedger.size() != 13){
            //check if the current block is already in the ledger and if so it moves to the next block
            if (!inLedger(top.getUUID())) {
                
                System.out.print("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\nWorking on Block: ");
                top.printBlock(); //print the current block

                // Setup for verification the signed data
                byte[] signedBlockIDdata = Base64.getDecoder().decode(top.signedBlockID.getBytes());

                int creator = Integer.parseInt(top.getCreatorProcessID().split(" ")[1]); //the process number that signed it
                PublicKey pk = Blockchain.wallet.PublicKeyDict.get(creator); //get the public key of the process number that created it

                try {
                    // Verify signed block id and print if it was correct
                    System.out.println("Has the signed blockID been verified: " + Blockchain.wallet.verifySig(top.getUUID().getBytes(), pk, signedBlockIDdata));
                } catch (Exception e) {
                    System.out.println("Signed BlockID verification failed.");
                    e.printStackTrace();
                }

                int count = 0; // number of times work loop is executed
                String seed = ""; // stores the random seed
                String puzzleString = "";// stores the full string puzzle
                String puzzleHash = ""; // stores the hashed value of the puzzle
                int cutoff = 20000; // this will change the difficulty of the puzzle (higher is easier)

                // do the work of verifying each block locally first
                while (true) {

                    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();} //to allow the blockchain to update

                    if(currentLedger.size() > 13){
                        break; //if the ledger is larger than 13 stop verifying
                    }

                    // for every 3 tries check if block has been verified in blockchain due to any updates from other processes
                    if (count == 3) {
                        count = 0;
                        if (inLedger(top.getUUID())) {
                            break;
                        }
                    }
                    
                    // create a random seed of length 8
                    seed = randomAlphaNumeric(8);

                    // insert seed into unverified block
                    top.setRandomSeed(seed); 

                    // my puzzle string will be the previous winning hash + JSON string of the
                    // current block + RandomSeed
                    puzzleString = top.getPreviousHash() + Blockchain.gson.toJson(top).toString() + seed;

                    // hash the puzzlestring
                    byte[] bytesHash;

                    try {
                        //create a SHA256 hash of the puzzlestring and turn it into hex values
                        MessageDigest MD = MessageDigest.getInstance("SHA-256");
                        bytesHash = MD.digest(puzzleString.getBytes("UTF-8"));
                        puzzleHash = byteToHex(bytesHash); // Turn into a string of hex values
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // print the hex value and the decimal value of the first 4 digits
                    System.out.println("Puzzle Hash in hex is: " + puzzleHash);
                    int workNumber = Integer.parseInt(puzzleHash.substring(0, 4), 16); //get first 4 digits and parse it as unsigned integer
                    System.out.println(
                            "First 16 bits in Hex and Decimal: " + puzzleHash.substring(0, 4) + " and " + workNumber);

                    // if its greater than cutoff, then its not solved
                    if (workNumber > cutoff) { // lower number = more work.
                        System.out.format("%d is not less than %d so it did not solve the puzzle\n", workNumber,
                                cutoff);
                    }

                    // puzzle is verified and correct
                    if (workNumber < cutoff) {
                        //print the correct answer
                        System.out.format("%d is less than %d so puzzle solved!\n", workNumber, cutoff);
                        System.out.println("The seed (correct answer) was: " + seed);

                        // update the blockNum of block
                        top.setBlockNum(String.format("%d", currentLedger.size() + 1)); 

                        // set the winning puzzleHash into the block
                        top.setWinningHash(puzzleHash);

                        try {
                            //sign the winning hash with public key
                            byte[] digitalSignature = Blockchain.wallet.signData(puzzleHash.getBytes(),Blockchain.wallet.getPrivateKey());
                            top.setSignedSHA256Hash(Base64.getEncoder().encodeToString(digitalSignature)); //store it into the block
                        } catch (Exception e) {
                            System.out.println("Signing of winning hash failed.");  
                            e.printStackTrace();
                        }

                        //set the previous hash if not the dummy block this ensures theres a chain of previous hashes
                        if(currentLedger.size() >= 1){
                            BlockRecord previous = (BlockRecord) currentLedger.toArray()[currentLedger.size() - 1];
                            top.setPreviousHash(previous.getWinningHash());
                        }

                        //set the verification process number
                        top.setVerificationProcessID(String.format("Process %d",Blockchain.pnum));

                        //set the winning seed
                        top.setRandomSeed(seed);

                        //send the ledger to all the processes after checking this block is not in the ledger already
                        if (!inLedger(top.getUUID())){

                            //add this block to the ledger
                            currentLedger.add(top);

                            //send updated ledger to the other processes
                            System.out.println("Sending my ledger to the other processes.");

                            // send blockChain in JSON format to process
                            String ledgerJson = Blockchain.gson.toJson(currentLedger);

                            //send to the other ports
                            for (int i : ports) {
                                try{
                                    Thread.sleep(1000); //sleep statements to stagger
                                    Socket sock = new Socket(Blockchain.serverName, i);
                                    PrintStream toServer = new PrintStream(sock.getOutputStream());
                                    toServer.println(ledgerJson); //send the JSON formatted ledger
                                    toServer.flush(); 
                                    sock.close();
                                }catch(Exception e) {
                                    System.out.println("Error sending current Ledger.");  
                                    e.printStackTrace();
                                }
                            }
                            break; //once the block is verified break
                        }
                    }
                    count++; //this counter keeps track of the number of verifications done 
                }
            }
        }
        System.out.println("\nDone Verifying all blocks. Final Blockchain Ledger:");
        printLedger(); //print out the final blockchain ledger
    }
}

// block record class to store data from the input files
class BlockRecord{
    String signedBlockID; // signed uuid
    String blockNum; // holds the position of the block in its chain
    String timeStamp; //when the block was created
    String creatorProcessID; // process number that created it
    String verificationProcessID; // process number that verifies this block
    String previousHash; // from the block before
    String uuid; // uuid
    String firstName;
    String lastName;
    String SSN;
    String dataOfBirth;
    String randomSeed; // guess and the final value will be the one that solves the puzzle
    String winningHash; //the answer to the puzzle
    String diagnosis; // diagnosis
    String treatment; // treatment
    String prescription; // medicine prescribed
    String signedSHA256Hash; //signed winning hash

    public String getSignedBlockID() {
        return signedBlockID;
    } // signed uuid

    public void setSignedBlockID(String id) {
        this.signedBlockID = id;
    }

    public String getBlockNum() {
        return blockNum;
    } // holds the position of the block in its chain

    public void setBlockNum(String num) {
        this.blockNum = num;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String ts) {
        this.timeStamp = ts;
    }

    public String getCreatorProcessID() {
        return creatorProcessID;
    } // this will store process number

    public void setCreatorProcessID(String cid) {
        this.creatorProcessID = cid;
    }

    public String getVerificationProcessID() {
        return verificationProcessID;
    } // this will store process number

    public void setVerificationProcessID(String vid) {
        this.verificationProcessID = vid;
    }

    public String getPreviousHash() {
        return this.previousHash;
    }

    public void setPreviousHash(String ph) {
        this.previousHash = ph;
    }

    public String getUUID() {
        return uuid;
    } // stores the UUID

    public void setUUID(String ud) {
        this.uuid = ud;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String ln) {
        this.lastName = ln;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String fn) {
        this.firstName = fn;
    }

    public String getSSN() {
        return SSN;
    }

    public void setSSN(String ss) {
        this.SSN = ss;
    }

    public String getDataOfBirth() {
        return dataOfBirth;
    }

    public void setDataOfBirth(String dob) {
        this.dataOfBirth = dob;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String d) {
        this.diagnosis = d;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String tr) {
        this.treatment = tr;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String p) {
        this.prescription = p;
    }

    public String getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(String rs) {
        this.randomSeed = rs;
    }

    public String getWinningHash() {
        return winningHash;
    }

    public void setWinningHash(String wh) {
        this.winningHash = wh;
    }

    public String getSignedSHA256Hash() {
        return signedSHA256Hash;
    }
    public void setSignedSHA256Hash(String signed) {
        this.signedSHA256Hash = signed;
    }

    //this prints some important information from the block
    public void printBlock() {
        System.out.println(String.format("%s %s %s %s", this.getUUID(), this.getTimeStamp(), this.getFirstName(),
                this.getLastName()));
    }
}

//this allows the priority queue to compare based on timestamp
class BlockRecordComparator implements Comparator<BlockRecord>{

    @Override
    public int compare(BlockRecord b1, BlockRecord b2) {
      //get their timestamps
      String s1 = b1.getTimeStamp(); 
      String s2 = b2.getTimeStamp();

      if (s1 == s2) {return 0;}
      if (s1 == null) {return -1;}
      if (s2 == null) {return 1;}
      return s1.compareTo(s2); //using a string compare return the position it should be in
    }
}

//stores the unverfied blocks
class BlockBank {
    LinkedList<BlockRecord> unverifiedBlockList; //this stores the blocks that come in from reading the input file unordered
    PriorityBlockingQueue<BlockRecord> unverifiedPriorityQueue; //ordered list of blocks from the input files
    PriorityBlockingQueue<BlockRecord> allUnverifiedBlocks; //ordered list of all blocks from all the processes

    String inputLine; //initialized for later

    //public constructor initializes the lists
    public BlockBank() {
        unverifiedBlockList = new LinkedList<BlockRecord>();
        unverifiedPriorityQueue = new PriorityBlockingQueue<BlockRecord>(20, new BlockRecordComparator());
        allUnverifiedBlocks = new PriorityBlockingQueue<BlockRecord>(20, new BlockRecordComparator());
    }

    //iterates over the queue and prints each block
    public void printPriorityQueue(PriorityBlockingQueue<BlockRecord> p){
        PriorityBlockingQueue<BlockRecord> copy = new PriorityBlockingQueue<BlockRecord>(p); //creates a copy since poll removes the blocks
        while(copy.size() > 0){
            BlockRecord b = copy.poll();
            b.printBlock();
        }
    }

    //converts the list to JSON format
    public String getUnverifiedBlocksInJSON(){
        return Blockchain.gson.toJson(unverifiedBlockList);
    }

    //method to read the input file
    public void readFile(String fileName) throws Exception {

        // create dummyblock in process 2
        if (Blockchain.pnum == 2) {
            BlockRecord dummyBlock = new BlockRecord();

            //get the current time
            long timestamp = new Date().getTime();
            //use calander object to get the current instance
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp); //set the time to milliseconds
            //get the time string and make sure to append the process number at the end
            String time = "0000-00-00.00:00:00:000";
            String TimeStampString = time + "." + Blockchain.pnum;
            dummyBlock.setTimeStamp(TimeStampString);

            // set verification process id number
            dummyBlock.setCreatorProcessID("Process " + Blockchain.pnum);
            // set uuid
            dummyBlock.setUUID(UUID.randomUUID().toString());
            // convert uuid to bytes to sign it with private key
            String stringUUID = dummyBlock.getUUID();
            byte[] digitalSignature = Blockchain.wallet.signData(stringUUID.getBytes(),Blockchain.wallet.getPrivateKey());
            dummyBlock.setSignedBlockID(Base64.getEncoder().encodeToString(digitalSignature));
            dummyBlock.setFirstName("Dummy");
            // set lastname
            dummyBlock.setLastName("Block");
            // set date of birth
            dummyBlock.setDataOfBirth("0000-00-00");
            // set SSN
            dummyBlock.setSSN("123-45-6789");
            // set diagnosis
            dummyBlock.setDiagnosis("Dummy");
            // set treatment
            dummyBlock.setTreatment("Nothing");
            // set prescription
            dummyBlock.setPrescription("Take Nothing");

            //set dummy previous proof of work
            dummyBlock.setPreviousHash("DummyPreviousHash123456789");

            // add the block to unverified linked list
            unverifiedBlockList.add(dummyBlock);
        }
        // read file and make unverified blocks from file data
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while ((inputLine = reader.readLine()) != null) {
                String[] allLines = inputLine.split("\n");

                //iterate over all the lines and splits it based on white space
                for (String line : allLines) {

                    String[] words = line.split("\\s+");

                    // create new block and fill up its fields
                    BlockRecord block = new BlockRecord();

                    // set timestamp
                    Thread.sleep(100); //stagger a little

                    ///get the current time
                    long timestamp = new Date().getTime();
                    //use calander object to get the current instance
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timestamp); //set the time to milliseconds
                    //get the time string and make sure to append the process number at the end
                    String time =new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss:SSS").format(cal.getTime());
                    String TimeStampString = time + "." + Blockchain.pnum;
                    block.setTimeStamp(TimeStampString);

                    // set verification process id number
                    block.setCreatorProcessID("Process " + Blockchain.pnum);

                    // set uuid
                    block.setUUID(UUID.randomUUID().toString());

                    // set block id which is the position of the block in the chain
                    // convert uuid to bytes to sign it with private key
                    String stringUUID = block.getUUID();
                    byte[] digitalSignature = Blockchain.wallet.signData(stringUUID.getBytes(),Blockchain.wallet.getPrivateKey());
                    block.setSignedBlockID(Base64.getEncoder().encodeToString(digitalSignature));

                    // set firstname
                    block.setFirstName(words[0]);

                    // set lastname
                    block.setLastName(words[1]);

                    // set date of birth
                    block.setDataOfBirth(words[2]);

                    // set SSN
                    block.setSSN(words[3]);

                    // set diagnosis
                    block.setDiagnosis(words[4]);

                    // set treatment
                    block.setTreatment(words[5]);

                    // set prescription
                    block.setPrescription(words[6]);

                    // add the block to unverified linked list
                    unverifiedBlockList.add(block);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //loop over the unverified block list and prints them
    public void printMyUnverifiedBlocks() {
        System.out.println("From input file:");
        System.out.println("####################################################################################\nNumber of blocks in unverified list: "
                + unverifiedBlockList.size());
        System.out.println("Blocks in Unverified List:");
        for (BlockRecord b : unverifiedBlockList) {
            b.printBlock();
        }
    }

    //loop over the unverfied list and adds them to the priority queue and then prints them out
    public void printUnverifiedPriorityQueue() {
        // shuffle the list
        System.out.println("\nShuffling Linked List of unverified blocks:");
        Collections.shuffle(unverifiedBlockList);

        // add blocks to priority queue
        for (BlockRecord b : unverifiedBlockList) {
            unverifiedPriorityQueue.add(b);
            b.printBlock();
        }
        // print blocks in priority queue
        System.out.println("\nPriority Queue Blocks in order by time: ");
        printPriorityQueue(unverifiedPriorityQueue);
        System.out.println("\n####################################################################################\n");
    }

    //handles all the JSON data coming in from other processes
    public void handleJSONData(String input){
        //JSON returns an array of blocks        
        BlockRecord[] temp = Blockchain.gson.fromJson(input, BlockRecord[].class);
        //convert back to BlockRecord object and add it to all unverified blocks priority queue
        for (BlockRecord b : temp) {
            allUnverifiedBlocks.add(b);
        }
    }

    //iterates and prints all the blocks from all processes
    public void printAllUnverifiedBlocks(){
        for (BlockRecord b : unverifiedPriorityQueue) {
            allUnverifiedBlocks.add(b); //adds it to the priority queue
        }
        System.out.println("All unverified blocks received. Printing all unverified blocks in order below:\n");
        printPriorityQueue(allUnverifiedBlocks);
    }
}

// store public and private key pair and has methods to easily get in different
// formats
class Wallet {
    KeyPairGenerator keyGenerator; //generates the keys
    KeyPair currentkeyPair; //the current key pair
    Map<Integer, PublicKey> PublicKeyDict; //holds the process number and the public key for that process

    //generates key pair on intialization based on some random seed
    public Wallet(long seed) throws Exception {
        System.out.println("Generating public and private key pair for this process.");
        // create key pair
        keyGenerator = KeyPairGenerator.getInstance("RSA"); //use RSA
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN"); //Use these secure random algorithms
        rng.setSeed(seed);
        keyGenerator.initialize(1024, rng);
        currentkeyPair = keyGenerator.generateKeyPair(); //generate the key pair
        System.out.println("my public key is:");
        System.out.println(this.getPublicKeyString()); //print out the public key for user to see

        // hashmap to store all the public keys
        PublicKeyDict = new HashMap<Integer, PublicKey>();

        // store the public key in the correct process number position
        PublicKeyDict.put(Blockchain.pnum, this.getPublicKey());
    }

    public KeyPair getKeyPair() {
        return currentkeyPair;
    }

    public PublicKey getPublicKey() {
        return currentkeyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return currentkeyPair.getPrivate();
    }

    //gets the public key in a byte array
    public byte[] getPublicKeyByte() {
        return currentkeyPair.getPublic().getEncoded();
    }

    //gets the private key in a byte array
    public byte[] getPrivateKeyByte() {
        return currentkeyPair.getPrivate().getEncoded();
    }

    //gets the public key in string form
    public String getPublicKeyString() {
        return Base64.getEncoder().encodeToString(this.getPublicKeyByte());
    }
    //gets the private key in string form
    public String getPrivateKeyString() {
        return Base64.getEncoder().encodeToString(this.getPrivateKeyByte());
    }
    //gets the public key in JSON form
    public String getPublicKeyJSON() {
        String[] output = new String[] { String.format("%d", Blockchain.pnum), this.getPublicKeyString() };
        return Blockchain.gson.toJson(output);
    }

    //converts back to a public key from JSON format
    public void convertBacktoPublicKey(String jsonString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] keys = Blockchain.gson.fromJson(jsonString, String[].class); //store an array of public key strings

        byte[] keyBytes = Base64.getDecoder().decode(keys[1]); //get the bytes array for the key based on its public key
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(new X509EncodedKeySpec(keyBytes)); //regenerate the public key according to the spec

        Blockchain.wallet.PublicKeyDict.put(Integer.parseInt(keys[0]), pk); //add the process number and key back to the hashmap
    }

    //iterate and list all public key in string format
    public void printPublicKeys() {
        for (int i : PublicKeyDict.keySet()) {
            System.out.println("For process: " + i);
            System.out.println(Base64.getEncoder().encodeToString(PublicKeyDict.get(i).getEncoded())); //convert to string format
        }
    }

    //method to sign a data. Takes in the data, private key 
    public byte[] signData(byte[] data, PrivateKey key) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA"); //sets the alogrihm used has to match the generation algorithm
        signer.initSign(key); //intialize the key
        signer.update(data);
        return (signer.sign()); //return the signed byte array
    }

    //verfies the authenticity of the signature using the data, the public key and the signed byte array
    public boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
        Signature signer = Signature.getInstance("SHA1withRSA");
        signer.initVerify(key);
        signer.update(data);
        return (signer.verify(sig));
    }
}

//this listens for any public key socket connections and spawns a thread to handle it
class PublicKeyLoop implements Runnable {
    public void run() {
        int q_len = 10;
        Socket sock;
        try {
            ServerSocket servsock = new ServerSocket(Blockchain.publicKeyPort, q_len);
            System.out.println("Public Key Listener is listening.");
            while (true) {
                sock = servsock.accept();
                new PublicKeyListener(sock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }    
}

class PublicKeyListener extends Thread{
    Socket sock; 
    public PublicKeyListener(Socket s){
        sock = s;
    }
    public void run() {
        //create input and output to connected client
        BufferedReader in = null;
        try {
            in = new BufferedReader (new InputStreamReader(sock.getInputStream()));
            try {
                String input;

                while ((input = in.readLine()) != null) {
                    //convert from JSON format back to PublicKey and store in wallet
                    Blockchain.wallet.convertBacktoPublicKey(input);
                }
            } catch (Exception e) {
                System.out.println("Read Error in Public key listener.");
                e.printStackTrace();
            }
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//this listens for any unverified block socket connections and spawns a thread to handle it
class UnverifiedBlockLoop implements Runnable {
    public void run() {
        int q_len = 10; //set queue length
        Socket sock;
        try {
            ServerSocket servsock = new ServerSocket(Blockchain.unverifiedBlockPort, q_len); //create server
            System.out.println("Unverified Block Listener is listening.");
            while (true) {
                sock = servsock.accept(); //accept a connection and spawn a worker thread
                new UnverifiedBlockListener(sock).start();
            }
        }catch (IOException ioe) {System.out.println(ioe);}
    }    
}

//handles any unverified blocks coming in
class UnverifiedBlockListener extends Thread{
    Socket sock; //binds the socket to a local variable
    public UnverifiedBlockListener(Socket s){
        sock = s;
    }
    public void run() {
        //create input and output to connected client
        BufferedReader in = null;
        try {
            in = new BufferedReader (new InputStreamReader(sock.getInputStream()));
            try {
                String input;

                while ((input = in.readLine()) != null) {
                    //handle JSON input of the unverified blocks here
                    Blockchain.bank.handleJSONData(input);              
                }
            } catch (Exception e) {
                System.out.println("Read Error in Public key listener.");
                e.printStackTrace();
            }
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//this listens for any incoming blockchain ledgers connections and spawns a thread to handle it
class BlockchainServer implements Runnable {
    public void run(){
      int q_len = 6;//set queue length
      Socket sock;
      System.out.println("Starting the blockchain listener.");
      try{
        ServerSocket servsock = new ServerSocket(Blockchain.blockChainPort, q_len); //create a server and listen for connections
        while (true) {
            sock = servsock.accept();
            new BlockchainWorker (sock).start(); //spawns a worker thread to handle it
        }
      }catch (IOException ioe) {System.out.println(ioe);}
    }
}

//handles any connection to the blockchain server
class BlockchainWorker extends Thread { 

    Socket sock; //local variable to hold the socket for communication
    BlockchainWorker (Socket s) {sock = s;}

    public void run(){
      try{
        //create a channel to send data to client
        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        String input;
        while((input = in.readLine()) != null){
            BlockRecord[] temp = Blockchain.gson.fromJson(input, BlockRecord[].class); //gets a temporary byte array of the ledger sent in JSON format
            //only update if length is greater than the current local blockchain ledger
            if(VerificationWorker.currentLedger.size() <= temp.length){
                VerificationWorker.currentLedger.clear();
                //convert back to BlockRecord object and add it to the currentLedger
                for (BlockRecord b : temp) {
                    VerificationWorker.currentLedger.add(b);
                }    
                //for process 0 write to file everytime the ledger is updated
                if(Blockchain.pnum == 0){
                    Blockchain.writeToFile();
                }
                System.out.println("\n------------- New Blockchain Received -------------\n");
                VerificationWorker.printLedger(); //print the ledger that was updated for readability
                System.out.println();
            }
        }
        sock.close(); //close when done
      } catch (IOException x){ System.out.println("Error with blockchain worker.");x.printStackTrace();}
    }
}