package byow.Core;

public class LightIntensity {
    private double intensity;
    private LightSource l;
    public LightIntensity(LightSource l, double distance) {
        this.l = l;
        // inverse square law to calculate distance
        this.intensity = (l.getPower()) / (Math.PI * Math.pow(distance, 2));
    }
    public LightSource source() {
        return l;
    }
    public double intensity() {
        return intensity;
    }
    @Override
    public int hashCode() {
        return l.hashCode();
    }
    @Override
    public boolean equals(Object c) {
        if (c == null) {
            return false;
        } else if (c.getClass() != this.getClass()) {
            return false;
        } else {
            LightIntensity comp = (LightIntensity) c;
            return comp.l == this.l;
        }
    }
}
