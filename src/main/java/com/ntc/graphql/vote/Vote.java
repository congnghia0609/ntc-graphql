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

package com.ntc.graphql.vote;

import java.time.ZonedDateTime;

/**
 *
 * @author nghiatc
 * @since Feb 26, 2018
 */
public class Vote {
    private final String id;
    private final ZonedDateTime createdAt;
    private final String userId;
    private final String linkId;

    public Vote(ZonedDateTime createdAt, String userId, String linkId) {
        this(null, createdAt, userId, linkId);
    }

    public Vote(String id, ZonedDateTime createdAt, String userId, String linkId) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.linkId = linkId;
    }

    public String getId() {
        return id;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getLinkId() {
        return linkId;
    }
}
