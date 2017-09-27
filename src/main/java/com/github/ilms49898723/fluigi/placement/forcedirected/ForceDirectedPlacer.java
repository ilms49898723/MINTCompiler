package com.github.ilms49898723.fluigi.placement.forcedirected;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

import javafx.geometry.Point2D;

public class ForceDirectedPlacer extends BasePlacer{
	
	private List<Integer> mLockedList;
	private Map<String, Integer> mWeights;

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
		List<BaseComponent> connectedComponents = new ArrayList<>();
		
		//Find connected components
		for(int i = 1 ; i <= mSymbolTable.getComponents().get(index).getPorts().size() ; i++){
			if(!mSymbolTable.getComponents().get(index).hasPort(i)) continue;
			
			String id = mSymbolTable.getComponents().get(index).getIdentifier();
			DeviceComponent srcPort = new DeviceComponent(id, i);
			Set<DeviceEdge> connectedEdges = mDeviceGraph.edgesOf(srcPort);
			for(DeviceEdge itr : connectedEdges) {
				String toId = new String();
				toId = (mDeviceGraph.getEdgeSource(itr) != srcPort) ? mDeviceGraph.getEdgeSource(itr).getIdentifier() : mDeviceGraph.getEdgeTarget(itr).getIdentifier();
				connectedComponents.add(mSymbolTable.get(toId));
			}
		}
		
		//Set connected components weight
		for(int i = 0 ; i < connectedComponents.size() ; i++) {
			if(mWeights.containsKey(connectedComponents.get(i).getIdentifier())) continue;
			
			int weight = 0;
			for(int j = 1 ; j <= connectedComponents.get(i).getPorts().size() ; j++) {
				if(!connectedComponents.get(i).hasPort(j)) continue;
				
				DeviceComponent srcPort = new DeviceComponent(connectedComponents.get(i).getIdentifier(), j);
				if (mDeviceGraph.edgesOf(srcPort) != null) weight += 1;
			}
			mWeights.put(connectedComponents.get(i).getIdentifier(), weight);
		}
		
		//calculate force and find zero-force location
		double s1 = 0, s2 = 0, r = 0;
		for(int i = 0 ; i < connectedComponents.size() ; i++) {
			s1 += connectedComponents.get(i).getPositionX() * mWeights.get(connectedComponents.get(i).getIdentifier());
			s2 += connectedComponents.get(i).getPositionY() * mWeights.get(connectedComponents.get(i).getIdentifier());
			r += mWeights.get(connectedComponents.get(i).getIdentifier());
		}
		newPosition = new Point2D(s1 / r, s2 / r);
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
	
	private boolean checkPositionValid(String id, Point2D newPt) {
		
		Point2D LeftTopPt = new Point2D(newPt.getX() - (mSymbolTable.get(id).getWidth() / 2), newPt.getY() - (mSymbolTable.get(id).getHeight() / 2));
		Point2D BottomRightPt = new Point2D(newPt.getX() + (mSymbolTable.get(id).getWidth() / 2), newPt.getY() + (mSymbolTable.get(id).getHeight() / 2));
		
		for(int i = 0 ; i < mSymbolTable.getComponents().size() ; i++) {
			if(id.equals(mSymbolTable.getComponents().get(i).getIdentifier())) continue;
			
			
		}
		
		return true;
	}

}
