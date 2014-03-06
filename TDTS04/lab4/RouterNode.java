 import javax.swing.*;        

public class RouterNode 
{
    private int myID; //Router id
    private GuiTextArea myGUI; //GUI 
    private RouterSimulator sim; 
    private boolean poison_reverse = true;
    private int[] cost         = new int[RouterSimulator.NUM_NODES];
    private int[] direct_cost  = new int[RouterSimulator.NUM_NODES];
    private int[] route       = new  int[RouterSimulator.NUM_NODES];
    private int[][] router_tables = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
    //--------------------------------------------------
    public RouterNode(int ID, RouterSimulator sim, int[] costs)
    {
	myID = ID;
	this.sim = sim;
	myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");

	System.arraycopy(costs, 0, this.cost, 0, RouterSimulator.NUM_NODES);

	
	for( int i = 0; i < cost.length; ++i )
	    {
		direct_cost[i] = costs[i]; // Fastes way is through neighbor
		route[i] = i;
	    }

	for( int i = 0; i < cost.length; ++i )
	    {
		if( cost[i] != 999 )
		    {
			RouterPacket updated_table = new RouterPacket( myID, i, cost  );
			sendUpdate( updated_table );
		    }
	    }
	printDistanceTable();	

    }

    //--------------------------------------------------
    public void recvUpdate(RouterPacket pkt) 
    {
	int pkt_mincost;
	boolean changed_table = false;

	for( int i = 0; i < cost.length; ++i)
	    {
		pkt_mincost = pkt.mincost[i];
	
       		if( (route[ i ] == pkt.sourceid && cost[ i ] != direct_cost[ pkt.sourceid ] + pkt_mincost)
		    ||
		    cost[ i ] > pkt_mincost + direct_cost[ pkt.sourceid ]   )  
		    {
			cost[ i ] =  direct_cost[ pkt.sourceid ] + pkt_mincost;
			route[ i ] = pkt.sourceid; 
			changed_table = true;
		    }

		//Update matrix
		router_tables [ pkt.sourceid ] [ i ] = pkt_mincost;
	    }

	printDistanceTable();
	if( changed_table  )
	    {
		for( int i = 0; i < cost.length; ++i )
		    {
			if(  cost[i] != 999 )
			    {
				RouterPacket updated_table;
				if( poison_reverse )
				    {
					int[] temp = new int[cost.length];

					for( int y = 0; y < temp.length; ++y )
					    {
						if( route[y] == i   )
						    {
							temp[y] = 999;
						    }
						else 
						    {
							temp[y] = direct_cost[y];
						    }
					    }	
					updated_table = new RouterPacket( myID, i, temp);
				    }
				else
				    {
					updated_table = new RouterPacket( myID, i, direct_cost);
				    }
				sendUpdate(  updated_table );
			    }

		    }
	    }
    }
  

    //--------------------------------------------------
    private void sendUpdate(RouterPacket pkt) 
    {
	sim.toLayer2(pkt); //DONT TOUCH THIS
    }
  

    //--------------------------------------------------
    public void printDistanceTable() 
    {
	myGUI.println("Current table for " + myID +
		      "  at time " + sim.getClocktime());

	myGUI.print("Router: ");
	for( int i = 0; i < cost.length; ++i )
	    {
		myGUI.print(  i + "  ");
	    }
	myGUI.print("\n");

	myGUI.print("Cost  : ");
	for( int i = 0; i < cost.length; ++i )
	    {
		myGUI.print( cost[i] + "  ");
	    }
	myGUI.print("\n");

	myGUI.print("Route : ");
	for( int i = 0; i < cost.length; ++i )
	    {
		if( cost[i] == 999 )
		    {
			myGUI.print( "-"  + "  ");
		    }
		else
		    {
			myGUI.print( route[i] + "  ");
		    }
	    }
	myGUI.print("\n");
    }

    //--------------------------------------------------
    public void updateLinkCost(int dest, int newcost)
    {
	direct_cost[dest] = newcost;
	router_tables[myID] = direct_cost;

	// For debugging!!!
	myGUI.println("Call to updateLinkCost! see router_tables below\n");
	for( int i = 0; i < cost.length; ++i)
	    {
		if( i == 0 )
		    myGUI.print("Router\t" + i + "\t" );
		else
		    myGUI.print(i + "\t");
	    }
	myGUI.print("\n");

	// For debugging!!!
	for( int i = 0; i < router_tables.length; ++i )
	    {
		myGUI.print("nr: " + i + "\t");
		for( int y = 0; y < router_tables[i].length; ++y )
		    {
			myGUI.print(router_tables[i][y] + "\t");
		    }
		myGUI.print("\n");
	    }

	// Need to calculate the new shortest path with our table
	for( int i = 0; i < router_tables.length; ++i )
	    {
		// find all the routes which routes through the changed route
		if( route[i] ==	dest )
		    {
			// resets our value to our direct cost
			cost[i] = direct_cost[i];
			// checks our table for a route to i
			for( int y = 0; y < router_tables[i].length; ++y )
			    {
				// this is the case where we have only 1 node or have not recieved any packets from a node
				if( router_tables[i].length < 2 || (router_tables[y][0] == 0 && router_tables[y][1] == 0) )
				    {
					break;
				    }
				// finds the current smallest cost
				if( cost[i] > direct_cost[y] + router_tables[y][i] )
				    {
					cost[i] = direct_cost[y] + router_tables[y][i];
					route[i] = y;
				    }
			    }
		    }
	    }

	for( int i = 0; i < cost.length; ++i )
	    {
		if( cost[i] != 999 )
		    {
			RouterPacket updated_table;
			if( poison_reverse )
			    {
				int[] temp  = new int[cost.length];
				for( int j = 0; j < direct_cost.length; ++j )
				    {
					if( i == route[j] )
					    {
						temp[j] = 999;
					    }
					else
					    {
						temp[j] = direct_cost[j];
					    }
				    }
				updated_table = new RouterPacket( myID, i, temp);
			    }
			else
			    {
				updated_table = new RouterPacket( myID, i, direct_cost);
			    }
			sendUpdate(  updated_table );
		    }

	    }
    }

}

