import java.net.*;
import java.io.*;

public class proxy
{
    public static void main(String args[]) throws IOException
    {
	if( args.length != 1 )
	    {
		System.err.println("Wrong number or args\n");
		System.exit(1);
	    }	
	//Need to parse string to int
	int server_port = Integer.parseInt(args[0]);
	ServerSocket welcome_socket = new ServerSocket(server_port);
	     
	System.out.println("#-- made a welcome-socket to listen for requests on port: " + server_port );
	while(true)
	    new connection( welcome_socket.accept() ).start(); // opens a new thread for the request 
    }
}
