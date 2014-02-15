import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class connection extends Thread
{
    private Socket server_side = null;
    private OutputStream out = null;
    private BufferedReader in = null;
    private ArrayList<String> bad_words = new ArrayList<String>();
    private BufferedReader byte_reader = null;
    
    public connection( Socket server_side ) throws SocketException
    {
        super("connection");
	this.server_side = server_side;
	this.server_side.setKeepAlive(true);
	System.out.println("created a new connection");
    }
    
    public void add_bad_words( String w )
    {
	bad_words.add( w.toLowerCase() );
    }
    
    public boolean check_content (byte[] buffer ) throws IOException
    {
	String input_line = null;
	InputStream is = new ByteArrayInputStream( buffer );
	byte_reader = new BufferedReader( new InputStreamReader( is ,"UTF-8") );

	while( true )
	    {

		input_line = byte_reader.readLine();
		if( input_line == null || input_line.length() == 0)
		    {
			break;
		    }
		else if( bad_word( input_line ) )
		    {
			return true;
		    }
	    }
	return false;
	
    }
    public void run()
    {

	//ADD/REMOVE FORBIDDEN WORDS HERE
	/**********************************/
	//EXEMPEL : add_bad_words( "Norrköpping" );
	//Norrköping is now a forbidden word!
	add_bad_words("Batman");


	/***********************************/
	System.out.println("START 1-- after a request on welcome-socket");
	try
	    {
		out = server_side.getOutputStream();
		in = new BufferedReader( new InputStreamReader( server_side.getInputStream() ) );

		// a class that handles the client_side of the proxy
		String host = get_host(in);

		if( host.equals("-1") )
		    return;
		    
		System.out.println("******hostname is: " + host);
		client client_side = new client( host, 80 );

		System.out.println("-------- after client class been created" );
		// writes the output from the client_side to server
		String input_line;
		while( true )
		    {
			in.ready();
			input_line = in.readLine();
			if( input_line == null || input_line.length() == 0)
			    {
				client_side.send_end();
				System.out.println("End of request!");
				break;
			    }
			client_side.send_line(input_line);
		    }


		// fetch the input from server_side to client_side
		boolean valid_content = true;
		byte[] buffer = new byte[1024];
		int counter = 0;
		while( ( counter = client_side.read(buffer) ) != -1 )
		    {
			//System.out.println( new String(buffer) );
			if( client_side.get_is_html() && check_content( buffer) )
			    {
				System.out.println("found bad word");
				valid_content = false;
				break;
			    }
		
			send_data(buffer,counter); 
		    }

		// if we have invalid content we send our predefined page
		if( !valid_content )
		    invalid_content( client_side );
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
	in.ready();
	String first_line = in.readLine();
	// some url check
	if( bad_url(first_line) )
	    {
		return invalid_url();
	    }

	String host_line = in.readLine();

	System.out.println("-----after hostname loop");
	// resets the buffer
	in.reset();
	// "host: hostname...." [0,1,2,3,4,5,6,..,n] hostname starts at index 6 of the second_line
	return host_line.substring(6);
    }

    public void clear_buffer() throws IOException
    {
	in.skip( Integer.MAX_VALUE );
    }

    public void send_data( byte[] buffer, int counter ) throws IOException
    {
	out.write(buffer,0, counter );
    }

    public boolean bad_url( String first_line )
    {
	return bad_word( first_line );
    }

    public String invalid_url() throws IOException
    {
	System.out.println("Invalid URL");
	client client_side = new client("www.ida.liu.se", 80);

	client_side.send_line("GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error1.html /HTTP/1.1");
	client_side.send_line("host: www.ida.liu.se");
	client_side.send_line("Connection: close");
	client_side.send_end();

	byte[] buffer = new byte[1024];
	int counter = 0;

	try {
	    while( ( counter = client_side.read(buffer) ) != -1 )
		{
		    //System.out.println( new String(buffer) );
		    send_data(buffer,counter);
		}
	}
	catch( IOException e )
	    {
		System.err.println( "Error in connection");
		e.printStackTrace();
	    }
	
	client_side.close();
	return "-1";
    }
    public void invalid_content( client client_side) throws IOException
    {
	System.out.println("Invalid content");
 	client_side.clear_buffer();

	client_side.new_host( "www.ida.liu.se");

	client_side.send_line("GET http://www.ida.liu.se/~TDTS04/labs/2011/ass2/error2.html /HTTP/1.1");
	client_side.send_line("host: www.ida.liu.se");
	client_side.send_line("Connection: close");
	client_side.send_end();

	byte[] buffer = new byte[1024];
	int counter = 0;

	try {
	    while( ( counter = client_side.read(buffer) ) != -1 )
		{
		    send_data(buffer,counter);
		}
	}
	catch( IOException e )
	    {
		System.err.println( "Error in connection");
		e.printStackTrace();
	    }		
    }

    public boolean bad_word(String input_line)
    {
	System.out.println("in bad word");
	if( input_line.length() != 0 )
	    {
		for( int i = 0; i < bad_words.size(); ++i )
		    {
			if( input_line.toLowerCase().indexOf( bad_words.get(i)  ) >= 0 )
			    return true;
		    }
	    }
	return false;
    }
}
