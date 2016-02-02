package com.suse.salt.netapi.results;

import java.util.Iterator;
import java.util.List;

/**
 * Holds a result set of a running job. Normally, only one result will be
 * available.
 */
public class ResultInfoSet implements Iterable<ResultInfo> {
    private List<ResultInfo> info;

    /**
     * Returns an iterator to the ResultInfo collection.
     */
    @Override
    public Iterator<ResultInfo> iterator() {
        return info.iterator();
    }

    /**
     * Returns {@link ResultInfo} associated with a given index. Most jobs
     * should only have 1 result structure.
     *
     * @param index represents index of the result.
     * @return ResultInfo associated with the index
     */
    public ResultInfo get(int index) {
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
     * @return list of {@link ResultInfo}
     */
    public List<ResultInfo> getInfoList() {
        return info;
    }
}
