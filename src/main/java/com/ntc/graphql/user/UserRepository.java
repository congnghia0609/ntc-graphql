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

package com.ntc.graphql.user;

import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author nghiatc
 * @since Feb 26, 2018
 */
public class UserRepository {
    private final MongoCollection<Document> users;

    public UserRepository(MongoCollection<Document> users) {
        this.users = users;
    }
    
    public User findByEmail(String email) {
        Document doc = users.find(eq("email", email)).first();
        return user(doc);
    }

    public User findById(String id) {
        Document doc = users.find(eq("_id", new ObjectId(id))).first();
        return user(doc);
    }
    
    public User saveUser(User user) {
        Document doc = new Document();
        doc.append("name", user.getName());
        doc.append("email", user.getEmail());
        doc.append("password", user.getPassword());
        users.insertOne(doc);
        return new User(
                doc.get("_id").toString(),
                user.getName(),
                user.getEmail(),
                user.getPassword());
    }
    
    private User user(Document doc) {
        return new User(
                doc.get("_id").toString(),
                doc.getString("name"),
                doc.getString("email"),
                doc.getString("password"));
    }
}
