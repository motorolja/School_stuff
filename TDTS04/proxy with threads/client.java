import java.io.*;
import java.net.*;

public class client extends Thread
{
    private Socket client_side = null;
    private PrintWriter out = null;
    private InputStream in = null;
    
    public client( String host, int port) throws IOException
    {
        super("client"); 
	System.out.println("in client constructor");
	client_side = new Socket(host, port);
	client_side.setKeepAlive(false); // Connection: close
	out = new PrintWriter( client_side.getOutputStream(), true ); // true = autoflush
	in = client_side.getInputStream() ;
    }
    
    
    public void send_line( String input_line )
    {
	if( input_line.toLowerCase().startsWith("connection:") )
	    input_line = "Connection: close";
	
	if ( input_line.toLowerCase().startsWith("accept-encoding:") )
	    return;

	out.print(input_line + "\r\n");
	System.out.println(input_line);

    }
    public void send_end()
    {
	out.print("\r\n");
	out.flush();
    }

    public int read( byte[] buffer ) throws IOException
    {
	System.out.println("reading");
	in.available();
	return in.read(buffer);
    } 

    public void close()
    {
	try 
	    { 
		client_side.shutdownInput();
		client_side.shutdownOutput();
		in.close();
		out.close();
		client_side.close(); 
	    }
	catch( IOException e )
	    {
		System.err.println("Error while closing client_side socket");
		e.printStackTrace();
	    }
    }
}
