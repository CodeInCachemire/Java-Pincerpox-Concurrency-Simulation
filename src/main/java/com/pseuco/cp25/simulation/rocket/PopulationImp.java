package com.pseuco.cp25.simulation.rocket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.model.Statistics;
import com.pseuco.cp25.simulation.common.Person;

public class PopulationImp {

    private final boolean traceEnabled;
    private final List<PersonInfo[]> traceList;
    private final Map<Query, Statistics[]> stats;
    private final Map<Query, Object> statLocks;
    public PopulationImp(boolean traceEnabled, int ticks, int numPersons, Collection<Query> queries) {
        this.traceEnabled = traceEnabled;
        this.traceList = traceEnabled ? new ArrayList<>(ticks + 1) : null;
        this.stats = !traceEnabled ? new HashMap<>() : null;
        this.statLocks = !traceEnabled ? new ConcurrentHashMap<>() : null;


        if (traceEnabled) {
            for (int i = 0; i <= ticks; i++) {
                traceList.add(new PersonInfo[numPersons]);
            }
        } else {
            for (Query q : queries) {
                Statistics[] stat = new Statistics[ticks + 1];
                stats.put(q, stat);
                statLocks.put(q, new Object());
            }
        }
    }

    //TRACE STATISTICS only.
    public  void addPopulation(List<Person> people, int tick) {
        if (traceEnabled) {
            for (Person p : people) {
                traceList.get(tick)[p.getId()] = p.getInfo();
            }
        }
    }


    //POPSTATS NORMAL not trrace
    public  void addPopulationStat(List<Person> people, Query query, int tick) {
        if (!traceEnabled) {
            long sus = 0, inf = 0, infe = 0, rec = 0;
            //to od  rest
            for (Person p : people) {
                if (p.isSusceptible()) sus++;
                else if (p.isInfected()) inf++;
                else if (p.isInfectious()) infe++;
                else rec++;
            }
            Statistics stat = new Statistics(sus, inf, infe, rec);


            Object lock = statLocks.get(query);
            synchronized (lock) {
                stats.get(query)[tick] = stat;
            }
            
        }
    }




    public List<PersonInfo[]> getTrace() {
        return traceList;
    }

    public Map<Query, Statistics[]> getStats() {
        return stats;
    }
    
}
