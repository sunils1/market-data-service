package com.carta.marketdata.repository;

import java.util.List;

public interface Repository<R> {
    /**
     * Get the latest for the symbol
     * @param key   key to search on
     * @return      data returned
     */
    R get(String key);

    /**
     * get multiple data sets, based on the row count
     *
     * @param key       key to search on
     * @param rowCount  row count to return
     * @return          list of data returned
     */
    List<R> get(String key, int rowCount);

    void add(R data);
}
