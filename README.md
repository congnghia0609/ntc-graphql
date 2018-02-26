# ntc-graphql
ntc-graphql is example template GraphQL in Java. Include Jetty  + GraphQL + MongoDB.  

## MongoDB
### Start MongoDB
```
#Start mongo
sudo mongod --config /etc/mongod.conf
or
nohup sudo mongod --config /etc/mongod.conf >/dev/null 2>&1 &
```
  
  
## GraphQL
  
```
type Link {
    id: ID!
    url: String!
    description: String!
    postedBy: User
}

input LinkFilter {
  description_contains: String
  url_contains: String
}

type Query {
    allLinks(filter: LinkFilter, skip: Int = 0, first: Int = 0): [Link]
}

type User {
    id: ID!
    name: String!
    email: String
    password: String
}

input AuthData {
    email: String!
    password: String!
}

type SigninPayload {
  token: String
  user: User
}

type Vote {
    id: ID!
    createdAt: DateTime!
    user: User!
    link: Link!
}

scalar DateTime

type Mutation {
    createUser(name: String!, authProvider: AuthData!): User
    signinUser(auth: AuthData): SigninPayload
    createLink(url: String!, description: String!): Link
    createVote(linkId: ID, userId: ID): Vote
}

schema {
    query: Query
    mutation: Mutation
}
```

## Start ntc-graphql

Open browser: [http://localhost:8080/](http://localhost:8080/)  
  
Try query:  
```
// Create User
mutation createUser{
    createUser(
        name: "nghia1"
        authProvider: {
            email: "nghia1@gmail.com"
            password: "123456"
        }
    ) {
        id
        name
    }
}

// SignIn get token
mutation signIn{
  signinUser(
    auth: {email: "nghia1@gmail.com", password: "123456"}
  ){
    token
    user{
      id
      name
    }
  }
}

// Change header of request GraphiQL in views/layout.xtm
headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'Authorization': 'Bearer 5a930061cf85664ef0afe029',
}

// Create Link
mutation link {
    createLink(
        url: "https://streetcodevn.com"
        description: "streetcodevn very cool."
    ) {
        id
        url
    }
}

// Query Link
{
  allLinks(skip: 0, first: 5){
    id
    url
    description
    postedBy {
      id
      name
    }
  }
}
```
