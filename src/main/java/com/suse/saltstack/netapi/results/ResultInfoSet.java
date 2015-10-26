package com.suse.saltstack.netapi.results;

import java.util.Iterator;
import java.util.List;

import com.suse.saltstack.netapi.calls.runner.Jobs;

/**
 * Holds a result set of a running job. Normally, only one result will be
 * available.
 *
 * @param <R> - result type
 */
public class ResultInfoSet<R> implements Iterable<Jobs.Info<R>> {
    private List<Jobs.Info<R>> info;

    /**
     * Returns an iterator to the
     * {@link com.suse.saltstack.netapi.calls.runner.Jobs.Info Jobs.Info} collection.
     */
    @Override
    public Iterator<Jobs.Info<R>> iterator() {
        return info.iterator();
    }

    /**
     * Returns {@link com.suse.saltstack.netapi.calls.runner.Jobs.Info Jobs.Info}
     * associated with a given index. Most jobs should only have 1 result structure.
     *
     * @param index represents index of the result.
     * @return Jobs.Info associated with the index
     */
    public Jobs.Info<R> get(int index) {
        return info.get(index);
    }

    /**
     * Returns result set size.
     *
     * @return result set size
     */
    public int size() {
        return info.size();
    }

    /**
     * Returns a list of all results.
     *
     * @return list of {@link com.suse.saltstack.netapi.calls.runner.Jobs.Info Jobs.Info}
     */
    public List<Jobs.Info<R>> getInfoList() {
        return info;
    }
}
