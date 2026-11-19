package net.environmentz.platform;

import java.util.ServiceLoader;

import net.environmentz.platform.services.INetworkHelper;
import net.environmentz.platform.services.IPlatformHelper;
import net.environmentz.platform.services.IRegistryHelper;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static INetworkHelper network() {
        return NetworkHolder.NETWORK;
    }

    public static IRegistryHelper registry() {
        return RegistryHolder.REGISTRY;
    }

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz, Services.class.getClassLoader()).findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }

    private static final class RegistryHolder {
        private static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);
    }

    private static final class NetworkHolder {
        private static final INetworkHelper NETWORK = load(INetworkHelper.class);
    }
}
