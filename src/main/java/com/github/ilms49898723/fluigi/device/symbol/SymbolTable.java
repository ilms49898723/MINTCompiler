package com.github.ilms49898723.fluigi.device.symbol;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;
import com.github.ilms49898723.fluigi.device.component.Valve;

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
            if (component.getType() == ComponentType.CHANNEL) {
                mChannels.add(component);
            } else {
                mComponents.add(component);
            }
            return true;
        }
    }

    public BaseComponent get(String identifier) {
        return mSymbols.getOrDefault(identifier, null);
    }

    public void remove(String identifier) {
        for (int i = 0; i < mComponents.size(); ++i) {
            if (mComponents.get(i).getIdentifier().equals(identifier)) {
                mComponents.remove(i);
                break;
            }
        }
        for (int i = 0; i < mChannels.size(); ++i) {
            if (mChannels.get(i).getIdentifier().equals(identifier)) {
                mChannels.remove(i);
                break;
            }
        }
        mSymbols.remove(identifier);
    }

    public boolean containsKey(String identifier) {
        return mSymbols.containsKey(identifier);
    }

    public Set<String> keySet() {
        return mSymbols.keySet();
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

    public List<BaseComponent> getValves() {
        List<BaseComponent> components = new ArrayList<>();
        for (BaseComponent component : mComponents) {
            if (component.getType() == ComponentType.VALVE) {
                components.add(component);
            }
        }
        return components;
    }

    public void replaceValveChannel(String original, String replace) {
        for (BaseComponent component : getValves()) {
            Valve valve = (Valve) component;
            if (valve.getChannelId().equals(original)) {
                valve.setChannelId(replace);
            }
        }
    }

    public int size() {
        return mSymbols.size();
    }
}
