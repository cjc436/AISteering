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
 * @author santi
 */
public class WallAvoidanceSeekController extends Controller {

    private GameObject target;

    public WallAvoidanceSeekController(GameObject target) {
        this.target = target;
    }

    private double[] getWallAvoidanceVector(Car car, Game game) {
        // wall avoidance (flee vector if exists)
        // avoid straight ahead
        RotatedRectangle carRect = car.getCollisionBox();
//        RotatedRectangle collisionRect = new RotatedRectangle(carRect.C.getX(),carRect.C.getY(),carRect.S.getX(),carRect.S.getY(),carRect.ang);
        double[] carDirVector = car.getDirectionVector();
        double carDirMag = lengthOf2DVector(carDirVector);
        carDirVector = new double[]{(10 * carDirVector[0])/carDirMag, (10 * carDirVector[1])/carDirMag};

        HashSet<GameObject> cars = new HashSet<>();
        cars.add(car);
        cars.add(target);

        double carAngle = car.getAngle();
        if (car.getSpeed() < 0) {
            carAngle += Math.PI;
            carDirVector = new double[]{-carDirVector[0],-carDirVector[1]};
        }

        double angleOfProjection = carAngle + (Math.PI/4);
        for (int i = 1; i < 5; i++) {
            RotatedRectangle collisionRect = new RotatedRectangle(carRect.C.getX()+(i*carDirVector[0]),carRect.C.getY()+(i*carDirVector[1]),carRect.S.getX(),carRect.S.getY(),angleOfProjection);

            GameObject collision = game.collision(collisionRect,cars);
//            System.out.println(collisionRect.C.getX());
            if (collision != null) {
//                System.out.println(collision);
                double[] dirVector = SteeringHelper.getFleeDirectionVector(car,collisionRect.C.getX(),collisionRect.C.getY());
//                System.out.println(dirVector[0]);
                return dirVector;
            }
        }
        angleOfProjection = car.getAngle() - (Math.PI/4);
        for (int i = 1; i < 5; i++) {
            RotatedRectangle collisionRect = new RotatedRectangle(carRect.C.getX()+(i*carDirVector[0]),carRect.C.getY()+(i*carDirVector[1]),carRect.S.getX(),carRect.S.getY(),angleOfProjection);

            GameObject collision = game.collision(collisionRect,cars);
//            System.out.println(collisionRect.C.getX());
            if (collision != null) {
//                System.out.println(collision);
                double[] dirVector = SteeringHelper.getFleeDirectionVector(car,collisionRect.C.getX(),collisionRect.C.getY());
//                System.out.println(dirVector[0]);
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
//        double[] totalVector = SteeringHelper.getSeekDirectionVector(subject,target);


        double[] wallAvoidanceVector = getWallAvoidanceVector(subject,game);
        System.out.println("0: "+wallAvoidanceVector[0]+", 1: "+wallAvoidanceVector[1]);

        double[] totalVector = {seekVector[0]+(2*wallAvoidanceVector[0]),seekVector[1]+(2*wallAvoidanceVector[1])};

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
