package com.blockchain.voting.services;

import com.amazon.ion.IonStruct;
import com.blockchain.voting.model.ERole;
import com.blockchain.voting.model.Role;
import com.blockchain.voting.model.Voter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface VoterService {

    List<IonStruct> findAll(final String tableName);

    List<Voter> findByUsername(String username, String tableName);

    Voter addVoter(Voter voter);

    Boolean existsByUsername(String username) throws IOException;

    Optional<Role> findByName(ERole name);

}
