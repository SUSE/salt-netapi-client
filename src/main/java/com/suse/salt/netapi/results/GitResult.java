package com.suse.salt.netapi.results;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Result structure as returned by git status.
 */
public class GitResult {

    private List<String> modified;
    private List<String> untracked;

    public List<String> getModified() {
        return modified;
    }

    public List<String> getUntracked() {
        return untracked;
    }

    @Override
    public String toString() {        
        List<List<String>> allFiles = Arrays.asList(modified, untracked);
        return allFiles.stream()
                .filter(list -> list != null)
                .flatMap(list -> list.stream())
                .collect(Collectors.joining(","));
    }

}
