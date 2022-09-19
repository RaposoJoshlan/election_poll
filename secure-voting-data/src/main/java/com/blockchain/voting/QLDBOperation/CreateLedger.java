package com.blockchain.voting.QLDBOperation;

import com.amazonaws.services.qldb.AmazonQLDB;
import com.amazonaws.services.qldb.model.CreateLedgerResult;
import com.amazonaws.services.qldb.model.DescribeLedgerResult;

/**
 * Created on 10/08/2022
 */

public interface CreateLedger {
    CreateLedgerResult createLedger(final String ledgerName);

    DescribeLedgerResult waitForActive(final String ledgerName) throws InterruptedException;

    DescribeLedgerResult describe(final String name);
}
