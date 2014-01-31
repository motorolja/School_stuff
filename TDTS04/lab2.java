import java.io.*;
import java.net.*;
//import java.util.Scanner; //Input

public class ProxyServer
{
    public static void main(string args[])
    {
	try
	    {
		// set port for communication between proxy and client
		if(args.length != 3)
		    {
			throw new RuntimeException("for wrong number of arguments\n");
		    }

	    }
	catch( exception e )
	    {
		System.err.println(e);
		System.err.println("Awsome error msg\n");
		System.exit(1);
	    }
	//Need to parse string to int
	int server_port = Integer.parseInt(args[1]), client_port = Integer.parseInt(args[2]);
	Socket client_socket = null; // between server and proxy
	Socket server_socket = null; // between client and proxy
	run_server(args[0],     server_port, server_socket);
	run_client("something", client_port, client_socket);
	
    }

    public static run_server(string host, int server_port, Socket server_socket )
    {
	server_socket = new Socket(host, server_port); // sets the socket between server and proxy
   
	
	// some datastreams for incoming connections (one for server and one for client)
	
	// datastreams for outgoing connections  (one for server and one for client)
	DataOutputStream to_client  ( new DataOutputStream ( server_socket.getOutputStream() ) );
	BufferedReader   from_client( new BufferedReader( new inputStreamReader( server_socket.getInputStream () ) ) );




    }

    public static run_client( string host, int client_port, Socket client_socket )
    {
	client_socket = new Socket( host, client_port ); // sets the socket between client and proxy

	DataOutputStream to_server( new DataOutputStream ( client_socket.getOutputStream() ) );
	BufferedReader   from_server( new BufferedReader( new inputStreamReader( client_socket.getInputStream () ) ) );

	
    }

}

