package net.environmentz;

import net.fabricmc.api.ModInitializer;
import net.environmentz.init.*;

public class EnvironmentzMain implements ModInitializer {

  @Override
  public void onInitialize() {
    ConfigInit.init();
    EffectInit.init();
    ItemInit.init();
    LootInit.init();
    TagInit.init();
  }
}

// You are LOVED!!!
// Jesus loves you unconditional!