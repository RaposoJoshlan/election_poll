package com.blockchain.voting.services;

import com.blockchain.voting.model.Candidate;
import com.blockchain.voting.model.Vote;

public interface CandidateService extends CrudService<Candidate, String> {
    Vote voteByCandidateId(String id, String tableName, Vote voteDetails);

}
