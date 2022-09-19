package com.blockchain.voting.model.consts;

import com.amazon.ion.IonSystem;
import com.amazon.ion.system.IonSystemBuilder;
import com.fasterxml.jackson.dataformat.ion.IonObjectMapper;
import com.fasterxml.jackson.dataformat.ion.ionvalue.IonValueMapper;

/**
 * Created on 10/08/2022
 */

public class Constants {
    public static final String LEDGER_NAME = "Voting";

    public static final String CANDIDATE_TABLE_NAME = "Candidates";
    public static final String CANDIDATE_NAME_INDEX = "CandidateName";
    public static final String CANDIDATE_ID_INDEX = "CandidateID";

    public static final String ELECTION_TABLE_NAME = "Election";
    public static final String ELECTION_TYPE_INDEX = "ElectionType";
    public static final String ELECTION_ID_INDEX = "ElectionID";


    /**
     * Vote Table Details
     */


    public static final String VOTE_TABLE_NAME = "Vote";
    public static final String VOTE_ID_INDEX = "VoteID";
    public static final String VOTED_CANDIDATE_INDEX = "CandidateVoted";
    public static final String VOTE_ELECTION_INDEX = "Election";


    /**
     * Voter Table Details
     */


    public static final String VOTER_TABLE_NAME = "Voter";
    public static final String VOTER_ID_INDEX = "VoterID";
    public static final String VOTER_FIRSTNAME_INDEX = "VoterFirstName";
    public static final String VOTER_LASTNAME_INDEX = "VoterLastName";
    public static final String VOTER_USERNAME_INDEX = "username";
    public static final String VOTER_PASSWORD_INDEX = "password";

    public static final String VOTER_GENDER = "Gender";
    public static final String VOTER_LOCATION = "Location";


    /**
     * Other
     */


    public static final IonSystem SYSTEM = IonSystemBuilder.standard().build();
    public static final IonObjectMapper MAPPER = new IonValueMapper(SYSTEM);
    public static final String USER_TABLES = "information_schema.user_tables";
    public static final String JOURNAL_EXPORT_S3_BUCKET_NAME_PREFIX = "qldb-tutorial-journal-export";



}
