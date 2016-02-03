package Server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class DHTPerformanceEvaluation implements Runnable
{

	private ConcurrentHashMap<String, Socket> socketMapping;
	private Socket dhtClientSocket;

	public DHTPerformanceEvaluation(ConcurrentHashMap<String, Socket> socketMapping) 
	{
		// TODO Auto-generated constructor stub
		this.socketMapping = socketMapping;
	}

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
				//System.out.println("4. EXIT");

				//read choice from client
				DataInputStream dIS = new DataInputStream(System.in);
				choice = dIS.readLine();

				String keyValueRegisterInfo = null;
				String getKeyName = null;
				String paddedKeyValue = null;
				boolean resultOfOperation;
				String resultGet;
				long startTime;
				long endTime;
				
				switch(choice)
				{

				case "1":	//PUT(KEY,VALUE)

					System.out.println("Enter the key and value pair to register: ");
					keyValueRegisterInfo = dIS.readLine();

					String[] keyValuePair = keyValueRegisterInfo.split(";");

					startTime = System.currentTimeMillis();

					for(int i=0;i<100000;i++)
					{
						//find the hashvalue
						dhtClientSocket = myHashFunction(padKey(keyValuePair[0]+i));

						if(dhtClientSocket == null)
						{
							resultOfOperation = ServerSideImplementation.put(padKey(keyValuePair[0]+i), padValue(keyValuePair[1]+i));
						}
						else
						{
							paddedKeyValue = padKey(keyValuePair[0]+i)+";"+padValue(keyValuePair[1]+i);
							sockCommunicateStream(dhtClientSocket,choice,paddedKeyValue);
						}
					}

					endTime = System.currentTimeMillis();
					System.out.println("Time taken for put is: "+(endTime-startTime));
					break;

				case "2":	//GET

					System.out.println("Enter the Key to get: ");
					getKeyName = dIS.readLine();
					String getValue;
					startTime = System.currentTimeMillis();
					
					for(int i=0;i<100000;i++)
					{
						dhtClientSocket = myHashFunction(padKey(getKeyName+i));

						if(dhtClientSocket == null)
						{
							//System.out.println("The value is: "+ServerSideImplementation.get(padKey(getKeyName+i)));
							getValue = ServerSideImplementation.get(padKey(getKeyName+i));
						}
						else
						{
							sockCommunicateStream(dhtClientSocket,choice,padKey(getKeyName+i));
						}
					}
					endTime = System.currentTimeMillis();
					System.out.println("Time taken for get is: "+(endTime-startTime));
					
					break;

				case "3":	//delete

					System.out.println("Enter the Key to be deleted");
					String deleteKeyName = dIS.readLine();

					startTime = System.currentTimeMillis();
					
					for(int i=0;i<100000;i++)
					{
						dhtClientSocket = myHashFunction(padKey(deleteKeyName+i));

						if(dhtClientSocket == null)
						{
							resultOfOperation = ServerSideImplementation.del(padKey(deleteKeyName+i));
						}
						else
						{
							sockCommunicateStream(dhtClientSocket,choice,padKey(deleteKeyName+i));
						}
					}
					
					endTime = System.currentTimeMillis();
					System.out.println("Time taken for delete is: "+(endTime-startTime));
					break;

				case "4":	//Exit

					System.out.println("EXIT");
					sockCommunicateStream(dhtClientSocket,choice,null);
					//dOutServer.writeUTF(choice);
					//cAsServer.close();
					//System.exit(0);
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


	public Socket myHashFunction(String Key)
	{
		//String hashValue = "server"+Math.abs((Key.hashCode())%8);
		int hash = 7;
		for(int i=0;i<Key.length();i++)
		{
			hash = hash*31 + Key.charAt(i);
		}
		String hashValue = "server"+Math.abs(hash%8);
		
		Socket value = socketMapping.get(hashValue);

		return value;
		/*String hashValue = "server"+Math.abs((Key.hashCode())%8);
		Socket value = socketMapping.get(hashValue);

		return value;*/
	}

	public void sockCommunicateStream(Socket sckt, String menuChoice, String clientInpVal)
	{
		try
		{
			//make send and receive for sockets to communicate
			DataInputStream dInpServer = new DataInputStream(sckt.getInputStream());
			DataOutputStream dOutServer = new DataOutputStream(sckt.getOutputStream());

			dOutServer.writeUTF(menuChoice);
			dOutServer.writeUTF(clientInpVal);

			if(menuChoice.equals("2"))
			{
				//System.out.println("Value is: "+dInpServer.readUTF());
			}

			if(menuChoice.equals("1") || menuChoice.equals("3"))
			{
				//String resultValue = dInpServer.readUTF();
				/*if(resultValue.equals("true"))
				{
					System.out.println("Success");
				}
				else
				{
					System.out.println("Failure");
				}*/
			}
			
			if(menuChoice.equals("4"))
			{
				dOutServer.writeUTF(menuChoice);
			}

		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public String padKey(String key)
	{
		for(int i=key.length();i<24;i++)
		{
			key+="*";
		}
		return key;
	}

	public String padValue(String value)
	{
		for(int i=value.length();i<1000;i++)
		{
			value+="*";
		}
		return value;
	}
}


