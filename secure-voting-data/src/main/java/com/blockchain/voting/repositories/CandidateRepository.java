package com.blockchain.voting.repositories;

import com.amazon.ion.IonStruct;
import com.blockchain.voting.model.Candidate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created on 11 Aug 2022
 */

public interface CandidateRepository extends AWSQLDBCrudRepository<Candidate, String> {
}
