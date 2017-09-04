package com.github.ilms49898723.fluigi.device;

import com.github.ilms49898723.fluigi.device.component.BaseComponent;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, BaseComponent> mSymbols;

    public SymbolTable() {
        mSymbols = new HashMap<>();
    }
}
