package com.pseuco.cp25.simulation.rocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pseuco.cp25.model.Output;
import com.pseuco.cp25.model.Rectangle;
import com.pseuco.cp25.model.Scenario;
import com.pseuco.cp25.model.Statistics;
import com.pseuco.cp25.model.TraceEntry;
import com.pseuco.cp25.simulation.common.Simulation;
import com.pseuco.cp25.validator.InsufficientPaddingException;
import com.pseuco.cp25.validator.Validator;

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
    public void run() {
        throw new RuntimeException("not implemented");
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

    private void threads(){
        //THREAD stuff
        List<Patch> patchList = new ArrayList<>();

        Iterator<Rectangle> patchAreas = Utils.getPatches(scenario);
    }





}
