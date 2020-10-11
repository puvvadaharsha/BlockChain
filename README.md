# Blockchain Program
### Technologies used: Java, JSON, Web Sockets, Web Server, Blockchain, SHA256, Encryption, Asymmetric Keys

* Program creates blocks from sample medical data from the blockchain input files
* Each process generates a public key and sends it to the other processes so all 3 processes know who verified each block
* Three processes compete to solve blocks and once a process solves a block, it sends the verified block to the other processes and process 0 via JSON
* Process 0 writes the ledger to disk after each update to it
*  Blocks are verfiied by SHA256 hash of 3 items: Previous Hash, Random Seed, Data
* Digital Signatures used to verify each block


### Dependencies:
* Make sure to have the gson jar file. If not program wont compile. One version of the JSON jar file here: https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.2/

* To Compile:
```
compile javac -cp "gson-2.8.2.jar": Blockchain.java
```

* Save script below into an .scpt file
```
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
```

### Run With:

```
run script osascript myScript.scpt
```

### Explanation of whats going on:
For each window, it will first create a section that says the Ports and the process number. It then generates a key pair and prints the public key. 
It then communicates with the other two processes to get their public keys and displays them once it has them. Next, it starts to read its own respective input 
file and creates unverified blocks into an unsorted list. It then shuffles the list and puts it into a priority queue. 
This is to demonstrate the sorting based on time for the priority queue.
Next, they send the unverified blocks to each other and each process prints out the full list of sorted unverified blocks.
At this point the processes start to verify the blocks and each time they verify a block, they send out the ledger to the other processes.
The random seed and the puzzle winning conditions are shown as well. Each time a new blockchain is received, it will print the new ledger. The program always 
chooses the longest chain. At the end, I used ctrl+c to terminate as I didn't have time to program an exit command.

### Sample Output when running the code:
```
******************************************************************************************************************************
In Process 0 window: This is what process 0's window showed in the output:
*****************************************************************************************************************************

++++++++++++++++++++++++++
Process number 0 running.
Public Key Port: 4710
Unverified Block Port:4820
Block Chain Port: 4930
++++++++++++++++++++++++++

Generating public and private key pair for this process.
my public key is:
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXt+3Ep3Zv+ipH25rfnuPYIpnwpRPhO7vM0z+p4y021OaMuyqxdr+EzuCqQkJ9Hdlv5L1DpDkpO7sWrA1DXqqs+ynd0GUeXe/jUOpY3FF8vd6+d3tLtrBMEvG5lme+XD6TsauiAp/K2rlKUX+zw92aJD5widB4MPiwSFMDRUGCVwIDAQAB
Sending my public key to the other processes.
Public Key Listener is listening.
For process: 0
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXt+3Ep3Zv+ipH25rfnuPYIpnwpRPhO7vM0z+p4y021OaMuyqxdr+EzuCqQkJ9Hdlv5L1DpDkpO7sWrA1DXqqs+ynd0GUeXe/jUOpY3FF8vd6+d3tLtrBMEvG5lme+XD6TsauiAp/K2rlKUX+zw92aJD5widB4MPiwSFMDRUGCVwIDAQAB
For process: 1
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaPVJR9HXXlyg5nGR71J8c3N2kcf926d56/SaT+GSyR8Vmp9YCe4xv9XFhbphCvDIKMOJn5E1nrZ3RXKSsDiRYb4SodimPXgzknrjpOqqXMriliPqyVFiaAzsUA9aJmYBAMR3Pb8m+UZfTzYp8pqgcFIn/pQdP/PdkLV3szScBTQIDAQAB
For process: 2
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPgAGMOUzj/L8gr6kDdGJVM+dptMOV+MauOWX8fkBrx2xCGu5Nt8+4/4M/ju8KbTtp18huuR+9GZeU0af3N2X+wVpGAfn4KWIWC5vCigMqwUYd1deyua+WDWKgGACqKDEnSHr90zyJqEKAfvfbukUE4oXNJGdLynrqbs4fwbljVQIDAQAB

I have gotten all three keys!

From input file:
####################################################################################
Number of blocks in unverified list: 4
Blocks in Unverified List:
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine

Shuffling Linked List of unverified blocks:
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson

Priority Queue Blocks in order by time: 
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine

####################################################################################

Unverified Block Listener is listening.
Sending my unverified blocks to the other processes.
All unverified blocks received. Printing all unverified blocks in order below:

be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu
2fbc4680-90aa-43eb-bfea-02cefd6a13be 2020-05-27.20:46:15:518.1 Sally McCutty
babbb9f8-6578-45ad-b8b1-6b064e06ef76 2020-05-27.20:46:15:623.1 Bruce Lee


####################################################################################

Starting the blockchain listener.


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block
Has the signed blockID been verified: true
Puzzle Hash in hex is: f0d24677111d15a2f32188d4d4bea862739309dd6f95dca2573cfdc3b224f515
First 16 bits in Hex and Decimal: f0d2 and 61650
61650 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: b08079f8cfeba0f2544d1f02f54f1227fffd678a02a87f3758ed95dfff57be36
First 16 bits in Hex and Decimal: b080 and 45184
45184 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: 4cc4c9d5087d68595e2d5f915236c4f13d18975ae22bb03e98ec9f3f040dedc8
First 16 bits in Hex and Decimal: 4cc4 and 19652
19652 is less than 20000 so puzzle solved!
The seed (correct answer) was: K9QP3SP6
Sending my ledger to the other processes.

Moving to next Block
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith
Has the signed blockID been verified: true
Puzzle Hash in hex is: 3d6db49638ba318eac554d9aa3aed11d1d91b65bcdcf2a89e97207186731fd79
First 16 bits in Hex and Decimal: 3d6d and 15725
15725 is less than 20000 so puzzle solved!
The seed (correct answer) was: JKUCEN1U
Sending my ledger to the other processes.

Moving to next Block
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: 05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow
Has the signed blockID been verified: true

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith CWSV1WR3 Process 2

Puzzle Hash in hex is: d9dbd699e450ed711d72db58697be749903670509a3977d55df30d3ba988f00f
First 16 bits in Hex and Decimal: d9db and 55771
55771 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1

Puzzle Hash in hex is: 454631dccf68576b733abc4752cdec9a36f457d9006bb2932670406f8c7c97a9
First 16 bits in Hex and Decimal: 4546 and 17734
17734 is less than 20000 so puzzle solved!
The seed (correct answer) was: N6RJ4GP2

Moving to next Block
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson
Puzzle Hash in hex is: 036bd74a5438225a22a37148abd74bca09fc290a99dd0605240defd20fa9e22a
First 16 bits in Hex and Decimal: 036b and 875
875 is less than 20000 so puzzle solved!
The seed (correct answer) was: I1GKCYFO
Sending my ledger to the other processes.

Moving to next Block
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: 2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine
Has the signed blockID been verified: true
Puzzle Hash in hex is: cb9182d8c2e275870076e3f9df80af865254a5497f5e64695adda04f1a89fdcf
First 16 bits in Hex and Decimal: cb91 and 52113
52113 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: 0cb69f839070068dceb64ea6c9a739a9d565189c09865ebdf519eb9aee96dc04
First 16 bits in Hex and Decimal: 0cb6 and 3254
3254 is less than 20000 so puzzle solved!
The seed (correct answer) was: PNYS6LK6
Sending my ledger to the other processes.

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1


Moving to next Block
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: 94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller
Has the signed blockID been verified: true
Puzzle Hash in hex is: b142fb57e1ace73877e11bc76d4235e80a140839139653be50c1aa17264fef56
First 16 bits in Hex and Decimal: b142 and 45378
45378 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: 22cc3c5e79bbf0379f89daf7724168f85dc3d77a6b36138c57ab877bf41bfc5a
First 16 bits in Hex and Decimal: 22cc and 8908
8908 is less than 20000 so puzzle solved!
The seed (correct answer) was: J5UBH1FM
Sending my ledger to the other processes.

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine PNYS6LK6 Process 0
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller Q48VHH6T Process 2


Moving to next Block
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln
Has the signed blockID been verified: true
Puzzle Hash in hex is: e223d6e092d75bbbc14ca3a144998d4e5d448e5d087f6cbaa1c4a3417845c430
First 16 bits in Hex and Decimal: e223 and 57891
57891 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine PNYS6LK6 Process 0
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller Q48VHH6T Process 2
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln B50RQDTR Process 1

Puzzle Hash in hex is: 1b3305144b5b6a98e1587f2aa1fd4bde0ad2a8c0e2576d71000aeb24090502e1
First 16 bits in Hex and Decimal: 1b33 and 6963
6963 is less than 20000 so puzzle solved!
The seed (correct answer) was: 9LEYROKD

Moving to next Block
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2

Puzzle Hash in hex is: 7cda0eaa78a53acf7a902c228f453f8f8685990206b534ae5118d99a10832e28
First 16 bits in Hex and Decimal: 7cda and 31962
31962 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: 31adedfee17c20c07f472e84e9b5a01c71dbe22b85277fe6dad0bd2a9165c51d
First 16 bits in Hex and Decimal: 31ad and 12717
12717 is less than 20000 so puzzle solved!
The seed (correct answer) was: FWHYE23K
Sending my ledger to the other processes.

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy MDXK6ON6 Process 1


Moving to next Block
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio
Has the signed blockID been verified: true
Puzzle Hash in hex is: 57dcc9723483f1cb0f0c3fbe327e79e0e890a8385bda794193ecc34324a8697c
First 16 bits in Hex and Decimal: 57dc and 22492
22492 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1

Puzzle Hash in hex is: a0120eb6ffcf07aad00af6d761989c359e11be3300b332a328a02e2e149ff31a
First 16 bits in Hex and Decimal: a012 and 40978
40978 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: c807d6a0e15272c24d8be37d22242c19568d3416a285d90878d6777ac70c67de
First 16 bits in Hex and Decimal: c807 and 51207
51207 is not less than 20000 so it did not solve the puzzle

Moving to next Block
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: 584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita
Has the signed blockID been verified: true
Puzzle Hash in hex is: f1352cca77ccdc15afe17cfc6a5b295d996115fc89744eac8db66849a108ec45
First 16 bits in Hex and Decimal: f135 and 61749
61749 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita B2FLLNH5 Process 1

Puzzle Hash in hex is: 539eea5a5b5b7018d0c909065254123c6bc798e8f964c53a7cd1ea0acbbe6cff
First 16 bits in Hex and Decimal: 539e and 21406
21406 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: 7b11479acf3212c1532bbcfca57e01361687273c8d9277d349c8138acbde4185
First 16 bits in Hex and Decimal: 7b11 and 31505
31505 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita U2AU4FOU Process 2


Moving to next Block
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: 4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu
Has the signed blockID been verified: true
Puzzle Hash in hex is: a879ee44364ecce41625d0d827cf53d690e40698a2537e1429ca89de4de64e32
First 16 bits in Hex and Decimal: a879 and 43129
43129 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: ae7cf64d06635bb7cd91c5118e38e5224634b97d6f7e074cbaa2b66ee616ac0a
First 16 bits in Hex and Decimal: ae7c and 44668
44668 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita U2AU4FOU Process 2
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu 8J2H2KJX Process 1

Puzzle Hash in hex is: 39613d3abae917a230381dc6aa2b4da18a71c5f0165990c4f8291e1153beff3e
First 16 bits in Hex and Decimal: 3961 and 14689
14689 is less than 20000 so puzzle solved!
The seed (correct answer) was: SV1JY07L

Moving to next Block
2fbc4680-90aa-43eb-bfea-02cefd6a13be 2020-05-27.20:46:15:518.1 Sally McCutty

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita B2FLLNH5 Process 1
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu P0N7VBMD Process 2

Puzzle Hash in hex is: eff0dbaf6766ee0b52b0f2af2f0f112ab4bf59cf27b036033f2a866e4d8cdbde
First 16 bits in Hex and Decimal: eff0 and 61424
61424 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: 781901a42808a5b6799bcd0d5f14054f27c68c8ff89b7f96fcb308108222dc3f
First 16 bits in Hex and Decimal: 7819 and 30745
30745 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita B2FLLNH5 Process 1
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu P0N7VBMD Process 2
2fbc4680-90aa-43eb-bfea-02cefd6a13be 2020-05-27.20:46:15:518.1 Sally McCutty 8935VDIU Process 1

Puzzle Hash in hex is: e37a55681323e4278ac636faecdcd37f8bda9c221fd4d6103fe16c2a2e93f5bb
First 16 bits in Hex and Decimal: e37a and 58234
58234 is not less than 20000 so it did not solve the puzzle

Moving to next Block
babbb9f8-6578-45ad-b8b1-6b064e06ef76 2020-05-27.20:46:15:623.1 Bruce Lee


!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
Working on Block: babbb9f8-6578-45ad-b8b1-6b064e06ef76 2020-05-27.20:46:15:623.1 Bruce Lee
Has the signed blockID been verified: true

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita U2AU4FOU Process 2
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu 8J2H2KJX Process 1
2fbc4680-90aa-43eb-bfea-02cefd6a13be 2020-05-27.20:46:15:518.1 Sally McCutty G8STAC05 Process 2

Puzzle Hash in hex is: 91d50dbd67e80cd63c05bd49f06ce9db8dab7001a10af00e288d00166a22b919
First 16 bits in Hex and Decimal: 91d5 and 37333
37333 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: 56f6a1cd421d1d0df1d05afb9d4b9dd6509d8dedfac9e47af6cdae318f7d8a08
First 16 bits in Hex and Decimal: 56f6 and 22262
22262 is not less than 20000 so it did not solve the puzzle
Puzzle Hash in hex is: b0e3e56673b5b3781da87d833a1f1b9f1792aa823cdaed17e93b0bd2ef740ac5
First 16 bits in Hex and Decimal: b0e3 and 45283
45283 is not less than 20000 so it did not solve the puzzle

------------- New Blockchain Received -------------

Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita U2AU4FOU Process 2
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu 8J2H2KJX Process 1
2fbc4680-90aa-43eb-bfea-02cefd6a13be 2020-05-27.20:46:15:518.1 Sally McCutty G8STAC05 Process 2
babbb9f8-6578-45ad-b8b1-6b064e06ef76 2020-05-27.20:46:15:623.1 Bruce Lee YHGW6N99 Process 1


Done Verifying all blocks. Final Blockchain Ledger:
Printing current ledger
be476a89-ac23-4ad9-82f0-2b81726a083b 0000-00-00.00:00:00:000.2 Dummy Block K9QP3SP6 Process 0
c5de0f32-b5ec-4f31-838e-043f351370b7 2020-05-27.20:46:12:774.0 John Smith JKUCEN1U Process 0
05409b67-7cae-4f06-81f0-ab250d2aa500 2020-05-27.20:46:12:917.0 Joe Blow CIUMFBWK Process 1
dbdf18bf-2cfc-43ba-a814-78ada9de18a4 2020-05-27.20:46:13:020.0 Julie Wilson I1GKCYFO Process 0
2ccf241e-8072-4549-80c5-8cfb5807d524 2020-05-27.20:46:13:126.0 Wayne Blaine 1KCSOB6F Process 1
94ae3c5e-a07f-4a19-8dba-7dfc658bc9fb 2020-05-27.20:46:14:918.2 Helen Keller J5UBH1FM Process 0
fe9aac24-872a-431c-9365-169ad25dc809 2020-05-27.20:46:15:026.2 Abraham Lincoln 6T7R4TMP Process 2
fc33da5d-6c28-408e-b66f-fef8bfcbc331 2020-05-27.20:46:15:132.2 John Kennedy FWHYE23K Process 0
b6358332-9570-426e-92e5-5feb3fc6d4f6 2020-05-27.20:46:15:234.2 Joe DiMaggio HV893VOX Process 1
584f498d-1c8a-4e59-bc9d-a147b1a77f8a 2020-05-27.20:46:15:271.1 Rita Vita U2AU4FOU Process 2
4df752a4-4673-464a-a5a7-cf3629855ab4 2020-05-27.20:46:15:411.1 Wei Xu 8J2H2KJX Process 1
2fbc4680-90aa-43eb-bfea-02cefd6a13be 2020-05-27.20:46:15:518.1 Sally McCutty G8STAC05 Process 2
babbb9f8-6578-45ad-b8b1-6b064e06ef76 2020-05-27.20:46:15:623.1 Bruce Lee YHGW6N99 Process 1
^C%                                                                                                    
```