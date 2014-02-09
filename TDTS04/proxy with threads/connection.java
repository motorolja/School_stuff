import java.io.*;
import java.net.*;

public class connection extends Thread 
{
    private Socket server_side = null;
    private OutputStream out = null;
    private BufferedReader in = null;

    public connection( Socket server_side ) throws SocketException
    {
        super("connection"); 
	this.server_side = server_side;
	this.server_side.setKeepAlive(true);
    }
    
    public void run() 
    {
	System.out.println("START 1-- after a request on welcome-socket");
	try
	    {
		out =  server_side.getOutputStream();
		in =  new BufferedReader( new InputStreamReader( server_side.getInputStream() ) );

		// a class that handles the client_side of the proxy
		String host = get_host(in);
		System.out.println("******hostname is: " + host);
		client client_side = new client( host, 80 );

		System.out.println("-------- after client class been created" );
		// writes the output from the client_side to server
		String input_line;
		while( true )
		    {
			in.ready();
			input_line = in.readLine();
			if( input_line == null  || input_line.length() == 0)
			    {
				client_side.send_end();
				System.out.println("End of request!");
				break;
			    }
			System.out.println(input_line);
			client_side.send_line(input_line);
		    }

		
		// fetch the input from server_side to client_side
		byte[] buffer = new byte[1024];
		int counter = 0;
		while( ( counter = client_side.read(buffer) ) != -1 )
		       {
			   System.out.println("yo");
			   System.out.println( new String(buffer) );
			   send_data(buffer,counter);
		       }
		System.out.println("never in?");
		boolean valid_content = true;
		 
		// if we have invalid content we send our predefined page
		if( !valid_content )
		    invalid_content();
		// closes the client_side socket
		client_side.close();
		out.flush();

		System.out.println(" END ---Before we closed the server_side socket");
		// after we are done the connection is closed
		server_side.close(); 
	    }
	catch( IOException e )
	    {
		System.err.println( "Error in connection");
		e.printStackTrace();
	    }
    }
    
    public String get_host ( BufferedReader in) throws IOException
    {
	System.out.println("in get hostname");
	// sets a mark of where to reset the buffer
	in.mark(1024); 
	boolean faulty_host = false;
	String first_line = in.readLine(); 
	// some url check
	
	String host_line = in.readLine();
	if( !host_line.isEmpty() && host_line.length() < 7)
	    faulty_host = true;

	System.out.println("-----after hostname loop");
	// resets the buffer
	in.reset();
	// incase we miss the host data
	if( faulty_host )
	    return "";
	// "host: hostname...." [0,1,2,3,4,5,6,..,n] hostname starts at index 6 of the second_line
	return host_line.substring(6);
    }

    public void send_data( byte[] buffer, int counter ) throws IOException
    {
	out.write(buffer,0, counter );
    }

    public void invalid_content()
    {
	
    }

    public boolean bad_word(String input_line)
    {
	return false;
    }
}
