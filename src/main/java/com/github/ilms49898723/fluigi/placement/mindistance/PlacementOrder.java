package com.github.ilms49898723.fluigi.placement.mindistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;

public class PlacementOrder {
	
	int[][] connectedMatrix;
	int n;
	
	public PlacementOrder(SymbolTable symbolTable, DeviceGraph deviceGraph) {
		List<BaseComponent> components = symbolTable.getComponents(ComponentLayer.CONTROL);
		n = components.size();
		connectedMatrix = new int [n][n];
		
		for(int i = 0 ; i < n ; i++) {
			for(int j = 0 ; j < n ; j++) {
				connectedMatrix[i][j] = 0;
			}
		}
		
		for (int i = 0 ; i < n ; i++) {
			String id = components.get(i).getIdentifier();
			for (int j = 1 ; j <= components.get(i).getNumPorts() ; j++) {
				if (!components.get(i).hasPort(j)) continue;
				
				DeviceComponent src = new DeviceComponent(id, j);
				Set<DeviceEdge> connectedEdges = deviceGraph.edgesOf(src);
				if(!connectedEdges.isEmpty()) {
					for (DeviceEdge itr : connectedEdges) {
	                    DeviceComponent target;
	                    int targetIdx = i;
	                    
	                    target = (deviceGraph.getEdgeSource(itr) != src) ? deviceGraph.getEdgeSource(itr) : deviceGraph.getEdgeTarget(itr);
	                    for(int k = 0 ; k < n ; k++) {
	                    	if(components.get(k).getIdentifier() == target.getIdentifier()) {
	                    		targetIdx = k;
	                    		break;
	                    	}
	                    }
	                    
	                    if(targetIdx != i) {
	                    	connectedMatrix[i][targetIdx] = 1;
	                    	connectedMatrix[targetIdx][i] = 1;
	                    }
	                }
				}
				
			}
		}
	}

	public List<Integer> getStaticOrder() {
		
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < n ; j++) {
				
			}
		}
		return new ArrayList<Integer>();
	}
	
	public List<Integer> getDynamicOrder(Map<String, Integer> locked) {
		return new ArrayList<Integer>();
	}
	
}
