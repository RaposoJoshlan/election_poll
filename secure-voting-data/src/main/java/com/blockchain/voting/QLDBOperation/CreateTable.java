package com.blockchain.voting.QLDBOperation;

import software.amazon.qldb.TransactionExecutor;

/**
 * Created on 10/08/2022
 */

public interface CreateTable {
    void createTableSetup(final TransactionExecutor txn, final String tableName);

    void createTable();
}
