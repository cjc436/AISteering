package controllers;

import engine.Car;
import engine.GameObject;

public class SteeringHelper {

    /**
     * If positive then angle1 is to the right of angle2, and if negative then vice-versa
     * @param angle1
     * @param angle2
     * @return
     */
    public static double getDiffBetweenAngles(double angle1, double angle2) {
        double twoPi = (2 * Math.PI);
        double rAngle1 = angle1 % twoPi;
        if (rAngle1 < 0)
            rAngle1 = rAngle1 + twoPi;
        double rAngle2 = angle2 % twoPi;
        if (rAngle2 < 0)
            rAngle2 = rAngle2 + twoPi;
        double diff = rAngle1 - rAngle2;
        if (Math.abs(diff) > Math.PI) {
            if (diff < 0)
                diff = diff + twoPi;
            else
                diff = diff - twoPi;
        }
        return diff;
    }

    public static double[] getDirVector(double x1, double y1, double x2, double y2) {
        double[] dirVector = {x2 - x1, y2 - y1};
        double dirMag = Math.sqrt((dirVector[0] * dirVector[0]) + (dirVector[1] * dirVector[1]));
        return new double[]{dirVector[0]/dirMag,dirVector[1]/dirMag};
    }
    public static double[] getSeekDirectionVector(Car car, GameObject target) {
        double carPosX = car.getX();
        double carPosY = car.getY();
        return getDirVector(carPosX,carPosY,target.getX(),target.getY());
    }
    public static double[] getFleeDirectionVector(Car car, double x2, double y2) {
        double carPosX = car.getX();
        double carPosY = car.getY();
        double[] dirVector = getDirVector(carPosX,carPosY,x2,y2);
        return new double[]{-dirVector[0],-dirVector[1]};
    }
}
