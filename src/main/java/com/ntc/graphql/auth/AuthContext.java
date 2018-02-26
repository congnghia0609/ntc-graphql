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

package com.ntc.graphql.auth;

import com.ntc.graphql.user.User;
import graphql.servlet.GraphQLContext;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author nghiatc
 * @since Feb 26, 2018
 */
public class AuthContext extends GraphQLContext {
    private final User user;

    public AuthContext(User user, Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
        super(request, response);
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }

}
