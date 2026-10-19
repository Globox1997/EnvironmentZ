package net.environmentz.temperature.data;

public enum EnvironmentCode {
    VERY_COLD,
    COLD,
    NORMAL,
    HOT,
    VERY_HOT;

    public boolean isCold() {
        return this == VERY_COLD || this == COLD;
    }

    public boolean isHot() {
        return this == HOT || this == VERY_HOT;
    }

    public static EnvironmentCode fromBiomeTemperature(float biomeTemperature, BiomeTemperature thresholds) {
        if (biomeTemperature < thresholds.cold()) {
            return biomeTemperature < thresholds.veryCold() ? VERY_COLD : COLD;
        }
        if (biomeTemperature > thresholds.hot()) {
            return biomeTemperature > thresholds.veryHot() ? VERY_HOT : HOT;
        }
        return NORMAL;
    }
}
