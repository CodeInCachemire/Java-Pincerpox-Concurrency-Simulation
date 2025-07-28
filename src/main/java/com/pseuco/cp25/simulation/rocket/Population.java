package com.pseuco.cp25.simulation.rocket;

import java.util.List;
import java.util.Map;

import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.simulation.common.Person;
import com.pseuco.cp25.simulation.rocket.Statistic.Per_tick;

public interface Population {
    /**
     * Adds population trace for a specific tick.
     * @param population
     * @param tick
     */
    public void addPop(List<Person> population, int tick);

    /**
     * Adds population statistics for a specific query and tick.
     * 
     * @param population The population to add statistics for
     * @param query      The query for which the statistics are collected
     * @param tick       The current tick in the simulation
     */
    public void addPopStat(List<Person> population, Query query, int tick);

    /**
     * Returns the population trace for each tick.
     * 
     * @return A list of PersonInfo arrays, where each array corresponds to a tick
     */
    public List<PersonInfo[]> getPop();

    /**
     * Returns the statistics for each query.
     * 
     * @return A map where the key is a Query and the value is an array of Per_tick
     *         objects containing statistics for that query
     */
    public Map<Query, Per_tick[]> getStats();

}

