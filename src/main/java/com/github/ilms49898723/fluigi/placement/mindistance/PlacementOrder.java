package com.github.ilms49898723.fluigi.placement.mindistance;

import java.util.*;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.ComponentLayer;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.maputil.MapUtil;

public class PlacementOrder {
	
	List<BaseComponent> mComponents;
	int[][] mConnectedMatrix;
	int n;
	
	public PlacementOrder(SymbolTable symbolTable, DeviceGraph deviceGraph) {
		mComponents = symbolTable.getComponents(ComponentLayer.FLOW);
		n = mComponents.size();
		mConnectedMatrix = new int [n][n];
		
		for(int i = 0 ; i < n ; i++) {
			for(int j = 0 ; j < n ; j++) {
				mConnectedMatrix[i][j] = 0;
			}
		}
		
		for (int i = 0 ; i < n ; i++) {
			String id = mComponents.get(i).getIdentifier();
			for (int j = 1 ; j <= mComponents.get(i).getNumPorts() ; j++) {
				if (!mComponents.get(i).hasPort(j)) continue;
				
				DeviceComponent src = new DeviceComponent(id, j);
				Set<DeviceEdge> connectedEdges = deviceGraph.edgesOf(src);
				if(!connectedEdges.isEmpty()) {
					for (DeviceEdge itr : connectedEdges) {
	                    DeviceComponent target;
	                    int targetIdx = i;
	                    
	                    target = (!(deviceGraph.getEdgeSource(itr).equals(src))) ? deviceGraph.getEdgeSource(itr) : deviceGraph.getEdgeTarget(itr);
	                    for(int k = 0 ; k < n ; k++) {
	                    	if(mComponents.get(k).getIdentifier().equals(target.getIdentifier())) {
	                    		targetIdx = k;
	                    		break;
	                    	}
	                    }
	                    
	                    if(targetIdx != i) {
	                    	mConnectedMatrix[i][targetIdx] = 1;
	                    	mConnectedMatrix[targetIdx][i] = 1;
	                    }
	                }
				}
				
			}
		}
	}

	public List<String> getStaticOrder() {
		Map<String, Integer> connectedNum = new HashMap<>();
		for (int i = 0 ; i < n ; i++) {
			int num = 0;
			for (int j = 0 ; j < n ; j++) {
				if(mConnectedMatrix[i][j] == 1) num++;
			}
			connectedNum.put(mComponents.get(i).getIdentifier(), num);
		}
		
		Map<String, Integer> sortedMap = MapUtil.sortByValue(connectedNum);
		ArrayList<String> result = new ArrayList<>();
		for (String itr : sortedMap.keySet()) {
			result.add(itr);
		}
		Collections.reverse(result);
		
		return result;
	}
	
	public List<String> getDynamicOrder(Map<String, Integer> locked) {
		Map<String, Integer> connectedNum = new HashMap<>();
		for (int i = 0 ; i < n ; i++) {
			int num = 0;
			for (int j = 0 ; j < n ; j++) {
				if(mConnectedMatrix[i][j] == 1 && locked.containsKey(mComponents.get(j).getIdentifier())) num++;
			}
			connectedNum.put(mComponents.get(i).getIdentifier(), num);
		}
		
		Map<String, Integer> sortedMap = MapUtil.sortByValue(connectedNum);
		ArrayList<String> result = new ArrayList<>();
		for (String itr : sortedMap.keySet()) {
			result.add(itr);
		}
		Collections.reverse(result);
		
		return result;
	}
	
}
