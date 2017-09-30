package com.github.ilms49898723.fluigi.placement.mindistance;

import java.util.*;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceComponent;
import com.github.ilms49898723.fluigi.device.graph.DeviceEdge;
import com.github.ilms49898723.fluigi.device.graph.DeviceGraph;
import com.github.ilms49898723.fluigi.device.symbol.PortDirection;
import com.github.ilms49898723.fluigi.device.symbol.SymbolTable;
import com.github.ilms49898723.fluigi.placement.BasePlacer;
import com.github.ilms49898723.fluigi.placement.overlap.OverlapFixer;
import com.github.ilms49898723.fluigi.processor.parameter.Parameters;

import javafx.geometry.Point2D;

public class MinDistancePlacer extends BasePlacer {
	
	private Map<String, Integer> mLocked;
	private Map<String, Integer> mWeights;

	public MinDistancePlacer(SymbolTable symbolTable, DeviceGraph deviceGraph, Parameters parameters) {
		super(symbolTable, deviceGraph, parameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean placement() {
		// TODO Auto-generated method stub
		return minDistance();
	}
	
	private boolean minDistance() {
		for(BaseComponent c : mSymbolTable.getComponents()) {
			if(!fixComponentPosition(c.getIdentifier())) return false;
		}
		
		return true;
	}
	
	private boolean fixComponentPosition(String id) {
		Point2D newPosition = new Point2D(0, 0);//The new position
		Map<Integer, List<BaseComponent>> connectedComponents = new HashMap<>();
		Map<Integer, List<Point2D>> connectedPorts = new HashMap<>();
		
		//Get connected components
		for(int i = 1 ; i <= mSymbolTable.get(id).getPorts().size() ; i++){
			if(!mSymbolTable.get(id).hasPort(i)) continue;
			
			DeviceComponent src = new DeviceComponent(id, i);
			List<BaseComponent> result1 = new ArrayList<>();
			List<Point2D> result2 = new ArrayList<>();
			Set<DeviceEdge> connectedEdges = mDeviceGraph.edgesOf(src);
			
			if(!connectedEdges.isEmpty()) {
				for(DeviceEdge itr : connectedEdges) {
					DeviceComponent target;
					target = (mDeviceGraph.getEdgeSource(itr) != src) ? mDeviceGraph.getEdgeSource(itr) : mDeviceGraph.getEdgeTarget(itr);
					result1.add(mSymbolTable.get(target.getIdentifier()));
					Point2D desPort = mSymbolTable.get(target.getIdentifier()).getPort(target.getPortNumber());
					result2.add(new Point2D(mSymbolTable.get(target.getIdentifier()).getPositionX() + desPort.getX(), 
							mSymbolTable.get(target.getIdentifier()).getPositionY() + desPort.getY()));
				}
			}
			
			connectedComponents.put(i, result1);
			connectedPorts.put(i, result2);
		}
		
		//Find new position which distance is minimum
		newPosition = getMinimumDistancePoint(id, connectedPorts);
		mLocked.put(id, 1);
		
		List<String> overlapComponents = getOverlapComponents(id, newPosition);
		if (overlapComponents.isEmpty()) {
			mSymbolTable.get(id).setPosition(newPosition);
		} else {
			boolean isValid = true;
			for (int i = 0 ; i < overlapComponents.size() ; i++) {
				if(mLocked.containsKey(overlapComponents.get(i))) {
					isValid = false;
					break;
				}
			}
			
			if(isValid) {
				mSymbolTable.get(id).setPosition(newPosition);
				for(int i = 0 ; i < overlapComponents.size(); i++) {
					fixComponentPosition(overlapComponents.get(i));
				}
			} else {
				Point2D p = OverlapFixer.findNewPosition(mSymbolTable.get(id), mSymbolTable, this.mParameters);
				if(p == null) return false;
				mSymbolTable.get(id).setPosition(p);
			}
		}
		
		return true;
	}
	
	private Point2D getMinimumDistancePoint(String id, Map<Integer, List<Point2D>> connectedPorts) {
		Point2D result = new Point2D(0, 0);
		
		//TODO: Find fitting new position
		Point2D newPosition = new Point2D(0, 0);
		
		double costX = 0, costY = 0;
		for(int i = 1 ; i <= mSymbolTable.get(id).getPorts().size() ; i++) {
			if(!mSymbolTable.get(id).hasPort(i)) continue;
			
			Point2D srcPort = new Point2D(0, 0);//newPosition + port
			Point2D cost = calculateCost(srcPort, mSymbolTable.get(id).getPortDirection(i), connectedPorts.get(i));
			costX += cost.getX();
			costY += cost.getY();
		}
		
		
		return result;
	}
	
	private Point2D calculateCost(Point2D src, PortDirection dir, List<Point2D> connectedPorts) {
		int disX = 0, disY = 0;
		for(int i = 0 ; i < connectedPorts.size() ; i++) {
			double x = connectedPorts.get(i).getX() - src.getX();
			double y = connectedPorts.get(i).getY() - src.getY();
			switch(dir) {
				case LEFT: 
					if(x > 0) disX += 2*x;
					else disX += -1*x;
					disY += Math.abs(y);
					break;
				case RIGHT: 
					if(x < 0) disX += -2*x;
					else disX += x;
					disY += Math.abs(y);
					break;
				case TOP: 
					if(y > 0) disY += 2*y;
					else disY += -1*y;
					disX += Math.abs(x);
					break;
				case BOTTOM: 
					if(y < 0) disY += -2*y;
					else disY += y;
					disX += Math.abs(x);
					break;
			}
		}
		
		return (new Point2D(disX, disY));
	}
	
	private List<String> getOverlapComponents(String id, Point2D newPt) {
		List<String> result = new ArrayList<>();
		int w1 = mSymbolTable.get(id).getWidth();
		int h1 = mSymbolTable.get(id).getHeight();
		
		for(int i = 0 ; i < mSymbolTable.getComponents().size() ; i++) {
			if(id.equals(mSymbolTable.getComponents().get(i).getIdentifier())) continue;
			
			int w2 = mSymbolTable.getComponents().get(i).getWidth();
			int h2 = mSymbolTable.getComponents().get(i).getHeight();
			double distanceX = Math.abs(mSymbolTable.getComponents().get(i).getPositionX() - newPt.getX());
			double distanceY = Math.abs(mSymbolTable.getComponents().get(i).getPositionY() - newPt.getY());
			
			if (distanceX <= (w1 + w2) / 2 && distanceY <= (h1 + h2) / 2) result.add(mSymbolTable.getComponents().get(i).getIdentifier());
		}
		
		return result;
	}
}
