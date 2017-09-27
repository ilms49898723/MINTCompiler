package com.github.ilms49898723.fluigi.device.symbol;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;

import java.util.*;

public class SymbolTable {
    private Map<String, BaseComponent> mSymbols;
    private List<BaseComponent> mComponents;
    private List<BaseComponent> mChannels;

    public SymbolTable() {
        mSymbols = new HashMap<>();
        mComponents = new ArrayList<>();
        mChannels = new ArrayList<>();
    }

    public boolean put(String identifier, BaseComponent component) {
        if (mSymbols.containsKey(identifier)) {
            return false;
        } else {
            mSymbols.put(identifier, component);
            if (component.getType() != ComponentType.CHANNEL) {
                mComponents.add(component);
            } else {
                mChannels.add(component);
            }
            return true;
        }
    }

    public BaseComponent get(String identifier) {
        return mSymbols.getOrDefault(identifier, null);
    }

    public BaseComponent remove(String identifier) {
        return mSymbols.remove(identifier);
    }

    public boolean containsKey(String identifier) {
        return mSymbols.containsKey(identifier);
    }

    public Set<String> keySet() {
        return mSymbols.keySet();
    }

    public Collection<BaseComponent> values() {
        return mSymbols.values();
    }

    public List<BaseComponent> getComponents() {
        return mComponents;
    }

    public List<BaseComponent> getComponents(ComponentLayer layer) {
        List<BaseComponent> result = new ArrayList<>();
        for (BaseComponent component : mComponents) {
            if (component.getLayer() == layer) {
                result.add(component);
            }
        }
        return result;
    }

    public List<BaseComponent> getChannels() {
        return mChannels;
    }

    public List<BaseComponent> getChannels(ComponentLayer layer) {
        List<BaseComponent> result = new ArrayList<>();
        for (BaseComponent channel : mChannels) {
            if (channel.getLayer() == layer) {
                result.add(channel);
            }
        }
        return result;
    }

    public int size() {
        return mSymbols.size();
    }

    public int componentSize() {
        return mComponents.size();
    }
}
