package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.test
 */
public class Test {

    private static final LocalCall<Boolean> PING =
            new LocalCall<>("test.ping", Optional.empty(), Optional.empty(),
            new TypeToken<Boolean>(){});

    private static final LocalCall<VersionInformation> VERSIONS_INFORMATION =
            new LocalCall<>("test.versions_information", Optional.empty(), Optional.empty(),
            new TypeToken<VersionInformation>(){});

    private static final LocalCall<ModuleReport> MODULE_REPORT =
            new LocalCall<>("test.module_report", Optional.empty(), Optional.empty(),
            new TypeToken<ModuleReport>(){});

    private static final LocalCall<Map<String, String>> PROVIDERS =
            new LocalCall<>("test.providers", Optional.empty(), Optional.empty(),
            new TypeToken<Map<String, String>>(){});

    /**
     * Availability report of all execution modules
     */
    public static class ModuleReport {
        private final List<String> functions;
        private final List<String> modules;

        @SerializedName("missing_subs")
        private final List<String> missingSubs;

        @SerializedName("missing_attrs")
        private final List<String> missingAttrs;

        @SerializedName("function_subs")
        private final List<String> functionSubs;

        @SerializedName("module_attrs")
        private final List<String> moduleAttrs;

        @SerializedName("function_attrs")
        private final List<String> functionAttrs;

        public ModuleReport(List<String> functions, List<String> modules,
                List<String> missingSubs, List<String> missingAttrs,
                List<String> functionSubs, List<String> moduleAttrs,
                List<String> functionAttrs) {
            this.functions = functions;
            this.modules = modules;
            this.missingSubs = missingSubs;
            this.missingAttrs = missingAttrs;
            this.functionSubs = functionSubs;
            this.moduleAttrs = moduleAttrs;
            this.functionAttrs = functionAttrs;
        }

        public List<String> getFunctions() {
            return functions;
        }

        public List<String> getModules() {
            return modules;
        }

        public List<String> getMissingSubs() {
            return missingSubs;
        }

        public List<String> getMissingAttrs() {
            return missingAttrs;
        }

        public List<String> getFunctionSubs() {
            return functionSubs;
        }

        public List<String> getModuleAttrs() {
            return moduleAttrs;
        }

        public List<String> getFunctionAttrs() {
            return functionAttrs;
        }
    }

    /**
     * Version report of dependent and system software
     */
    public static class VersionInformation {
        @SerializedName("Salt Version")
        private final Map<String, String> salt;

        @SerializedName("System Versions")
        private final Map<String, String> system;

        @SerializedName("Dependency Versions")
        private final Map<String, String> dependencies;

        public VersionInformation(Map<String, String> salt,
                Map<String, String> system,
                Map<String, String> dependencies) {
            this.salt = salt;
            this.system = system;
            this.dependencies = dependencies;
        }

        public Map<String, String> getDependencies() {
            return dependencies;
        }

        public Map<String, String> getSalt() {
            return salt;
        }

        public Map<String, String> getSystem() {
            return system;
        }
    }

    public static LocalCall<Boolean> ping() {
        return PING;
    }

    public static LocalCall<VersionInformation> versionsInformation() {
        return VERSIONS_INFORMATION;
    }

    public static LocalCall<ModuleReport> moduleReport() {
        return MODULE_REPORT;
    }

    public static LocalCall<Map<String, String>> providers() {
        return PROVIDERS;
    }

    public static LocalCall<String> provider(String module) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("module", module);
        return new LocalCall<>("test.provider", Optional.empty(), Optional.of(args),
                new TypeToken<String>() {});
    }
}
