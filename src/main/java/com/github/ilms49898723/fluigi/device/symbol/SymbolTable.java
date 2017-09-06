package com.github.ilms49898723.fluigi.device.symbol;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, BaseComponent> mSymbols;

    public SymbolTable() {
        mSymbols = new HashMap<>();
    }

    public boolean put(String identifier, BaseComponent component) {
        if (mSymbols.containsKey(identifier)) {
            return false;
        } else {
            mSymbols.put(identifier, component);
            return true;
        }
    }

    public BaseComponent get(String identifier) {
        return mSymbols.getOrDefault(identifier, null);
    }

    public boolean containsKey(String identifier) {
        return mSymbols.containsKey(identifier);
    }
}
