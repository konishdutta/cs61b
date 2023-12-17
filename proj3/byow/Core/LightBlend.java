package byow.Core;
import java.awt.Color;

public class LightBlend {
    public double intensity;
    public Color color;
    public LightBlend(double intensity, Color color) {
        this.intensity = intensity;
        this.color = color;
    }

    public double intensity() {
        return intensity;
    }
    public Color color() {
        return color;
    }
    @Override
    public String toString() {
        return "Intensity: " + intensity + ", Color: " + color.toString();
    }

}
