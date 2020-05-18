package com.example.reactive.graphql;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class QueryService {

    private static List<Map<String, Object>> books = Arrays.asList(
            ImmutableMap.of("id", "book-1",
                    "name", "Harry Potter and the Philosopher's Stone",
                    "pageCount", "223",
                    "author", ImmutableMap.of("id", "author-1",
                            "firstName", "Joanne",
                            "lastName", "Rowling")),

            ImmutableMap.of("id", "book-2",
                    "name", "Moby Dick",
                    "pageCount", "635",
                    "author", ImmutableMap.of("id", "author-2",
                            "firstName", "Herman",
                            "lastName", "Melville")),

            ImmutableMap.of("id", "book-3",
                    "name", "Interview with the vampire",
                    "pageCount", "371",
                    "author", ImmutableMap.of("id", "author-3",
                            "firstName", "Anne",
                            "lastName", "Rice")
            ));


    private DataFetcher getBooksDataFetcher() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment environment) throws Exception {
                Integer limit = (Integer) environment.getArgument("limit");
                if (limit != null) return books.stream().limit(limit).toArray();
                else return books;

            }
        };
    }

    private DataFetcher getBookByIdDataFetcher() {

        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment environment) throws Exception {
                Map<String, Object> aruguments = environment.getArguments();
                String id = (String) aruguments.get("id");

                // backend api
                return books
                        .stream()
                        .filter(book -> book.get("id").equals(id))
                        .findFirst()
                        .orElse(null);
            }
        };
    }


    public DataFetcher doQuery(int route) {
        return switch (route) {
            case 2 -> getBooksDataFetcher();
            case 1 -> getBookByIdDataFetcher();
            default -> null;
        };

    }
}
