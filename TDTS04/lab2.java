import java.net.*;
import java.io.*;
import java.util.ArrayList;

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
			/* Client-side */
			Socket client_socket = welcome_socket.accept(); // socket for the newly accepted client on our server-side
			PrintWriter from_server = new PrintWriter(client_socket.getOutputStream(), true); // enables output
			BufferedReader to_server = new BufferedReader( new InputStreamReader ( client_socket.getInputStream() ) ); // get the input

			String input_line = to_server.readLine();
			ArrayList<String> input = new ArrayList<String>(); // so that we can change the content of each line
			while( input_line != null ) // output the data we got from the client
			    {
				input.add(input_line ); // adds the current line to the array, check if we really needed to add '\n'
				System.out.println( input_line ); // outputs the current line for troubleshooting
				System.out.println( input.size() );
				input_line = to_server.readLine();
			    }
			System.out.println(input.get(1));
			/* Server-side */
			// need to parse "input" for the hostname and set connection Close(), not keep-alive
			String delim = " ";
			String[] temp = input.get(0).split(delim); // gets the string at index 0 and splits it at every " "
			System.out.println("Hostname: " + temp[1]);
		    }
	    }
	catch( IOException e )
	    {
		System.err.println(e);
		System.out.println("Error in RunServer, have premission to use sockets?\n");
	    }
    }
}

