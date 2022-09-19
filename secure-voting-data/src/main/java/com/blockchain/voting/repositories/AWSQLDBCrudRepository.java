package com.blockchain.voting.repositories;

import com.amazon.ion.IonStruct;
import com.blockchain.voting.model.Vote;

import java.util.List;

@NoRepositoryBean
public interface AWSQLDBCrudRepository<T, ID> extends AWSRepository<T, ID> {
    List<IonStruct> findAll(final String tableName);

    List<T> findById(String id, String tableName);

    T updateById(String id, String tableName, Vote voteDetails);

}
