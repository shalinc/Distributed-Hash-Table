/**
 * 
 */
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ServerSideImplementation implements Runnable
{
	private Socket clientSocket;
	public static ConcurrentHashMap<String, String> distributedHashTable = new ConcurrentHashMap<String, String>();
	private boolean loop = true;

	public ServerSideImplementation(Socket clientSocket) 
	{
		// TODO Auto-generated constructor stub
		this.clientSocket = clientSocket;
	}

	public void run()
	{
		try
		{
			//for communication over sockets between Client and server
			DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

			while(loop)
			{
				try
				{
					//read the choice according to MENU
					String choice = dIn.readUTF();

					switch(choice)
					{
					case "1":	
						//Perform Put Operation
						//System.out.println("Performing PUT into Distributed Hash Table ...");
												
						//read the key value pair from client
						String readKeyValue = dIn.readUTF();
						
						//split the value from Key and Value by ";"
						String[] hashKeyValue = readKeyValue.split(";");
						boolean resultofPut = put(hashKeyValue[0],hashKeyValue[1]);
						
						/*Comment out below for Performance Evaluation*/
						dOut.writeUTF(String.valueOf(resultofPut));
						break;

					case "2":	
						//Perform Get Operation
						
						//System.out.println("Performing GET operation from Distributed Hash Table ...");
						String key = dIn.readUTF();
						String value = get(key);
						
						/*Comment out below for Performance Evaluation*/
						
						/*Check if value is null return NULL to client, else return the actual VALUE for the KEY*/
						if(value == null)
							dOut.writeUTF("null");
						else
							dOut.writeUTF(value);
						break;

					case "3":	//Perform Delete Operation
						
						//System.out.println("performing DELET operation from DHT");
						String delkey = dIn.readUTF();
						boolean resultofDelete = del(delkey);
						
						/*Comment out below for Performance Evaluation*/
						/*Return the boolean result whether success on deletion or failure*/
						dOut.writeUTF(String.valueOf(resultofDelete));
						
						break;

					case "4":	//Exit
			
						System.out.println("Client Disconnected");
						loop = false;	//exit from the while(true) loop
						//System.exit(0);
						break;
					}

				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}//end of while
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	//Put function to put the KEY VALUE pair into the HashTable
	public static synchronized boolean put(String key, String value)
	{
		//obtain the actual key by removing the padded values from the Key & value
		key = key.substring(0,23).replace("*","");
		value = value.substring(0,999).replace("*", "");
		
		//if put into hashtable is successful then true, and display over which server the hashtable was put
		if((distributedHashTable.put(key, value))!="null")
		{
			System.out.println("The Distributed Hash Table is now: "+distributedHashTable);
			return true;
		}
		else
		{
				return false;
		}
	}
	
	//Get function to get the VALUE from the Specified KEY, if value is null it will return NULL
	public static String get(String key)
	{
		key = key.substring(0,23).replace("*","");
		String value = distributedHashTable.get(key);
		return value;
	}
	
	//Delete method to delete the KEY, VALUE from the hashtable, 
	//if HASH table doesn't KEY contain it will return false i.e. FAILURE
	public static synchronized boolean del(String key)
	{
		key = key.substring(0,23).replace("*","");
		
		if(distributedHashTable.remove(key,distributedHashTable.get(key)))
			return true;
		else
			return false;
	}
}