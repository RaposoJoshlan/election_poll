package com.blockchain.voting.model.consts;

import com.amazon.ion.IonReader;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonValue;
import com.amazon.ion.IonWriter;
import com.amazon.ion.system.IonReaderBuilder;
import com.amazon.ion.system.IonTextWriterBuilder;
import com.blockchain.voting.model.streams.RevisionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.qldb.QldbDriver;
import software.amazon.qldb.Result;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/08/2022
 */

public class AWSQldb implements RevisionData {
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    public AwsCredentialsProvider credentialsProvider;
    public String endpoint = null;
    public final String ledgerName = Constants.LEDGER_NAME;
    public String region = null;
    public QldbDriver driver;
    public final int RETRY_LIMIT = 4;

    public AwsCredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public void setCredentialsProvider(AwsCredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getLedgerName() {
        return ledgerName;
    }

    public String getRegion() {
        return region;
    }

    public QldbDriver getDriver() {
        return driver;
    }

    public QldbDriver setDriver(QldbDriver driver) {
        this.driver = driver;
        return driver;
    }

    public int getRETRY_LIMIT() {
        return RETRY_LIMIT;
    }

    public static void rewrite(String textIon, IonWriter writer) throws IOException {
        try (IonReader reader = IonReaderBuilder.standard().build(textIon)) {
            writer.writeValues(reader);
        }
    }

    public static String intoJson(List<IonStruct> ionStructs) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (IonWriter jsonWriter = IonTextWriterBuilder.json().withPrettyPrinting().build(stringBuilder)) {
            rewrite(ionStructs.toString(), jsonWriter);
        }

        return stringBuilder.toString();
    }

    public static List<String> getDocumentIdsFromDmlResult(final Result result) {
        final List<String> strings = new ArrayList<>();
        result.iterator().forEachRemaining(row -> strings.add(getDocumentIdFromDmlResultDocument(row)));
        return strings;
    }

    public static String getDocumentIdFromDmlResultDocument(final IonValue dmlResultDocument) {
        try {
            DmlResultDocument result = Constants.MAPPER.readValue(dmlResultDocument, DmlResultDocument.class);
            return result.getDocumentId();
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public static String generateUniqueStringForGivenCandidateAsID(String a, String b, String c, String d) {
        String uniqueId = a + b + c + d;
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < uniqueId.length(); i++) {
            stringBuilder.append(uniqueId.charAt(secureRandom.nextInt(uniqueId.length())));
        }

        return stringBuilder.toString();
    }

}
