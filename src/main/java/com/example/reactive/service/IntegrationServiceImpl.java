package com.example.reactive.service;

import com.example.reactive.graphql.GQLQueryDetails;
import com.example.reactive.graphql.Query;
import com.example.reactive.graphql.QueryService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private QueryService queryService;

    private GQLQueryDetails initDb(String operationName) {
        Map<String, GQLQueryDetails> database = new HashMap<>();
        GQLQueryDetails details1 = new GQLQueryDetails();
        details1.setFieldName("findBookById");
        details1.setTypeName("Query");
        details1.setId(1);
        database.put("findBookById", details1);

        GQLQueryDetails details2 = new GQLQueryDetails();
        details2.setFieldName("findBooks");
        details2.setTypeName("Query");
        details2.setId(2);
        database.put("findBooks", details2);


        return database.get(operationName);
    }

    @Override
    public Mono<Object> doIntegrate(Mono<Query> query) {
        if (query == null) throw new IllegalArgumentException();

        return Mono.create(sink -> {
            executorService.submit(() -> {
                try {
                    Query gqlQuery = query.block();
                    assert gqlQuery != null;
                    String operationName = gqlQuery.getOperationName();
                    if (operationName == null || operationName.isEmpty()) throw new IllegalArgumentException();

                    GQLQueryDetails gqlQueryDetails = initDb(operationName);

                    SchemaParser schemaParser = new SchemaParser();
                    TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(gqlQueryDetails.getSchema());

                    // need to be checked for complex entities/ inputs | API, DB, File data source
                    RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                            .type(gqlQueryDetails.getTypeName(), builder ->
                                    builder.dataFetcher(gqlQueryDetails.getFieldName(),
                                            queryService.doQuery(gqlQueryDetails.getId()))) // need to research how this works
                            .build();

                    SchemaGenerator schemaGenerator = new SchemaGenerator();
                    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

                    GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();

                    ExecutionInput executionInput =
                            ExecutionInput.newExecutionInput()
                                    .query(gqlQuery.getQuery())
                                    .operationName(gqlQuery.getOperationName())
                                    .variables(gqlQuery.getVariables())
                                    .context(typeDefinitionRegistry)
                                    .build();

                    ExecutionResult executionResult = build.execute(executionInput);
                    // task execution time
                    Object data = executionResult.getData();
                    sink.success(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error");
                }
            });
        });
    }
}
