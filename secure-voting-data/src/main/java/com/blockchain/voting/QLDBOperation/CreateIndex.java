package com.blockchain.voting.QLDBOperation;

import software.amazon.qldb.TransactionExecutor;

/**
 * Created on 10/08/2022
 */

public interface CreateIndex {
    void createIndexSetup(final TransactionExecutor txn, final String tableName, final String indexAttribute);
    void createIndex();
}
