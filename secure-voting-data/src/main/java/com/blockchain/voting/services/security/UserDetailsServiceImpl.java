package com.blockchain.voting.services.security;


import com.blockchain.voting.model.User;
import com.blockchain.voting.model.Voter;
import com.blockchain.voting.model.consts.Constants;
import com.blockchain.voting.services.VoterService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final VoterService voterService;

    public UserDetailsServiceImpl(VoterService voterService) {
        this.voterService = voterService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Voter voter = voterService.findByUsername(username, Constants.VOTER_TABLE_NAME).get(0);
        User user = new User();
        user.setUsername(voter.getUsername());
        user.setPassword(voter.getPassword());
        user.setRoles(voter.getRoles());
    /*
     .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username))*/
        ;

        return UserDetailsImpl.build(voter);
    }
}
