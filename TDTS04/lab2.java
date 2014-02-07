import java.net.*;
import java.io.*;
//import java.util.ArrayList;

public class lab2
{
    public static void main(String args[]) throws Exception
    {
	if( args.length != 1 )
	    {
		System.err.println("Wrong number or args\n");
		System.exit(1);
	    }	
	//Need to parse string to int
	int server_port = Integer.parseInt(args[0]);
	RunServer( server_port );
    }

    public static void RunServer(int server_port) throws Exception
    {
	ServerSocket welcome_socket = new ServerSocket(server_port);
	while(true)
	    {
		Server( welcome_socket.accept() );
	    }
    }
    public static void Server( Socket server_side ) throws Exception
    {
	/****** server-side ********************/
	BufferedReader from_client = new BufferedReader( new InputStreamReader( server_side.getInputStream() ) ); // gets the input and reads it to a buffer
	PrintStream to_client = new PrintStream( server_side.getOutputStream() ); // enables output

	String hostname, input_line = from_client.readLine(), second = from_client.readLine();
	hostname = second.substring(6);
	System.out.println("Hostname: " + hostname); // troubleshooting

	/*-----------------------------------*/
	/****** client-side ******************/
			
	Socket client_side = new Socket(hostname, 80); // initiates with the hostname and port
	BufferedReader from_server = new BufferedReader( new InputStreamReader( client_side.getInputStream() ) ); // gets the input and reads it to a buffer
	PrintStream to_server = new PrintStream( client_side.getOutputStream() ); // enables output 
	
	System.out.println( "before write to server" );
	to_server.print(input_line + "\r\n");
	to_server.print(second + "\r\n");
       
	while( true )
	    {
		input_line = from_client.readLine();
		if( input_line != null && input_line.isEmpty())
		    break;
		else
		    {
			if( input_line.startsWith("Connection:") )
			    input_line = "Connection: close";
			to_server.print(input_line + "\r\n");
			System.out.println(input_line);
		    }
	    }
	to_server.print("\r\n");
	/*-----------------------------------*/	
	/****** server-side ********************/
	System.out.println( "before write to client");
	input_line = null;
	boolean headers = true;
	while( true )
	    {
		input_line = from_server.readLine();
		if( input_line != null && input_line.isEmpty() )
		    {
			headers = false;
			to_client.print("\n");
		    }
		else if( input_line == null )
		   break;
		else
		    {
			if( headers )
			    to_client.print(input_line + "\r\n");
			else
			    to_client.print(input_line + "\n");
		    }
	    }
	/*-----------------------------------*/
	System.out.println("End of transmission!");
	to_server.close();
	to_client.close();
	client_side.close();
	server_side.close();
	
    }
    
}
