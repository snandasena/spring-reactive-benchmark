package com.example.reactive.service;

import com.example.reactive.graphql.QueryService;
import com.example.reactive.models.Query;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;

@Service
public class IntegrationServiceImpl implements IntegrationService {
    private static final String SCHEMA =
            "type Query {\n" +
                    "    bookById(id: ID): Book\n" +
                    "}\n" +
                    "type Book {\n" +
                    "    id: ID\n" +
                    "    name: String\n" +
                    "    pageCount: Int\n" +
                    "    author: Author\n" +
                    "}\n" +
                    "type Author {\n" +
                    "    id: ID\n" +
                    "    firstName: String\n" +
                    "    lastName: String\n" +
                    "}";

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private QueryService queryService;

    @Override
    public Mono<Object> doIntegrate(Mono<Query> query) {
        if (query == null) throw new IllegalArgumentException();

        return Mono.create(sink -> {
            executorService.submit(() -> {
                try {
                    SchemaParser schemaParser = new SchemaParser();
                    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(SCHEMA);

                    // need to be checked for complex entities
                    RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                            .type("Query", builder ->
                                    builder.dataFetcher("bookById", queryService.getBookByIdDataFetcher())) // need to research how this works
                            .type("Book", builder ->
                                    builder.dataFetcher("author", queryService.getAuthorDataFetcher())) // need to research how this works
                            .build();

                    SchemaGenerator schemaGenerator = new SchemaGenerator();
                    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

                    GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
                    Query gqlQuery = query.block();

                    assert gqlQuery != null;
                    ExecutionInput executionInput =
                            ExecutionInput.newExecutionInput()
                                    .query(gqlQuery.getQuery())
                                    .operationName(gqlQuery.getOperationName())
                                    .variables(gqlQuery.getVariables())
                                    .context(typeDefinitionRegistry)
                                    .build();

                    ExecutionResult executionResult = build.execute(executionInput);
                    // task execution time
                    sink.success(executionResult.getData());
                } catch (Exception e) {
                    throw new RuntimeException("Error");
                }
            });
        });
    }
}
