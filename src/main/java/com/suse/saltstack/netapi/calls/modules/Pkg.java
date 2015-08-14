package com.suse.saltstack.netapi.calls.modules;

import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.LocalCall;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.pkg
 */
public class Pkg {

    /**
     *
     */
    public class PackageInfo {

        private String summary;

        public String getSummary() {
            return summary;
        }
    }

    /**
     *
     */
    public class PackageDict {

        private List<Object> errors;
        private Map<String, List<String>> packages;

        public List<Object> getErrors() {
            return errors;
        }

        public Map<String, List<String>> getPackages() {
            return packages;
        }
    }


    private Pkg() { }

    public static LocalCall<Map<String, Map<String, PackageInfo>>> search(String criteria) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("criteria", criteria);
        return new LocalCall<>("pkg.search", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Map<String, PackageInfo>>>(){});
    }

    public static LocalCall<Map<String, PackageDict>> fileDict(String... packages) {
        return new LocalCall<>("pkg.file_dict", Optional.of(Arrays.asList(packages)),
                Optional.empty(), new TypeToken<Map<String, PackageDict>>(){});
    }

    public static LocalCall<Map<String, Map<String, List<String>>>> listPkgs() {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("versions_as_list", true);
        return new LocalCall<>("pkg.list_pkgs", Optional.empty(), Optional.of(args),
                new TypeToken<Map<String, Map<String, List<String>>>>(){});
    }

}
