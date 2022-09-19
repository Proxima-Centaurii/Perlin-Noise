package app.utility;

public class MathUtil {

    public static float interpolate(float x, float xa, float xb, float ya, float yb){
        return ya+(yb - ya)*((x-xa)/(xb-xa));
    }

    public static float interpolate(float ya, float yb, float x){
        return ya + (yb - ya) * x;
    }

}//end of class
