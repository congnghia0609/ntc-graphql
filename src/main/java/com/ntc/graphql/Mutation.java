/*
 * Copyright 2018 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ntc.graphql;

import com.ntc.graphql.auth.AuthData;
import com.ntc.graphql.auth.SigninPayload;
import com.ntc.graphql.auth.AuthContext;
import com.ntc.graphql.vote.VoteRepository;
import com.ntc.graphql.vote.Vote;
import com.ntc.graphql.user.UserRepository;
import com.ntc.graphql.user.User;
import com.ntc.graphql.link.Link;
import com.ntc.graphql.link.LinkRepository;
import com.coxautodev.graphql.tools.GraphQLRootResolver;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 *
 * @author nghiatc
 * @since Feb 26, 2018
 */
public class Mutation implements GraphQLRootResolver {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public Mutation(LinkRepository linkRepository, UserRepository userRepository, VoteRepository voteRepository) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }
    
//    public Link createLink(String url, String description) {
//        Link newLink = new Link(url, description);
//        linkRepository.saveLink(newLink);
//        return newLink;
//    }
    
    //The way to inject the context is via DataFetchingEnvironment
    public Link createLink(String url, String description, DataFetchingEnvironment env) {
        AuthContext context = env.getContext();
        Link newLink = new Link(url, description, context.getUser().getId());
        linkRepository.saveLink(newLink);
        return newLink;
    }
    
    public User createUser(String name, AuthData auth) {
        User newUser = new User(name, auth.getEmail(), auth.getPassword());
        return userRepository.saveUser(newUser);
    }
    
    public SigninPayload signinUser(AuthData auth) throws IllegalAccessException {
        User user = userRepository.findByEmail(auth.getEmail());
        if (user.getPassword().equals(auth.getPassword())) {
            return new SigninPayload(user.getId(), user); // Replace UserId by JWT.
        }
        throw new GraphQLException("Invalid credentials");
    }
    
    public Vote createVote(String linkId, String userId) {
        ZonedDateTime now = Instant.now().atZone(ZoneOffset.UTC);
        return voteRepository.saveVote(new Vote(now, userId, linkId));
    }
}
