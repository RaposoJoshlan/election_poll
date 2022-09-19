package com.blockchain.voting.repositories;

import com.blockchain.voting.model.Voter;

/**
 * Created on 12 Sep 2022
 */
public interface VoterRepository extends AWSQLDBCrudRepository<Voter, String> {
}
