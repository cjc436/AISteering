/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import engine.Car;
import engine.Game;
import engine.GameObject;
import engine.RotatedRectangle;

import java.util.HashSet;

/**
 *
 * @author Cristos Criniti
 */
public class WallAvoidanceSeekController extends Controller {

    private GameObject target;

    public WallAvoidanceSeekController(GameObject target) {
        this.target = target;
    }

    private double[] getWallAvoidanceVector(Car car, Game game) {
        RotatedRectangle carRect = car.getCollisionBox();
        HashSet<GameObject> cars = new HashSet<>();
        cars.add(car);
        cars.add(target);

        double carAngle = car.getAngle();
        if (car.getSpeed() < 0) {
            carAngle += Math.PI;
        }

        // check for wall 45 degrees to the right
        double angleOfProjection = carAngle + (Math.PI/4);
        for (int i = 1; i < 5; i++) {
            RotatedRectangle collisionRect = new RotatedRectangle(carRect.C.getX()+(i*20*Math.cos(angleOfProjection)),carRect.C.getY()+(i*20*Math.sin(angleOfProjection)),carRect.S.getX(),carRect.S.getY(),angleOfProjection);

            GameObject collision = game.collision(collisionRect,cars);
            if (collision != null) {
                double translatedAngle = carAngle - (Math.PI/2);
                double[] dirVector = {Math.cos(translatedAngle),Math.sin(translatedAngle)};
                return dirVector;
            }
        }
        // check for wall 45 degrees to the left
        angleOfProjection = carAngle - (Math.PI/4);
        for (int i = 1; i < 5; i++) {
            RotatedRectangle collisionRect = new RotatedRectangle(carRect.C.getX()+(i*20*Math.cos(angleOfProjection)),carRect.C.getY()+(i*20*Math.sin(angleOfProjection)),carRect.S.getX(),carRect.S.getY(),angleOfProjection);

            GameObject collision = game.collision(collisionRect,cars);
            if (collision != null) {
                double translatedAngle = carAngle + (Math.PI/2);
                double[] dirVector = {Math.cos(translatedAngle),Math.sin(translatedAngle)};
                return dirVector;
            }
        }
        // check for wall straight ahead
        angleOfProjection = carAngle;
        for (int i = 1; i < 5; i++) {
            RotatedRectangle collisionRect = new RotatedRectangle(carRect.C.getX()+(i*20*Math.cos(angleOfProjection)),carRect.C.getY()+(i*20*Math.sin(angleOfProjection)),carRect.S.getX(),carRect.S.getY(),angleOfProjection);

            GameObject collision = game.collision(collisionRect,cars);
            if (collision != null) {
                double translatedAngle = carAngle + Math.PI;
                double[] dirVector = {Math.cos(translatedAngle),Math.sin(translatedAngle)};
                return dirVector;
            }
        }
        return new double[]{0,0};
    }

    public void update(Car subject, Game game, double delta_t, double[] controlVariables) {
        controlVariables[VARIABLE_STEERING] = 0;
        controlVariables[VARIABLE_THROTTLE] = 0;
        controlVariables[VARIABLE_BRAKE] = 0;

        // seek vector
        double[] seekVector = SteeringHelper.getSeekDirectionVector(subject,target);
        double wallAvoidanceMultiplier;
        if (lengthOf2DVector(new double[]{target.getX() - subject.getX(), target.getY() - subject.getY()}) < 40)
            wallAvoidanceMultiplier = 0;
        else
            wallAvoidanceMultiplier = 4;

        double[] wallAvoidanceVector = getWallAvoidanceVector(subject,game);

        double[] totalVector = {seekVector[0]+(wallAvoidanceMultiplier*wallAvoidanceVector[0]),seekVector[1]+(wallAvoidanceMultiplier*wallAvoidanceVector[1])};

        double totalDir = Math.atan2(totalVector[1],totalVector[0]);
        double carAngle = subject.getAngle();


        double diffBetweenAngles = SteeringHelper.getDiffBetweenAngles(totalDir,carAngle);
        double absOfDiffOfAngles = Math.abs(diffBetweenAngles);
        if (absOfDiffOfAngles > (Math.PI/2))
            controlVariables[VARIABLE_BRAKE] = 1;
        else
            controlVariables[VARIABLE_THROTTLE] = 1;
        if (absOfDiffOfAngles > 1)
            diffBetweenAngles /= absOfDiffOfAngles;
        if (absOfDiffOfAngles < 0.05)
            diffBetweenAngles = 0;
        controlVariables[VARIABLE_STEERING] = diffBetweenAngles;
    }
    
}
