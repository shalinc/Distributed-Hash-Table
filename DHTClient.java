package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class DHTClient implements Runnable
{

	private ConcurrentHashMap<String, Socket> socketMapping;
	private Socket dhtClientSocket;

	//Constructor for Client
	public DHTClient(ConcurrentHashMap<String, Socket> socketMapping) 
	{
		// TODO Auto-generated constructor stub
		this.socketMapping = socketMapping;
	}

	//run the client side thread
	@SuppressWarnings("deprecation")
	public void run()
	{
		try
		{
			String choice;

			do
			{
				System.out.println("****MENU****");
				System.out.println("1. PUT");
				System.out.println("2. GET");
				System.out.println("3. DELETE");
				System.out.println("4. EXIT");

				//read choice from client
				DataInputStream dIS = new DataInputStream(System.in);
				choice = dIS.readLine();

				String keyValueRegisterInfo = null;
				String getKeyName = null;
				String paddedKeyValue = null;
				boolean resultOfOperation;
				String resultGet;

				switch(choice)
				{

				case "1":	//PUT(KEY,VALUE)

					//accept the key value pair separated by ";"
					System.out.println("Enter the key and value pair to register: ");
					keyValueRegisterInfo = dIS.readLine();

					String[] keyValuePair = keyValueRegisterInfo.split(";");

					//find the hashValue, where to put this KEY,VALUE
					dhtClientSocket = myHashFunction(padKey(keyValuePair[0]));

					//put the value if local server, put into local hash table
					//else, put into other server, by connecting to other server using sockCommunicateStream
					if(dhtClientSocket == null)
					{
						resultOfOperation = ServerSideImplementation.put(padKey(keyValuePair[0]), padValue(keyValuePair[1]));
						if(resultOfOperation)
								System.out.println("Success");
							else
								System.out.println("Failure");
					}
					else
					{
						paddedKeyValue = padKey(keyValuePair[0])+";"+padValue(keyValuePair[1]);
						sockCommunicateStream(dhtClientSocket,choice,paddedKeyValue);
					}
					
					break;

				case "2":	//GET

					System.out.println("Enter the Key to get: ");
					getKeyName = dIS.readLine();

					//get the Hashvalue where to get the value from
					dhtClientSocket = myHashFunction(padKey(getKeyName));
					
					//search in local hashtable,
					//else, find from other server
					if(dhtClientSocket == null)
					{
						System.out.println("The value is: "+ServerSideImplementation.get(padKey(getKeyName)));
					}
					else
					{
						sockCommunicateStream(dhtClientSocket,choice,padKey(getKeyName));
					}
					break;

				case "3":	//delete

					System.out.println("Enter the Key to be deleted");
					String deleteKeyName = dIS.readLine();

					dhtClientSocket = myHashFunction(padKey(deleteKeyName));

					if(dhtClientSocket == null)
					{
						resultOfOperation = ServerSideImplementation.del(padKey(deleteKeyName));
						if(resultOfOperation)
							System.out.println("Success");
						else
							System.out.println("Failure");
					}
					else
					{
						sockCommunicateStream(dhtClientSocket,choice,padKey(deleteKeyName));
					}
					break;

				case "4":	//Exit

					System.out.println("EXIT");
					break;

				default:
					break;

				}
			}while(!(choice.equals("4")));

		}catch(IOException e)
		{
			e.printStackTrace();
		}
	} 


	/*This method finds the hashValue and return the Server Socket for Communication*/
	public Socket myHashFunction(String Key)
	{
		String hashValue = "server"+Math.abs((Key.hashCode())%8);
		Socket value = socketMapping.get(hashValue);
		
		//own hashFunction
		/*int hash = 7;
		for(int i=0;i<Key.length();i++)
		{
			hash = hash*31 + Key.charAt(i);
		}
		String hashValue = "server"+Math.abs(hash%8);
		Socket value = socketMapping.get(hashValue);*/
		
	
		return value;
	}

	/*This method is used to connect between sockets i.e. Servers, and send and receive
	 * message and Communicate for key/value pair to get/put/delete */
	public void sockCommunicateStream(Socket sckt, String menuChoice, String clientInpVal)
	{
		try
		{
			//make send and receive for sockets to communicate
			DataInputStream dInpServer = new DataInputStream(sckt.getInputStream());
			DataOutputStream dOutServer = new DataOutputStream(sckt.getOutputStream());

			//send the server the choice and key/value
			dOutServer.writeUTF(menuChoice);
			dOutServer.writeUTF(clientInpVal);

			if(menuChoice.equals("2"))
			{
				System.out.println("Value is: "+dInpServer.readUTF());
			}

			if(menuChoice.equals("1") || menuChoice.equals("3"))
			{	
				String resultValue = dInpServer.readUTF();
				if(resultValue.equals("true"))
				{
					System.out.println("Success");
				}
				else
				{
					System.out.println("Failure");
				}
			}
			
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	/* The entire message i.e KEY + VALUE is of 1024 bytes
	 * out of which KEY is of 24 Bytes, here we pad the remaning bytes of the key with "*" 
	 * while sending we send entire 1024 bytes*/
	public String padKey(String key)
	{
		for(int i=key.length();i<24;i++)
		{
			key+="*";
		}
		return key;
	}

	/* The entire message i.e KEY + VALUE is of 1024 bytes
	 * out of which VALUE is of 1000 Bytes, here we pad the remaning bytes of the value with "*" 
	 * while sending we send entire 1024 bytes*/
	public String padValue(String value)
	{
		for(int i=value.length();i<1000;i++)
		{
			value+="*";
		}
		return value;
	}
}
