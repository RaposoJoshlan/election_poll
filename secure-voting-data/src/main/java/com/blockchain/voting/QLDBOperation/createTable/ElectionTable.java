package com.blockchain.voting.QLDBOperation.createTable;

import com.blockchain.voting.QLDBOperation.CreateIndex;
import com.blockchain.voting.QLDBOperation.CreateTable;
import com.blockchain.voting.config.QLDBConfig;
import com.blockchain.voting.model.consts.AWSQldb;
import com.blockchain.voting.model.consts.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

/**
 * Created on 10/08/2022
 */

public class ElectionTable implements CreateTable, CreateIndex {
    public static AWSQldb awsQldb = new AWSQldb();
    public final Logger log = LoggerFactory.getLogger(this.getClass());

//    @Override
//    public int createTableSetup(TransactionExecutor txn, String tableName) {
//        log.info("Creating the '{}' table...", tableName);
//        final String createTable = String.format("CREATE TABLE %s", tableName);
//        final Result result = txn.execute(createTable);
//        log.info("{} table created successfully.", tableName);
//        return SampleData.toIonValues(result).size();
//    }

    @Override
    public void createTableSetup(TransactionExecutor txn, String tableName) {
        try {
            log.info("Creating the '{}' table...", tableName);
            final String createTable = String.format("CREATE TABLE %s", tableName);
            final Result result = txn.execute(createTable);
            log.info("{} table created successfully.", tableName);
//        return SampleData.toIonValues(result).size();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    @Override
    public void createTable() {
        QLDBConfig.getDriver().execute(txn -> {
            createTableSetup(txn, Constants.ELECTION_TABLE_NAME);
        });
    }

//    @Override
//    public int createIndexSetup(TransactionExecutor txn, String tableName, String indexAttribute) {
//        log.info("Creating an index on {}...", indexAttribute);
//        final String createIndex = String.format("CREATE INDEX ON %s (%s)", tableName, indexAttribute);
//        final Result r = txn.execute(createIndex);
//        return SampleData.toIonValues(r).size();
//    }

    @Override
    public void createIndexSetup(TransactionExecutor txn, String tableName, String indexAttribute) {
        try {
            log.info("Creating an index on {}...", indexAttribute);
            final String createIndex = String.format("CREATE INDEX ON %s (%s)", tableName, indexAttribute);
            final Result r = txn.execute(createIndex);
//        return SampleData.toIonValues(r).size();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    @Override
    public void createIndex() {
        QLDBConfig.getDriver().execute(txn -> {
            createIndexSetup(txn, Constants.ELECTION_TABLE_NAME, Constants.ELECTION_TYPE_INDEX);
            createIndexSetup(txn, Constants.ELECTION_TABLE_NAME, Constants.ELECTION_ID_INDEX);
        });
        log.info("Indexes created successfully!");
    }
}
