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
 * @author cristos criniti
 */
public class ArriveController extends Controller {

    private GameObject target;
    private double maxAcceleration;

    public ArriveController(GameObject target, double maxAcceleration) {
        this.target = target;
        this.maxAcceleration = maxAcceleration;
    }

    private double[] getArriveAcceleration(Car car, double targetRadius, double slowRadius, double deltaTime) {
        double carPosX = car.getX();
        double carPosY = car.getY();

        double[] distanceVector = {target.getX() - carPosX, target.getY() - carPosY};
        double lengthToTarget = lengthOf2DVector(distanceVector);

        if (lengthToTarget < targetRadius)
            return new double[]{0.0, 0.0};
        double targetSpeed;
        if (lengthToTarget < slowRadius)
            targetSpeed = car.getMaxSpeed() * (lengthToTarget/slowRadius);
        else
            targetSpeed = car.getMaxSpeed();
        double[] targetVelocity = {(distanceVector[0]/lengthToTarget)*targetSpeed,(distanceVector[1]/lengthToTarget)*targetSpeed};
        double[] carSpeed = car.getVelocityVector();
        double[] acceleration = {(targetVelocity[0]-carSpeed[0])/deltaTime,(targetVelocity[1]-carSpeed[1])/deltaTime};
        double accelerationValue = lengthOf2DVector(acceleration);
        if (accelerationValue > maxAcceleration) {
            acceleration[0] = (acceleration[0] / accelerationValue) * maxAcceleration;
            acceleration[1] = (acceleration[1] / accelerationValue) * maxAcceleration;
        }
        return acceleration;
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;

        double[] acceleration = getArriveAcceleration(subject,30,200,delta_t);
        double accMag = lengthOf2DVector(acceleration);
        if (accMag != 0) {
            double targetDir = Math.atan2(acceleration[1],acceleration[0]);
            double carAngle = subject.getAngle();
            double diffBetweenAngles = SteeringHelper.getDiffBetweenAngles(targetDir,carAngle);
            double absOfDiffOfAngles = Math.abs(diffBetweenAngles);
            System.out.println(absOfDiffOfAngles);
            if (absOfDiffOfAngles > (3*Math.PI/4))
                controlVariables[VARIABLE_BRAKE] = 0.1;
            else
                controlVariables[VARIABLE_THROTTLE] = accMag;
            if (absOfDiffOfAngles > 1)
                diffBetweenAngles /= absOfDiffOfAngles;
            if (absOfDiffOfAngles < 0.05)
                diffBetweenAngles = 0;
            controlVariables[VARIABLE_STEERING] = diffBetweenAngles;
        }

    }
    
}
