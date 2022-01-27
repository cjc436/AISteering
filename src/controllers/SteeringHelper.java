package controllers;

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
}
