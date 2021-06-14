package net.environmentz;

import net.environmentz.init.RenderInit;
import net.fabricmc.api.ClientModInitializer;

public class EnvironmentzClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    RenderInit.init();
  }

}
