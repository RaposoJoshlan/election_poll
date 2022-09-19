package com.blockchain.voting.QLDBOperation;

import software.amazon.qldb.TransactionExecutor;

/**
 * Created on 10/08/2022
 */

public interface CreateVoteIndex {
    int createIndexSetup(final TransactionExecutor txn, final String tableName, final String indexAttribute,
                         String indexAttribute2, String indexAttribute3);

    void createIndex();
}
