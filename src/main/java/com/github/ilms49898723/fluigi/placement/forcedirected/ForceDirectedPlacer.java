package com.github.ilms49898723.fluigi.placement.forcedirected;

import java.util.ArrayList;
import java.util.List;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

import javafx.geometry.Point2D;

public class ForceDirectedPlacer extends BasePlacer{
	
	private List<Integer> mLockedList;

	public ForceDirectedPlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
		super(symbolTable, deviceGraph, parameters);
		// TODO Auto-generated constructor stub
		mLockedList = new ArrayList<>();
		for (int i = 0 ; i < mSymbolTable.getComponents().size() ; i++) {
			mLockedList.add(0);
		}
	}

	@Override
	public boolean placement() {
		// TODO Auto-generated method stub
		return forceDirected();
	}
	
	private boolean forceDirected() {
		for (int i = 0 ; i < mSymbolTable.getComponents().size() ; i++) {
			if(mLockedList.get(i) == 1) continue;
			fixSingleComponentPosition(i);
		}
		
		
		return true;
	}
	
	private boolean fixSingleComponentPosition (int index) {
		if(mLockedList.get(index) == 1) return false;
		
		Point2D newPosition = new Point2D(0, 0);//The new position
		for(int i = 0 ; i < mSymbolTable.getComponents().get(index).getPorts().size() ; i++){
			if(!mSymbolTable.getComponents().get(index).hasPort(i)) continue;
			String id = mSymbolTable.getComponents().get(index).getIdentifier();
			
			
		}
		//Set<DeviceEdge> connectedEdges = mDeviceGraph.edgesOf(source);
		List<BaseComponent> connectedComponents = new ArrayList<>();
		//calculate force and find zero-force location
		mLockedList.set(index, 1);
		/*****
		if (new position is valid) {
			return true;
		} else {
			get the component caused new position invalid
			if(invalid component locked) {
				fixNearestValidPosition(index);
			} else {
				fixSingleComponentPosition(invalid id)
			}
		}
		*****/
		return true;
	}
	
	private boolean fixNearestValidPosition (int index) {
		//find the smallest force and valid position
		mLockedList.set(index, 1);
		return true;
	}

}
