package com.blockchain.voting.QLDBOperation.insertDocument;

import com.amazon.ion.IonValue;
import com.blockchain.voting.model.consts.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.qldb.TransactionExecutor;

import java.io.IOException;
import java.util.List;

import static com.blockchain.voting.model.consts.AWSQldb.getDocumentIdsFromDmlResult;

/**
 * Created on 12 Sep 2022
 */
public class InsertDocument {
    public static final Logger log = LoggerFactory.getLogger(InsertDocument.class);

    public static List<String> insertDocument(TransactionExecutor txn, String tableName, List documents) {
        log.info("Inserting some documents in the {} table...", tableName);
        try {
            final String query = String.format("INSERT INTO %s ? ", tableName);
            final IonValue ionDocuments = Constants.MAPPER.writeValueAsIonValue(documents);
            return getDocumentIdsFromDmlResult(txn.execute(query, ionDocuments));
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
}
