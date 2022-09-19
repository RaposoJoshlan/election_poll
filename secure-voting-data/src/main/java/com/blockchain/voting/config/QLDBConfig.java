package com.blockchain.voting.config;

import com.blockchain.voting.model.consts.AWSQldb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.qldbsession.QldbSessionClient;
import software.amazon.awssdk.services.qldbsession.QldbSessionClientBuilder;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.RetryPolicy;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created on 10/08/2022
 */
@Configuration
public class QLDBConfig {

    public static final Logger log = LoggerFactory.getLogger(QLDBConfig.class);
    public static AWSQldb awsQldb = new AWSQldb();

    @Bean
    public static QldbDriver createQldbDriver() {
        log.info("Initialing the QLDB Driver");
        QldbSessionClientBuilder builder = getAmazonQldbSessionClientBuilder();
        return QldbDriver.builder()
                .ledger(awsQldb.getLedgerName())
                .transactionRetryPolicy(RetryPolicy.builder()
                        .maxRetries(awsQldb.getRETRY_LIMIT()).build())
                .sessionClientBuilder(builder)
                .build();
    }

    /**
     * Creates a QldbSession builder that is passed to the QldbDriver to connect to the Ledger.
     *
     * @return An instance of the AmazonQLDBSessionClientBuilder
     */


    public static QldbSessionClientBuilder getAmazonQldbSessionClientBuilder() {
        QldbSessionClientBuilder builder = QldbSessionClient.builder();
        if (null != awsQldb.getEndpoint() && null != awsQldb.getRegion()) {
            try {
                builder.endpointOverride(new URI(awsQldb.getRegion()));
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (null != awsQldb.credentialsProvider) {
            builder.credentialsProvider(awsQldb.getCredentialsProvider());
        }
        return builder;
    }

    /**
     * Create a pooled driver for creating sessions.
     *
     * @return The pooled driver for creating sessions.
     */
    public static QldbDriver getDriver() {
        if (awsQldb.getDriver() == null) {
            awsQldb.setDriver(createQldbDriver());
            log.info("QLDB Driver Initialised");
        }
        return awsQldb.getDriver();
    }
}
