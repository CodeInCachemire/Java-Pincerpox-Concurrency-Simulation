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
     * Initialize the number of arrays necessary to capture all PersonInfos throughout the
     * simulation.
     * 
     * @param numTicks   Number of all ticks
     * @param numPersons Number of people in the grid
     */
    public Trace(int numTicks, int numPersons) {
        populations = new ArrayList<>(numTicks + 1);
        for (int i = 0; i <= numTicks; i++) {
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
    public void addPopStat(List<Person> population, Query query, int tick) {
        throw new UnsupportedOperationException("Unimplemented method 'addPopulationStat'");
    }

    @Override
    public List<PersonInfo[]> getPop() {
        return populations;
    }

    @Override
    public Map<Query, Per_tick[]> getStats() {
        throw new UnsupportedOperationException("Unimplemented method 'getStats'");
    }
    
}
