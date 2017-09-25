package com.github.ilms49898723.fluigi.placement.forcedirected;

import java.util.ArrayList;
import java.util.List;

import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

import javafx.geometry.Point2D;

public class ForceDirectedPlacer extends BasePlacer{
	
	private List<Integer> mCheckList;

	public ForceDirectedPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
		super(symbolTable, deviceGraph, parameters);
		// TODO Auto-generated constructor stub
		mCheckList = new ArrayList<>();
		for (int i = 0 ; i < mSymbolTable.size() ; i++) {
			mCheckList.add(0);
		}
	}

	@Override
	public boolean placement() {
		// TODO Auto-generated method stub
		return forceDirected();
	}
	
	private boolean forceDirected() {
		
		for (int i = 0 ; i < mSymbolTable.size() ; i++) {
			if(mCheckList.get(i) == 1) continue;
			fixSingleComponentPosition(i);
		}
		
		
		return true;
	}
	
	private boolean fixSingleComponentPosition (int index) {
		if(mCheckList.get(index) == 1) return false;
		
		Point2D newPosition = new Point2D(0, 0);//The new position
		//calculate force and find zero-force location
		mCheckList.set(index, 1);
		/*****
		if (new position is valid) {
			return true;
		} else {
			get the component caused new position invalid
			if(!fixSingleComponentPosition(invalid idx)) {
				fixNearestValidPosition(index);
			}
		}
		*****/
		return true;
	}
	
	private boolean fixNearestValidPosition (int index) {
		//find the smallest force and valid position
		mCheckList.set(index, 1);
		return true;
	}

}
