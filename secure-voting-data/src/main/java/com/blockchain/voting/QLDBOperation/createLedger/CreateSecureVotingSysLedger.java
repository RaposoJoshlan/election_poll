package com.blockchain.voting.QLDBOperation.createLedger;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.qldb.AmazonQLDB;
import com.amazonaws.services.qldb.AmazonQLDBClientBuilder;
import com.amazonaws.services.qldb.model.*;
import com.blockchain.voting.QLDBOperation.CreateLedger;
import com.blockchain.voting.model.consts.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 10/08/2022
 */

public class CreateSecureVotingSysLedger implements CreateLedger {

    public final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final Long LEDGER_CREATION_POLL_PERIOD_MS = 10_000L;
    public static String endpoint = null;
    public static String region = null;

    public static AmazonQLDB getClient() {
        AmazonQLDBClientBuilder builder = AmazonQLDBClientBuilder.standard();
        if (null != endpoint && null != region) {
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region));
        }
        return builder.build();
    }

    @Override
    public CreateLedgerResult createLedger(String ledgerName) {
        log.info("Creating ledger: {}...", ledgerName);
        CreateLedgerRequest request = new CreateLedgerRequest()
                .withName(ledgerName)
                .withPermissionsMode(PermissionsMode.ALLOW_ALL);
        CreateLedgerResult result = getClient().createLedger(request);
        log.info("Success. Ledger state: {}.", result.getState());
        return result;
    }

    @Override
    public DescribeLedgerResult waitForActive(String ledgerName) {
        log.info("Checking ledger status...");
        while (true) {
            try {
                DescribeLedgerResult result = describe(ledgerName);
                if (result.getState().equals(LedgerState.ACTIVE.name())) {
                    log.info("Ledger status is active and ready for use.");
                    log.info("Ledger description: {}", result);
                    return result;
                }
                log.info("The ledger is still creating. Please wait...");
                Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            } catch (Exception e) {
                log.error("ledger status unknown!", e);
            }
        }
    }

    @Override
    public DescribeLedgerResult describe(String name) {
        DescribeLedgerRequest request = new DescribeLedgerRequest().withName(name);
        return getClient().describeLedger(request);
    }

    public void create() {
        try {
            getClient();
            createLedger(Constants.LEDGER_NAME);
            waitForActive(Constants.LEDGER_NAME);
        } catch (ResourceAlreadyExistsException r) {
            log.info(r.getMessage());
            waitForActive(Constants.LEDGER_NAME);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
