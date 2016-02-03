# Distributed-Hash-Table
A Simple Distributed Hash Table

<h4>INTRODUCTION:</h4>
This implementation is Java based Simple Distributed Hash Table (DHT). Unlike Centralized Architecture, here there is no Centralized Index Server.
Here we have Peers i.e. both acting as Server as well as Client. 
Each of these peers has its own Hash table which stores Key/Value pairs. 
There are two components viz. Peer and its DHT, which works as follows:</br>

<ol>
<li><strong>A Peer</strong></li>
The peer acts as a server and client both. As a Client, it provides interfaces through which users can issue queries and view search results. As a Server, it accepts queries from other peers, checks for matches against its local hash table, and responds with corresponding results. In addition, since there's no central indexing server, search is done through consistent hashing.
<li><strong>Distributed Hash Table (DHT) </strong></li>
A DHT is a hash table which store values in form of Key/Value pairs. 
The hash table stores these values into buckets, which is computed by using a hash function. 
The hash function computes the hash value for the Key and allocates it to a particular bucket in table.
</ol>

<h4>BEFORE EXECUTION:</h4>
1. Edit ConfigureServer.java file, to change the path according to the system to read the config.xml file, line (51)
2. Change the config.xml file to change the ServerIP tag to IP address you want.

<h4>STEPS FOR EXECUTION:</h4>
<strong>Using Command Prompt/ Terminal:</strong>

1. Open Cmd
2. Goto \Distributed-Hash-Table\
3. Run the cmd to compile all java files: javac *.java
4. Now to run server code: java Server.ConfigureServer server0 
(name of the server from 0 to 7, there is no space between server and its number ex. server1)
Pass the servers as the cmd line argument
5. Repeat step 4, for all the 8 Servers connected to each other.
