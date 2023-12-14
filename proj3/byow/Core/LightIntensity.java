package byow.Core;

public class LightIntensity {
    public double intensity;
    LightSource l;
    public LightIntensity(LightSource l, double distance) {
        this.l = l;
        // inverse square law to calculate distance
        this.intensity = (l.getPower()) / (Math.PI * Math.pow(distance, 2));
    }
}
