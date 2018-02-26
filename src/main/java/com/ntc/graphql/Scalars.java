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

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author nghiatc
 * @since Feb 26, 2018
 */
public class Scalars {
    public static GraphQLScalarType dateTime = new GraphQLScalarType("DateTime", "DataTime scalar", new Coercing() {
        @Override
        public String serialize(Object input) {
            //serialize the ZonedDateTime into string on the way out
            return ((ZonedDateTime)input).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        @Override
        public Object parseValue(Object input) {
            return serialize(input);
        }

        @Override
        public ZonedDateTime parseLiteral(Object input) {
            //parse the string values coming in
            if (input instanceof StringValue) {
                return ZonedDateTime.parse(((StringValue) input).getValue());
            } else {
                return null;
            }
        }
    });
}
