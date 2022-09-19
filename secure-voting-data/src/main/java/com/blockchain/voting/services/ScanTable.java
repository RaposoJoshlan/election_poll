package com.blockchain.voting.services;

import com.amazon.ion.IonStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

import java.util.ArrayList;
import java.util.List;

public final class ScanTable {
    private static final Logger log = LoggerFactory.getLogger(ScanTable.class);

    private ScanTable() { }

    /**
     * Scan the table with the given {@code tableName} for all documents.
     *
     * @param txn
     *              The {@link TransactionExecutor} for lambda execute.
     * @param tableName
     *              Name of the table to scan.
     * @return a list of documents in {@link IonStruct} .
     */
    public static List<IonStruct> scanTableForDocuments(final TransactionExecutor txn, final String tableName) {
        log.info("Scanning '{}'...", tableName);
        final String scanTable = String.format("SELECT * FROM %s", tableName);
        List<IonStruct> documents = toIonStructs(txn.execute(scanTable));
        log.info("Scan successful!");
        printDocuments(documents);
        return documents;
    }

    /**
     * Pretty print all elements in the provided {@link Result}.
     *
     * @param result
     *              {@link Result} from executing a query.
     */
    public static void printDocuments(final Result result) {
        result.iterator().forEachRemaining(row -> log.info(row.toPrettyString()));
    }

    /**
     * Pretty print all elements in the provided list of {@link IonStruct}.
     *
     * @param documents
     *              List of documents to print.
     */
    public static void printDocuments(final List<IonStruct> documents) {
        documents.forEach(row -> log.info(row.toPrettyString()));
    }

    /**
     * Convert the result set into a list of {@link IonStruct}.
     *
     * @param result
     *              {@link Result} from executing a query.
     * @return a list of documents in IonStruct.
     */
    public static List<IonStruct> toIonStructs(final Result result) {
        final List<IonStruct> documentList = new ArrayList<>();

        if (result.isEmpty()) {
            return documentList;
        }

        result.iterator().forEachRemaining(row -> documentList.add((IonStruct) row));
        return documentList;
    }

//    public static void main(final String... args) {
//        QLDBConfig.getDriver().execute(txn -> {
//            List<String> tableNames = scanTableForDocuments(txn, Constants.USER_TABLES)
//                .stream()
//                .map((s) -> s.get("name").toString())
//                .collect(Collectors.toList());
//            for (String tableName : tableNames) {
//                scanTableForDocuments(txn, tableName);
//            }
//        });
//    }
}
