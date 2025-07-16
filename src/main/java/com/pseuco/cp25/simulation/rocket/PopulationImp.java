package com.pseuco.cp25.simulation.rocket;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.model.Statistics;

public class PopulationImp {

    private final boolean traceEnabled;
    private final List<PersonInfo[]> traceList;
    private final Map<Query, Statistics[]> stats;

    public PopulationImp(boolean traceEnabled, int ticks, int numPersons, Collection<Query> queries) {
        this.traceEnabled = false;
        this.traceList = null;
        this.stats = null;
    }

    public List<PersonInfo[]> getTrace() {
        return traceList;
    }

    public Map<Query, Statistics[]> getStats() {
        return stats;
    }
    
}
