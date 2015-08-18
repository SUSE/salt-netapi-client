package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.network
 */
public class Network {

    private Network() { }

    /**
     *
     */
    public static class Interface {
        private String hwaddr;
        private boolean up;
        private List<INet> inet;
        private List<INet6> inet6;
    }

    /**
     *
     */
    public static class INet {
        private String broadcast;
        private String netmask;
        private String label;
        private String address;

    }

    /**
     *
     */
    public static class INet6 {
        private String prefixlen;
        private String address;
    }

    public static LocalCall<Map<String, Map<String, Interface>>> interfaces() {
        return new LocalCall<>("network.interfaces", Optional.empty(), Optional.empty(),
                new TypeToken<Map<String, Map<String, Interface>>>(){});
    }
}
