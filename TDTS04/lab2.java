import java.net.*;
import java.io.*;

public class lab2
{
    public static void main(String args[])
    {
	if( args.length != 1 )
	    {
		System.err.println("Wrong number or args\n");
		System.exit(1);
	    }	
	//Need to parse string to int
	int server_port = Integer.parseInt(args[0]), remote_port = 80;
	RunServer( server_port, remote_port);
    }

    public static void RunServer(int server_port, int remote_port)
    {
	try
	    {
		ServerSocket welcome_socket = new ServerSocket(server_port);
		while(true)
		    {
			Socket client_socket = welcome_socket.accept(); // socket for the newly accepted client on our server-side
			PrintWriter from_server = new PrintWriter(client_socket.getOutputStream(), true);
			BufferedReader to_server = new BufferedReader( new InputStreamReader ( client_socket.getInputStream() ) );
			
			String input_line;
			while( ( input_line = to_server.readLine() ) != null )
			    System.out.println( input_line );
		    }
	    }
	catch( IOException e )
	    {
		System.err.println(e);
		System.out.println("Error with creating a new socket\n");
	    }
    }
}

