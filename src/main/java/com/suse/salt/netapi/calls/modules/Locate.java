package com.suse.salt.netapi.calls.modules;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.LocalCall;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.locate
 * Module for using the mlocate utilities.
 */
public class Locate {

    private static final LocalCall<List<String>> VERSION = new LocalCall<>(
            "locate.version",
            Optional.empty(), Optional.empty(),
            new TypeToken<List<String>>() {
            });

    private static final LocalCall<List<String>> UPDATEDB = new LocalCall<>(
            "locate.updatedb",
            Optional.empty(), Optional.empty(),
            new TypeToken<List<String>>() {
            });

    private static final LocalCall<Stats> STATS = new LocalCall<>("locate.stats",
            Optional.empty(), Optional.empty(),
            new TypeToken<Stats>() {
            });

    /**
     * Locate module result object
     */
    public static class Stats {
        private long files;
        private long directories;
        @SerializedName("bytes in file names")
        private long fileNamesBytes;
        @SerializedName("bytes used to store database")
        private long databaseBytes;
        @SerializedName("database")
        private String databaseLocation;

        public long getFiles() {
            return files;
        }

        public long getDirectories() {
            return directories;
        }

        public long getFileNamesBytes() {
            return fileNamesBytes;
        }

        public long getDatabaseBytes() {
            return databaseBytes;
        }

        public String getDatabaseLocation() {
            return databaseLocation;
        }
    }

    /**
     * All possible options for "locate" method.
     */
    public static class LocateOpts {
        private Map<String, Boolean> opts = new HashMap<>();

        public void setBasename(boolean basename) {
            opts.put("basename", basename);
        }

        public void setCount(boolean count) {
            opts.put("count", count);
        }

        public void setExisting(boolean existing) {
            opts.put("existing", existing);
        }

        public void setFollow(boolean follow) {
            opts.put("follow", follow);
        }

        public void setIgnore(boolean ignore) {
            opts.put("ignore", ignore);
        }

        public void setNofollow(boolean nofollow) {
            opts.put("nofollow", nofollow);
        }

        public void setWholename(boolean wholename) {
            opts.put("wholename", wholename);
        }

        public void setRegex(boolean regex) {
            opts.put("regex", regex);
        }

        Map<String, Boolean> getOpts() {
            return opts;
        }
    }

    /**
     * Returns the version of locate.
     * <br>
     * The result is a list of Strings representing each a line of the output
     * of the 'locate --v' execution.
     *
     * @return The {@link LocalCall} object to make the call
     */
    public static LocalCall<List<String>> version() {
        return VERSION;
    }

    /**
     * Updates the locate database.
     * <br>
     * A successful result will return an empty list of Strings.
     * <br>If any error is returned, then a list of Strings representing each a line of
     * the error output is returned.
     *
     * @return The {@link LocalCall} object to make the call
     */
    public static LocalCall<List<String>> updatedb() {
        return UPDATEDB;
    }

    /**
     * Returns statistics about the locate database.
     *
     * @return The {@link LocalCall} object to make the call
     */
    public static LocalCall<Stats> stats() {
        return STATS;
    }

    /**
     * Performs a file lookup. Valid options (and their defaults) are:
     * <ul>
     * <li>basename=False</li>
     * <li>count=False</li>
     * <li>existing=False</li>
     * <li>follow=True</li>
     * <li>ignore=False</li>
     * <li>nofollow=False</li>
     * <li>wholename=True</li>
     * <li>regex=False</li>
     * </ul>
     *
     * @param pattern  The pattern to search
     * @param database ﻿Replace the default database.
     * @param limit    Set the maximum number of results.
     * @param options  More options to mofify the search.
     * @return The {@link LocalCall} object to make the call
     */
    public static LocalCall<List<String>> locate(String pattern, Optional<String> database,
            Optional<Integer> limit,
            Optional<LocateOpts> options) {
        List<Object> args = new LinkedList<>();
        Map<String, Boolean> kwargs = new LinkedHashMap<>();

        args.add(pattern);
        args.add(database.orElse(""));
        args.add(limit.orElse(0));

        options.ifPresent(opts -> kwargs.putAll(opts.getOpts()));

        return new LocalCall<>("locate.locate", Optional.of(args), Optional.of(kwargs),
                new TypeToken<List<String>>() {});
    }
}
