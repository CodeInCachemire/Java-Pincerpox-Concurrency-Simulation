package com.pseuco.cp25.simulation.rocket;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.simulation.common.Person;

public class Statistic implements Population {

    
    public class Per_tick {

        private long numSus = 0;
        private long numInf = 0;
        private long numInfect = 0;
        private long numRecov = 0;

        public long getNumSus() {
            return numSus;
        }

        public long getNumInf() {
            return numInf;
        }

        public long getNumInfectious() {
            return numInfect;
        }

        public long getNumRecov() {
            return numRecov;
        }

        public void addSus() {
            numSus++;
        }

        public void addInf() {
            numInf++;
        }

        public void addInfectious() {
            numInfect++;
        }

        public void addRecov() {
            numRecov++;
        }

    }

    private final Map<Query, Per_tick[]> stats;

    /**
     * Initialize an array of quadruples for each query given.
     * 
     * @param numTicks Number of all ticks
     * @param queries  The queries specified in the scenario
     */
    public Statistic(int numTicks, Collection<Query> queries) {
        this.stats = new HashMap<>(queries.size());
        for (Query query : queries) {
            Per_tick[] quadruples = new Per_tick[numTicks + 1];
            for (int i = 0; i <= numTicks; i++) {
                quadruples[i] = new Per_tick();
            }
            stats.put(query, quadruples);
        }
    }

    @Override
    public void addPop(List<Person> population, int tick) {
        throw new UnsupportedOperationException("Unimplemented method 'addPopulationStat'");
    }

    @Override
    public synchronized void addPopStat(List<Person> population, Query query, int tick) {
        for (Person person : population) {
            if (person.isSusceptible()) {
                stats.get(query)[tick].addSus();
            } else if (person.isInfected()) {
                stats.get(query)[tick].addInf();
            } else if (person.isInfectious()) {
                stats.get(query)[tick].addInfectious();
            } else {
                stats.get(query)[tick].addRecov();
            }
        }
    }

    @Override
    public List<PersonInfo[]> getPop() {
        throw new UnsupportedOperationException("Unimplemented method 'getPopulations'");
    }

    @Override
    public Map<Query, Per_tick[]> getStats() {
        return stats;
    }

}
