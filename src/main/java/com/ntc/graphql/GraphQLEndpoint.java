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

import com.ntc.graphql.auth.SigninResolver;
import com.ntc.graphql.auth.AuthContext;
import com.ntc.graphql.vote.VoteResolver;
import com.ntc.graphql.vote.VoteRepository;
import com.ntc.graphql.user.UserRepository;
import com.ntc.graphql.user.User;
import com.ntc.graphql.link.LinkResolver;
import com.ntc.graphql.link.LinkRepository;
import com.coxautodev.graphql.tools.SchemaParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author nghiatc
 * @since Feb 25, 2018
 */
@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {
    
    private static String schema = readContentFile("./graphql/schema.graphqls");
    private static final LinkRepository linkRepository;
    private static final UserRepository userRepository;
    private static final VoteRepository voteRepository;
    
    static {
        //Change to `new MongoClient("mongodb://<host>:<port>/hackernews")`
        //if you don't have Mongo running locally on port 27017
        MongoDatabase mongo = new MongoClient().getDatabase("hackernews");
        linkRepository = new LinkRepository(mongo.getCollection("links"));
        userRepository = new UserRepository(mongo.getCollection("users"));
        voteRepository = new VoteRepository(mongo.getCollection("votes"));
    }
    
    public GraphQLEndpoint() {
        super(buildSchema());
    }
    
    private static GraphQLSchema buildSchema() {
        return SchemaParser.newParser()
                //.file("schema.graphqls") // /home/nghiatc/lab/labGraphQL/ntc-graphql/graphql/schema.graphqls
                .schemaString(schema)
                .resolvers(
                        new Query(linkRepository), 
                        new Mutation(linkRepository, userRepository, voteRepository),
                        new SigninResolver(),
                        new LinkResolver(userRepository),
                        new VoteResolver(linkRepository, userRepository)
                )
                .scalars(Scalars.dateTime) //register the new scalar
                .build()
                .makeExecutableSchema();
    }
    
    @Override
    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
        // not safe, throw exception query mongodb.
        User user = request
            .map(req -> req.getHeader("Authorization"))
            .filter(id -> !id.isEmpty())
            .map(id -> id.replace("Bearer ", ""))
            .map(userRepository::findById)
            .orElse(null);
        return new AuthContext(user, request, response);
    }
    
    @Override
    protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
        return errors.stream()
                .filter(e -> e instanceof ExceptionWhileDataFetching || super.isClientError(e))
                .map(e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
                .collect(Collectors.toList());
    }
    
    public static String readContentFile(String pathFile){
        String rs = "";
        try {
            rs = new String(Files.readAllBytes(Paths.get(pathFile)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }
}
