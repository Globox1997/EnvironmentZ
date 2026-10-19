package net.environmentz.temperature.data;

import net.minecraft.state.property.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public record BlockTemperature(int maxCount, int[] temperatures, @Nullable BooleanProperty property) {

    public int getTemperature(int distance) {
        return distance >= 0 && distance < this.temperatures.length ? this.temperatures[distance] : 0;
    }
}
