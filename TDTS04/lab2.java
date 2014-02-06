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

	PrintWriter to_client = new PrintWriter(server_side.getOutputStream(), true); // enables output
	BufferedReader from_client = new BufferedReader( new InputStreamReader ( server_side.getInputStream() ) ); // gets the input and reads it to a buffer

	String hostname = "liu.se";
	System.out.println("Hostname: " + hostname); // troubleshooting

	/*-----------------------------------*/
	/****** client-side ******************/
			
	Socket client_side = new Socket(hostname, 80); // initiates with the hostname and port
	PrintWriter to_server = new PrintWriter( client_side.getOutputStream(), true ); // enables output 
	BufferedReader from_server = new BufferedReader( new InputStreamReader ( client_side.getInputStream() ) ); // gets the input and reads it to a buffer
	
	System.out.println( "before write to server" );
	String input_line = null;
	for( int i = 0; i < 8; ++i)
	    {
		input_line = from_client.readLine();
		if( input_line == null )
		    break;
		System.out.println(input_line);
		to_server.println(input_line);
	    }
	to_server.println("\r");
	/*-----------------------------------*/	
	/****** server-side ********************/
	System.out.println( "before write to client");
	input_line = null;
	while( true )
	    {
		input_line = from_server.readLine();
		if( input_line == null )
		    break;
		System.out.println(input_line);	
		to_client.println(input_line);
	    }
	/*-----------------------------------*/
	System.out.println("End of transmission!");
    }
    
}
