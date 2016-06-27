package com.example.battleship;

import java.util.*;

public class CoOrdinate {
	int x;
	int y;
	
	public CoOrdinate()
	{
		
	}
	
	public CoOrdinate(int x,int y)
	{
		this.x=x;this.y=y;
	}
}

class CoOrdinateList{
	LinkedList<CoOrdinate> CoOrdList = new LinkedList<CoOrdinate>();
}