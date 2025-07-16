package com.pseuco.cp25.simulation.rocket;

import com.pseuco.cp25.model.Parameters;
import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.model.Rectangle;
import com.pseuco.cp25.model.XY;
import com.pseuco.cp25.simulation.common.Context;
import com.pseuco.cp25.simulation.common.Person;
import com.pseuco.cp25.validator.Validator;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Patch implements Runnable, Context {

    final private int ticks;
    final private int cycleOfTicks;
    final private int infectionRadius;
    final private int id;

    final private List<Rectangle> obstacles;
    final private List<Query> queries;
    final private List<Person> population;
    final private List<Patch> neighbors = new ArrayList<>();
    final private boolean traceEnabled;
    //final private Populations output;
    final private PopulationImp output;

    final private Rectangle grid;
    final private Rectangle area;
    final private Rectangle baddingArea;

    // may need to add some stuff for statistacs
    final private Validator validator;
    private int currentTick = 0;
    private int swapTick = 0;

    /**
     * Creates a new patch with the given parameters.
     *
     * @param id           the id of the patch
     * @param ticks        the number of ticks to run
     * @param cycleOfTicks the total number of ticks in the simulation
     * @param parameters   the parameters for the simulation
     * @param grid         the grid of the patch
     * @param area         the area of the patch
     * @param baddingArea  the bounding area of the patch
     * @param obstacles    the obstacles in the patch
     * @param queries      the queries for the patch
     * @param population   the population in the patch
     * @param validator    the validator for the patch
     */
    public Patch(
            int id,
            int ticks,
            int cycleOfTicks,
            Parameters parameters,
            Rectangle grid,
            Rectangle area,
            Rectangle baddingArea,
            List<Rectangle> obstacles,
            Map<String, Query> queries,
            List<PersonInfo> population,
            Validator validator,
            PopulationImp output,
            boolean traceEnabled) { // output and trace to be
                                   // added later
        this.id = id;
        this.ticks = ticks;
        this.cycleOfTicks = cycleOfTicks;
        this.infectionRadius = parameters.getInfectionRadius();
        this.grid = grid;
        this.area = area;
        this.baddingArea = baddingArea;
        this.obstacles = obstacles;
        this.traceEnabled = traceEnabled;
        this.output = output; // output to be added later
        this.validator = validator;

        this.queries = new ArrayList<>();
        for (Map.Entry<String, Query> entry : queries.entrySet()) { // debug
            Query query = entry.getValue();
            if (area.overlaps(query.getArea())) {
                this.queries.add(query);
            }
        }

        this.population = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < population.size(); i++) {
            PersonInfo personInfo = population.get(i);
            if (area.contains(personInfo.getPosition())) {
                Person person = new Person(i, this, parameters, personInfo);
                this.population.add(person);
            }
        }
        
        //Maybe need to add something for output here
    }

    @Override
    public void run() {
        while(currentTick < ticks){
            //REST OF IMP
            if (currentTick % cycleOfTicks == 0) {

            }

            //AFTER cycle tick

            //BEFORE Loop end

        }
    }

    @Override
    public Rectangle getGrid() {
        return this.grid;
    }

    @Override
    public List<Rectangle> getObstacles() {
        return this.obstacles;
    }

    @Override
    public List<Person> getPopulation() {
        return this.population;
    }

    public Rectangle getBaddingArea() {
        return baddingArea;
    }

    public Rectangle getArea() {
        return area;
    }

    public int getId() {
        return id;
    }
}