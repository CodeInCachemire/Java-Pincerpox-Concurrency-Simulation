package com.pseuco.cp25.simulation.rocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.simulation.common.Person;
import com.pseuco.cp25.simulation.rocket.Statistic.Per_tick;

public class Trace implements Population {

    private final List<PersonInfo[]> populations;

    /**
     * Constructs a Trace object that will 
     * hold the population of each tick.
     * 
     * @param ticksNum  The number of ticks in the simulation
     * @param numPersons The number of persons in the simulation
     */
    public Trace(int ticksNum, int numPersons) {
        populations = new ArrayList<>(ticksNum + 1);
        for (int i = 0; i <= ticksNum; i++) {
            populations.add(new PersonInfo[numPersons]);
        }
    }

    @Override
    public synchronized void addPop(List<Person> population, int tick) {
        for (Person person : population) {
        populations.get(tick)[person.getId()] = person.getInfo();
        }
    }

    @Override
    public List<PersonInfo[]> getPop() {
        return populations;
    }
    
    @Override
    public void addPopStat(List<Person> population, Query query, int tick) {
        throw new UnsupportedOperationException("Unimplemented method 'addPopulationStat'");
    }

    
    @Override
    public Map<Query, Per_tick[]> getStats() {
        throw new UnsupportedOperationException("Unimplemented method 'getStats'");
    }
    
}
