package com.blockchain.voting.services;

import com.amazon.ion.IonStruct;
import com.blockchain.voting.model.Vote;

import java.util.List;

public interface CrudService<T, ID> {

    List<IonStruct> findAll(final String tableName);

    List<T> findById(String id, String tableName);
}
