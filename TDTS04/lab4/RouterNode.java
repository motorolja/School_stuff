import javax.swing.*;        

public class RouterNode {
    private int myID;
    private GuiTextArea myGUI;
    private RouterSimulator sim;
    private boolean poisoned_reverse;
    // costs[0] = direct cost, costs[1] = shortest path, costs[2] = next Route for shortest path
    private int[][] costs = new int[3][RouterSimulator.NUM_NODES];

    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs) 
    {
	myID = ID;
	this.sim = sim;
	myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
	poisoned_reverse = false;

	System.arraycopy(costs, 0, this.costs[0], 0, RouterSimulator.NUM_NODES);
	this.costs[1] = this.costs[0];

	for( int i = 0; i < this.costs[2].length; ++i )
	    this.costs[2][i] = i;

	// Send update to get simulation going
	for( int i = 0; i < costs.length; ++i )
	{
	    sendUpdate( new RouterPacket( myID, i, costs ) );
	}
    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) 
    {
	boolean changed_table = false;
	for( int i = 0; i < pkt.mincost.length; ++i )
	    {
		// if we route through and the costs dont match or our current shortest path is longer
		if( ( pkt.sourceid == costs[2][i] && costs[1][pkt.sourceid] != pkt.mincost[myID] + pkt.mincost[i] ) ||
		    ( pkt.mincost[myID] + pkt.mincost[i] < costs[1][i] ) )
		    {
			costs[1][i] = pkt.mincost[myID] + pkt.mincost[i];
			// check if this is correct later
			if( poisoned_reverse )
			    {
				sendUpdate(  new RouterPacket( myID, i, costs[1] ) );			    
				printDistanceTable();
			    }			
			else
			    {
				changed_table = true;
			    }
		    } 
	    }
	if( changed_table )
	    {
		for( int i = 0; i < costs[1].length; ++i )
		    sendUpdate(  new RouterPacket( myID, i, costs[1] ) );    
		
		printDistanceTable();
	    }
    }
  
  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) 
    {
	sim.toLayer2(pkt);
    }
  

  //--------------------------------------------------
  public void printDistanceTable() 
    {
	// Timestamp
	myGUI.println("Current table for " + myID +
			"  at time " + sim.getClocktime());
	// router numbers
	myGUI.print("         ");
	for( int i = 0; i < costs[0].length; ++i )
	    myGUI.print(i + "\t");
	myGUI.println("");
	  
	// cost table
	String header = "Direct  :";
	for(int i = 0; i < costs.length; ++i)
	    {
		if( i == 1 )
		    header = "Shortest:";
		else if( i == 2 )
		    header = "Route   :";
		myGUI.print( header );
		
		for( int e = 0; e < costs[i].length; ++e )
		    myGUI.print( costs[i][e] + "\t" );
		myGUI.println("");
	    }
    }

  //--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) 
    {
	costs[0][dest] = newcost;
	if( costs[2][dest] == dest )
	    {
		costs[1][dest] = newcost;
	    }

	for( int i = 0; i < costs[0].length; ++i )
	    {
		sendUpdate(  new RouterPacket( myID, i, costs[0] ) );    
	    }		
	printDistanceTable();	
    }

}
