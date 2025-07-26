package com.pseuco.cp25.simulation.rocket;

import java.util.List;
import java.util.Map;

import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.simulation.common.Person;
import com.pseuco.cp25.simulation.rocket.Statistic.Per_tick;

public interface Population {
    /**
     * Used to receive a population from some patch and converting it into an output.
     * 
     * @param population The current population of some patch
     * @param tick       The current tick
     */
    public void addPop(List<Person> population, int tick);

    /**
     * Used to receive a population from some patch and converting it into an output.
     * In the case of not wanting a trace
     * 
     * @param population The population of the given query
     * @param query      Some query
     * @param tick       The current tick
     */
    public void addPopStat(List<Person> population, Query query, int tick);

    /**
     * Returns all populations of the whole grid for each tick.
     * 
     * @return List of all populations per tick
     */
    public List<PersonInfo[]> getPop();

    /**
     * Returns all necessary numbers per tick to use when no trace is asked for.
     * 
     * @return A map from some query to its corresponding list of statistics per tick
     */
    public Map<Query, Per_tick[]> getStats();

}

