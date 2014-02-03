import java.io.*;
import java.net.*;

public class ProxyServer
{
    public static void main(string args[])
    {
	if( args[].length != 3 )
	    return;
	//Need to parse string to int
	String hostname = args[0];
	int server_port = Integer.parseInt(args[1]), remote_port = Integer.parseInt(args[2]);
	RunServer( hostname, server_port, remote_port);
    }

    public static void RunServer(String hostname, int server_port, int remote_port)
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
			    out.println( input_line );
		    }
	    }
	catch( IOException e )
	    {
		System.err.println(e);
		System.out.println("Error with creating a new socket\n");
	    }
    }
}

