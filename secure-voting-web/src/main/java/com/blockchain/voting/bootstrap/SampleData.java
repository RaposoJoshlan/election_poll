package com.blockchain.voting.bootstrap;

import com.amazon.ion.IonString;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonValue;
import com.blockchain.voting.QLDBOperation.createLedger.CreateSecureVotingSysLedger;
import com.blockchain.voting.QLDBOperation.createTable.CandidateTable;
import com.blockchain.voting.QLDBOperation.createTable.ElectionTable;
import com.blockchain.voting.QLDBOperation.createTable.VoteTable;
import com.blockchain.voting.QLDBOperation.createTable.VoterTable;
import com.blockchain.voting.insertDocument.CandidateInsertDocument;
import com.blockchain.voting.model.Candidate;
import com.blockchain.voting.model.Election;
import com.blockchain.voting.model.Gender;
import com.blockchain.voting.model.consts.AWSQldb;
import com.blockchain.voting.model.consts.Constants;
import com.blockchain.voting.model.consts.DmlResultDocument;
import com.blockchain.voting.services.CandidateServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static com.blockchain.voting.model.consts.AWSQldb.generateUniqueStringForGivenCandidateAsID;

/**
 * Created on 10/08/2022
 */

@Component
public class SampleData implements CommandLineRunner {
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final Long LEDGER_CREATION_POLL_PERIOD_MS = 10_000L;
    public static AWSQldb awsQldb = new AWSQldb();

    private final CandidateServiceImpl candidateService;

    public SampleData(CandidateServiceImpl candidateService) {
        this.candidateService = candidateService;
    }

    public static List<String> constituencies() throws FileNotFoundException {
        Scanner s = new Scanner(new File("constituenciesUK.txt"));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext()) {
            list.add(s.next());
        }
        s.close();

        return list;
    }

    public static List<Election> electionParties() throws FileNotFoundException {
        Scanner s = new Scanner(new File("electionPartyListUK.txt"));
        ArrayList<String> list = new ArrayList<>();
        while (s.hasNext()) {
            list.add(s.next());
        }
        s.close();

        List<Election> elections = new ArrayList<>();


        for (String value : list) {
            elections.add(new Election(value));
        }

        return elections;
    }


    public static List<Election> ELECTIONS = null;

    static {
        try {
            ELECTIONS = Collections.unmodifiableList((electionParties()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static List<Candidate> CANDIDATES = null;

    static {
        try {
            CANDIDATES = Collections.unmodifiableList(Arrays.asList(
                    new Candidate("Raul", "Paul",
                            generateUniqueStringForGivenCandidateAsID("Raul", "Paul", ELECTIONS.get(0).getElectionParty(), constituencies().get(0))
                            , ELECTIONS.get(0), Gender.MALE, constituencies().get(0)),
                    new Candidate("Jack", "Comedy",
                            generateUniqueStringForGivenCandidateAsID("Jack", "Comedy", ELECTIONS.get(1).getElectionParty(), constituencies().get(7))
                            , ELECTIONS.get(1), Gender.FEMALE, constituencies().get(7)),
                    new Candidate("Mark", "Wood",
                            generateUniqueStringForGivenCandidateAsID("Mark", "Wood", ELECTIONS.get(2).getElectionParty(), constituencies().get(15))
                            , ELECTIONS.get(2), Gender.MALE, constituencies().get(15)),
                    new Candidate("Stuart", "Henderson",
                            generateUniqueStringForGivenCandidateAsID("Stuart", "Henderson", ELECTIONS.get(4).getElectionParty(), constituencies().get(67))
                            , ELECTIONS.get(4), Gender.FEMALE, constituencies().get(67)),
                    new Candidate("Stuart", "Henderson",
                            generateUniqueStringForGivenCandidateAsID("Stuart", "Henderson", ELECTIONS.get(5).getElectionParty(), constituencies().get(67))
                            , ELECTIONS.get(5), Gender.OTHER, constituencies().get(67))
            ));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public static Candidate updateCandidateIDVotes(Candidate candidate, String candidateID) {
        return new Candidate(candidateID, candidate.getFirstName(), candidate.getLastName(), candidate.getGovId(),
                candidate.getElection(), candidate.getGender(), candidate.getConstituency()/*, candidate.getVotes()*/);
    }

//    public static Vote updateVotesByID(Vote vote/*, String voteID*/) {
//        return new Vote(new Candidate(vote.getCandidate().getFirstName(), vote.getCandidate().getLastName(), vote.getCandidate().getGovId(),
//                vote.getCandidate().getElection(), vote.getCandidate().getGender(), vote.getCandidate().getConstituency()), vote.getVoter());
//    }

    public static List<IonValue> toIonValues(Result result) {
        final List<IonValue> valueList = new ArrayList<>();
        result.iterator().forEachRemaining(valueList::add);
        return valueList;
    }

    public static String getDocumentId(final TransactionExecutor txn, final String tableName,
                                       final String identifier, final String value) {
        try {
            final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(value));
            final String query = String.format("SELECT metadata.id FROM _ql_committed_%s AS p WHERE p.data.%s = ?",
                    tableName, identifier);
            Result result = txn.execute(query, parameters);
            if (result.isEmpty()) {
                throw new IllegalStateException("Unable to retrieve document ID using " + value);
            }
            return getStringValueOfStructField((IonStruct) result.iterator().next(), "id");
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public static String getStringValueOfStructField(final IonStruct struct, final String fieldName) {
        return ((IonString) struct.get(fieldName)).stringValue();
    }


    public static String getDocumentIdByCandidateId(final TransactionExecutor txn, final String candidateID) {
        return SampleData.getDocumentId(txn, Constants.CANDIDATE_TABLE_NAME, "id", candidateID);
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("I am into an Spring Component");
            //QLDBConfig.getDriver();
            CreateSecureVotingSysLedger ledger = new CreateSecureVotingSysLedger();
            ledger.create();
            CandidateTable tableService = new CandidateTable();
            tableService.createTable();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            ElectionTable tableService1 = new ElectionTable();
            tableService1.createTable();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            tableService.createIndex();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            tableService1.createIndex();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            VoteTable voteTable = new VoteTable();
            voteTable.createTable();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            voteTable.createIndex();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            VoterTable voterTable = new VoterTable();
            voterTable.createTable();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            voterTable.createIndex();
            Thread.sleep(LEDGER_CREATION_POLL_PERIOD_MS);
            CandidateInsertDocument insertDocument = new CandidateInsertDocument();
            // Loading data into QLDB
            insertDocument.insert();
        } catch (Exception e) {
            log.error("Connection Not Successful", e);
        }
    }
}
