package com.blockchain.voting.controllers;

import com.blockchain.voting.model.Candidate;
import com.blockchain.voting.model.Vote;
import com.blockchain.voting.model.Voter;
import com.blockchain.voting.services.CandidateService;
import com.blockchain.voting.services.VoterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.blockchain.voting.model.consts.AWSQldb.intoJson;


/**
 * Created on 17 Aug 2022
 */

@RequestMapping({ "/candidates"})
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class CandidateController {
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CandidateService candidateService;

    private final VoterService voterService;

    public CandidateController(CandidateService candidateService, VoterService voterService) {
        this.candidateService = candidateService;
        this.voterService = voterService;
    }

    @GetMapping
    public String getAllCandidates() throws IOException {
        return intoJson(candidateService.findAll("Candidates"));
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<List<Candidate>> getCandidateById(@PathVariable String id) {
        return new ResponseEntity<>(candidateService.findById(id, "Candidates"), HttpStatus.OK);
    }

    @PostMapping({"/{id}/vote"})
    public ResponseEntity<Vote> voteForCandidate(@PathVariable String id, @RequestBody Vote votedCandidate) {
        return new ResponseEntity<>(candidateService.voteByCandidateId(id, "Candidates", votedCandidate), HttpStatus.OK);
    }
}