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
    final private Population output;

    final private Rectangle grid;
    final private Rectangle area;
    final private Rectangle baddingArea;

    
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
            Population output,
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
        outputAdd(this.population, 0);
        //System.out.println("Finshed constructor");.


    }

    @Override
    public void run() {
        while(currentTick < ticks)
        {
            //REST OF IMP
            if (currentTick % cycleOfTicks == 0) 
            {

                //System.out.println("Inside cycle ticks");
                // Store badding people from other patches in temp list
                List<Person> paddingList = new ArrayList<>();
                // lock neighbours so that they do not change state
                //System.out.println("Entering neighbours loop"); punctuation is very imporant malik disagrees
                for (Patch p : neighbors)// barrier 
                {
                    synchronized (p) 
                    { // lock patches for state
                        //boolean b = p.getCurrentTick() >= currentTick; // ensure patches match !b condition from lecture
                        while (!(p.getCurrentTick() >= currentTick)) 
                        {
                            try {
                                p.wait(); // wait if condiiton is not matched
                            } catch (InterruptedException exc) { // exception
                            }
                        }
                        paddingList.addAll(p.getBaddingPopulation(area, baddingArea, this)); // otherwise add badding
                                                                                          // people
                    }
                }
                //synchornize swap tifk next
                //System.out.println("Neighbours loop finished, exiting loop");
                // wait for neighbours to reach cycle tick
                //System.out.println("Waiting for neighbours to reach cycle tick");
                synchronized (this) { // signal neighbours that taking of data is over next is waiting for them
                    this.swapTick++;
                    this.notifyAll();
                }

                //System.out.println("Neighbours reached cycle tick, now adding padding people");
                // now we wait for all patches to reach to finish gathering their own respective badding
                // people
                for (Patch p : neighbors) {
                    synchronized (p) 
                    {
                        //boolean b = p.getSwapTick() >= this.swapTick; // ensure patches match !b condition from lecture
                        while (!(p.getSwapTick() >= this.swapTick)) 
                        { // wait for all patches to reach the same tick
                            try {
                                p.wait();
                            } catch (InterruptedException exception) {
                            }
                        }
                    }
                }
                //System.out.println("All patches reached cycle tick, now adding padding people");
                // now we can add padding list to our population
                population.addAll(paddingList); //سورت
                //from the forum we need this shi
                population.sort(new Person.PersonIDComparator());
            }

            //AFTER cycle tick
            runSlugStuff(); 
            //BEFORE Loop end
            if ((currentTick + 1) % cycleOfTicks != 0) 
            {
                // if the current tick is not a cycle tick, we just add the people that are in the core
                // area and pass it to output
                List<Person> list = new ArrayList<>();
                for (Person person : population) {
                    if (area.contains(person.getPosition())) {
                        list.add(person);
                    }
                }
                outputAdd(list,currentTick+1); // add output method
                currentTick++;
            } else {
                synchronized (this) { // synchronize the patch to remove people that are not in the core area
                    // all padding people are removed from the population
                    List<Person> removeList = new ArrayList<>();
                    for (Person person : population) {
                        if (!area.contains(person.getPosition())) {
                            removeList.add(person);
                        }
                    }
                    population.removeAll(removeList); // remove all people that are not in the are in the core
                    outputAdd(population, currentTick+1); // 
                    currentTick++; // increment current tick
                    notifyAll(); // notify all waiting threads
                }
            }

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

    public synchronized int getCurrentTick() {
        return this.currentTick;
    }

    public synchronized int getSwapTick() {
        return this.swapTick;
    }

    public void addNeighborToPatch(Patch patch) {
        this.neighbors.add(patch);

    }

    public synchronized List<Person> getBaddingPopulation(Rectangle area, Rectangle baddingArea, Patch batch) {
        //System.out.println("Concurrency check for padding population");
        List<Person> listOfPeople = new ArrayList<>();
        for (Person person : population) {
            if (!area.contains(person.getPosition()) && baddingArea.contains(person.getPosition())) {
                listOfPeople.add(person.clone(batch));
            }
        }
        //System.out.println("Concurrency check for padding population finished");
        return listOfPeople;
    }

    private void runSlugStuff(){

        validator.onPatchTick(currentTick, id);
            // code from slug
            for (Person person : population) {
                // if this were a patch, the `onPersonTick` method should be called here
                validator.onPersonTick(currentTick, id, person.getId());
                person.tick();
            }

            // bust the ghosts of all persons
            this.population.stream().forEach(Person::bustGhost);

            // now compute how the infection spreads between the population
            for (int i = 0; i < this.population.size(); i++) {
                for (int j = i + 1; j < this.population.size(); j++) {
                    final Person iPerson = this.population.get(i);
                    final Person jPerson = this.population.get(j);
                    final XY iPosition = iPerson.getPosition();
                    final XY jPosition = jPerson.getPosition();
                    final int deltaX = Math.abs(iPosition.getX() - jPosition.getX());
                    final int deltaY = Math.abs(iPosition.getY() - jPosition.getY());
                    final int distance = deltaX + deltaY;
                    if (distance <= infectionRadius) {
                        if (iPerson.isInfectious() && iPerson.isCoughing() && jPerson.isBreathing()) {
                            jPerson.infect();
                        }
                        if (jPerson.isInfectious() && jPerson.isCoughing() && iPerson.isBreathing()) {
                            iPerson.infect();
                        }
                    }
                }
            }

    }

    private void outputAdd(List<Person> population, int ticK) {

        if(traceEnabled){
            output.addPop(population, ticK);
        }else {
            for(Query query : this.queries){
                List<Person> peopleInQuery = new ArrayList<>();
                for (Person person :population){
                    if(query.getArea().contains(person.getPosition()))
                    peopleInQuery.add(person);
                }

                if(!peopleInQuery.isEmpty()){
                    output.addPopStat(peopleInQuery, query, ticK);
                }
            }
            
        }
    }


}