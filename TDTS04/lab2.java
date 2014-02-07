import java.net.*;
import java.io.*;
import java.util.ArrayList;

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

	ServerSocket welcome_socket = new ServerSocket(server_port) ;
	while(true)
	    {
		Socket server_side = welcome_socket.accept();
		Server( server_side );
		server_side.close();
	    }
 
  
    }
    public static void Server( Socket server_side ) throws Exception
    {
	/****** server-side ********************/
	BufferedReader from_client = new BufferedReader( new InputStreamReader( server_side.getInputStream() ) ); // gets the input and reads it to a buffer
	PrintStream to_client = new PrintStream( server_side.getOutputStream(),true ); // enables output

	String hostname;
	String  input_line= "" , second = "";

	from_client.ready();
	input_line = from_client.readLine();
	    

	from_client.ready();	 
	second = from_client.readLine();
	if( second != null )	
	    hostname = second.substring(6, second.length() );
	else
	    return;
	
	//	System.out.println("Hostname: " + hostname); // troubleshooting
	/*-----------------------------------*/
	/****** client-side ******************/

	Socket client_side = new Socket(hostname, 80); // initiates with the hostname and port
	BufferedReader from_server = new BufferedReader( new InputStreamReader( client_side.getInputStream() ) ); // gets the input and reads it to a buffer
	PrintStream to_server = new PrintStream( client_side.getOutputStream(),true ); // enables output


	System.out.println( "#1 before write to server" );
	to_server.print(  input_line + "\r\n"); 
	to_server.print(second + "\r\n");
	boolean notText = false;

	from_client.ready();
   	while( true )
	    {
		input_line = from_client.readLine();
		if( input_line == null || input_line.isEmpty() )
		    break;
		if( input_line.startsWith("Connection:") )
		    {
			input_line = "Connection: close";
		    }
		else if( input_line.length() > 11 && ( input_line.startsWith("Accept:")  && input_line.substring(8,11) != "text") ) // if we do not have text data
		    notText = true;
			 
		to_server.print(input_line + "\r\n");
		from_client.ready();
	    }
	to_server.print("\r\n");
	System.out.println( "#2 data has been written to the to server" );

	/*-----------------------------------*/	
	/****** server-side ********************/
	input_line = "";
	boolean headers = true;
	from_server.ready();
	System.out.println( "#3 before write to client");

	while( (input_line = from_server.readLine()) != null  )
	    {
			
		if( bad_words( input_line ) )
		    {
			System.out.println("#Fult ord!") ;
			System.exit(1);
		    }

		if( input_line == "\n" ) // this is the case when we only have a \n
		    {
			headers = false;
			to_client.print("\n");
		    }
		else
		    {
			if( headers )
			    to_client.print(input_line + "\r\n");
			else if( notText )
			    to_client.print(input_line);
			else
			    to_client.print(input_line + "\n");
		    }
		from_server.ready();

	    }
	System.out.println( "#4 data has been written to the to client" );

	/*-----------------------------------*/
	System.out.println("End of transmission!");
	to_server.close();
	from_server.close();
	to_client.close();
	from_client.close();
	client_side.close();


    }

    public static BufferReader Read ( Socket S  ) throws Exception
    {
      
    }


    public static void Send ( Socket S ) throws Exception
    {

    }





    public static boolean bad_words ( String line ) throws Exception
    {
	ArrayList<String> bad_words = new ArrayList<String>();
	bad_words.add("Batman");
	String[] splitter = line.split(" ");

	for( int i = 0; i < bad_words.size(); ++i )
	    {
		for( int j = 0; j < splitter.length ; ++j )
		    {
			if( bad_words.get(i) == splitter[j]  )
			    {
				return true;
			    }
		    }
	    }
	return false;
    }

    
}
