package com.pseuco.cp25.simulation.rocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pseuco.cp25.model.Output;
import com.pseuco.cp25.model.PersonInfo;
import com.pseuco.cp25.model.Query;
import com.pseuco.cp25.model.Rectangle;
import com.pseuco.cp25.model.Scenario;
import com.pseuco.cp25.model.Statistics;
import com.pseuco.cp25.model.TraceEntry;
import com.pseuco.cp25.model.XY;
import com.pseuco.cp25.simulation.common.Simulation;
import com.pseuco.cp25.simulation.rocket.Statistic.Per_tick;
import com.pseuco.cp25.validator.InsufficientPaddingException;
import com.pseuco.cp25.validator.Validator;
import static com.pseuco.cp25.simulation.common.Utils.mayPropagateFrom;

/**
 * Your implementation shall go into this class.
 *
 * <p>
 * This class has to implement the <em>Simulation</em> interface.
 * </p>
 */
public class Rocket implements Simulation 
{

    final private Scenario scenario;
    final private int badding;
    final private Validator validator;
    final private int cycleOfTicks;
    final private List<TraceEntry> trace = new ArrayList<>();
    final private Map<String, List<Statistics>> statistics = new HashMap<>();

    /**
     * Constructs a rocket with the given parameters.
     *
     * <p>
     * You must not change the signature of this constructor.
     * </p>
     *
     * <p>
     * Throw an insufficient padding exception if and only if the padding is
     * insufficient. Hint: Depending on the parameters, some amount of padding
     * is required even if one only computes one tick concurrently. The padding
     * is insufficient if the provided padding is below this minimal required
     * padding.
     * </p>
     *
     * @param scenario  The scenario to simulate.
     * @param padding   The padding to be used.
     * @param validator The validator to be called.
     */
    public Rocket(Scenario scenario, int padding, Validator validator) throws InsufficientPaddingException {
        // your concurrent implementation goes here
        this.scenario = scenario;
        this.badding = padding;
        this.validator = validator;

        cycleOfTicks = infectionUncertainity();
        if (cycleOfTicks == 0) {
            throw new InsufficientPaddingException(badding);
        }
        //System.out.println("Tick is caclculated and accepted");
        initializeStatistics();
        //System.out.println("Statsistics initialized");
    }
    

    @Override
    public Output getOutput() {
        //System.out.println("getOutput called");
        return new Output(scenario, trace, statistics);
    }

@Override
    public void run() 
    {
        //call threads here incomplete noww
        //threads(output);
        //THREADS FUNC
        if (scenario.getTrace()) {
            // Store trace entries
            Population output = new Trace(scenario.getTicks(), scenario.getPopulation().size());
            threads(output);
                //finish rest of this to get num infected etc output ddeets
            for (PersonInfo[] tr : output.getPop()) {
                this.trace.add(new TraceEntry(Arrays.asList(tr)));
            }

            for (PersonInfo[] gtrace : output.getPop()) {
                for (Map.Entry<String, Query> entry : scenario.getQueries().entrySet()) {
                    final Query query = entry.getValue();
                    long sus = 0, inf = 0, infe = 0, rec = 0;
                    for (PersonInfo info : gtrace) {
                        if (query.getArea().contains(info.getPosition())) {
                            switch (info.getInfectionState().getState()) {
                                case SUSCEPTIBLE -> sus++;
                                case INFECTED    -> inf++;
                                case INFECTIOUS  -> infe++;
                                case RECOVERED   -> rec++;
                            }
                        }
                    }
                    this.statistics.get(entry.getKey())
                        .add(new Statistics(sus, inf, infe, rec));
                }
            }
        } else {
            Population output = new Statistic(scenario.getTicks(), scenario.getQueries().values());
            threads(output);
            Map<Query, Per_tick[]> stats = output.getStats();
            //finish rest of run
            for (int i = 0; i <= scenario.getTicks(); i++) {
                for (Map.Entry<String, Query> entry : this.scenario.getQueries().entrySet()) {
                    final Per_tick[] per_ticks = stats.get(entry.getValue());
                    this.statistics.get(entry.getKey())
                            .add(new Statistics(per_ticks[i].getNumSus(),
                                    per_ticks[i].getNumInf(),
                                    per_ticks[i].getNumInfectious(),
                                    per_ticks[i].getNumRecov()));
                }
            }
        }
            
    }

    private int infectionUncertainity() 
    {
        //System.out.println("infectionUncertainity called");
        int T = scenario.getParameters().getIncubationTime();
        int R = scenario.getParameters().getInfectionRadius(); // infection radius

        int distance = 2 + R;
        if (distance > badding)
            return 0;

        int tick = 1;
        int timeSinceInfectious = 1;

        while (distance <= badding) {
            if (timeSinceInfectious == T) {
                distance += 2 + R;
                timeSinceInfectious = 1;
            } else {
                distance += 2;
                timeSinceInfectious++;
            }

            if (distance > badding)
                break;
            tick++;
        }
        //System.out.println("Tick is caclculated");
        return tick;
    }

    private void initializeStatistics() {
        //System.out.println("initializeStatistics called");
        // we initialize the map we use to collect the necessary statistics
        for (String queryKey : this.scenario.getQueries().keySet()) {
            this.statistics.put(queryKey, new ArrayList<>());
        }
        //System.out.println("initializeStatistics completed");
    }

    private void threads(Population output)
    {
        //THREAD stuff
       
        List<Patch> patchList = new ArrayList<>();

        Iterator<Rectangle> patchAreas = Utils.getPatches(scenario);
        int patchId = 0;

        while (patchAreas.hasNext()) {
            Rectangle patchArea = patchAreas.next(); // get za patch
            XY TL1 = patchArea.getTopLeft(); // top left corner of the patch starting point
            XY TL2 = TL1.sub(badding); // now add padding to the core of the patch
            XY BR = scenario.getGrid().getBottomRight(); // now find the bottom right corner of the grid
            XY TL3 = TL2.limit(XY.ZERO, BR); // limit the top left corner to the grid size
            XY sizeOfPatch = patchArea.getBottomRight().add(badding).limit(XY.ZERO, BR).sub(TL3);
            Rectangle paddingArea = new Rectangle(TL3, sizeOfPatch); // now create the padding area

            Patch patch = new Patch(
                    patchId, 
                    scenario.getTicks(),
                    cycleOfTicks,
                    scenario.getParameters(), 
                    scenario.getGrid(), 
                    patchArea, 
                    paddingArea, 
                    scenario.getObstacles(), 
                    scenario.getQueries(), 
                    scenario.getPopulation(), 
                    validator, 
                    output , 
                    scenario.getTrace()
                    );
            patchList.add(patch); // add the patch to the list of patches
            patchId++;
        }

        for (int i = 0; i < patchList.size(); i++) {
            Patch patchOne = patchList.get(i);
            Iterator<Rectangle> otherPatch = Utils.getPatches(scenario);
            int otherPatchId = -1;

            while (otherPatch.hasNext()) {
                Rectangle otherArea = otherPatch.next();
                otherPatchId++;
                if (i == otherPatchId) {
                    continue; // skip the patch itself
                }

                Patch patchTwoID = patchList.get(otherPatchId);
                if (patchOne.getBaddingArea().overlaps(otherArea)
                        && mayPropagateFrom(scenario, patchOne.getArea(), otherArea)) {
                    patchOne.addNeighborToPatch(patchTwoID); //add nieghbor patfch to our neighbiur patches
                }

            }

        }
        
        startAndJoinThreads(patchList);
        //Initialize neighbourds logic 


    }

    private void startAndJoinThreads(List<Patch> patchList){
        // start all patch threads and join
        List<Thread> threads = new ArrayList<>(); 
        for (Patch patch : patchList) {
            Thread thread = new Thread(patch);
            threads.add(thread);
            thread.start(); 
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException exception) {
            }
        }
    }

}
