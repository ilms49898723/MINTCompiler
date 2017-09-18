package com.github.ilms49898723.fluigi.device.symbol;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;

import java.util.*;

public class SymbolTable {
    private Map<String, BaseComponent> mSymbols;
    private List<BaseComponent> mChannels;
    private List<BaseComponent> mComponentsExceptChannel;

    public SymbolTable() {
        mSymbols = new HashMap<>();
        mChannels = new ArrayList<>();
        mComponentsExceptChannel = new ArrayList<>();
    }

    public boolean put(String identifier, BaseComponent component) {
        if (mSymbols.containsKey(identifier)) {
            return false;
        } else {
            mSymbols.put(identifier, component);
            if (component.getType() != ComponentType.CHANNEL) {
                mComponentsExceptChannel.add(component);
            } else {
                mChannels.add(component);
            }
            return true;
        }
    }

    public BaseComponent get(String identifier) {
        return mSymbols.getOrDefault(identifier, null);
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

    public List<BaseComponent> getChannels() {
        return mChannels;
    }

    public List<BaseComponent> getComponentsExceptChannel() {
        return mComponentsExceptChannel;
    }

    public int size() {
        return mSymbols.size();
    }

    public int sizeExceptChannel() {
        return mComponentsExceptChannel.size();
    }
}
