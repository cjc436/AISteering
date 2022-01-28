/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;

/**
 *
 * @author Cristos Criniti
 */
public class SeekController extends Controller {

    private GameObject target;

    public SeekController(GameObject target) {
        this.target = target;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;

        double[] distanceToTarget = SteeringHelper.getSeekDirectionVector(subject,target);
        double targetDir = Math.atan2(distanceToTarget[1],distanceToTarget[0]);
        double carAngle = subject.getAngle();
        double diffBetweenAngles = SteeringHelper.getDiffBetweenAngles(targetDir,carAngle);
        double absOfDiffOfAngles = Math.abs(diffBetweenAngles);
        if (absOfDiffOfAngles > (Math.PI/2))
            controlVariables[VARIABLE_BRAKE] = 0.1;
        else
            controlVariables[VARIABLE_THROTTLE] = 1;
        if (absOfDiffOfAngles > 1)
            diffBetweenAngles /= absOfDiffOfAngles;
        if (absOfDiffOfAngles < 0.05)
            diffBetweenAngles = 0;
        controlVariables[VARIABLE_STEERING] = diffBetweenAngles;
    } 
    
}
