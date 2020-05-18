package com.example.reactive.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    WebClient webClient = WebClient.create("http://www.mocky.io/v2");
    RestTemplate restTemplate = new RestTemplate();

    ObjectMapper mapper = new ObjectMapper();

    private DataFetcher getBooksDataFetcher() {
        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment environment) throws Exception {
                Integer limit = (Integer) environment.getArgument("limit");
                if (limit != null) return books.stream().limit(limit).toArray();
                else return getListRestObjects();

            }
        };
    }

    private Map<String, Object> getOneRestObject() throws JsonProcessingException {
        Mono<String> rest2 = getRestResponse();
        String data = rest2.block();
        List<Map<String, Object>> data2 = getListRestObjects();

        return mapper.readValue(data, new TypeReference<Map<String, Object>>() {
        });
    }

    private List<Map<String, Object>> getListRestObjects() throws JsonProcessingException {
        Mono<String> rest = getRestListOfResponse();
        String data = rest.block();
        System.out.println(data);

        return mapper.readValue(data, new TypeReference<List<Map<String, Object>>>() {
        });

    }

    private DataFetcher getRestRes() {
        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment environment) throws Exception {
                return getOneRestObject();
            }
        };
    }

    private Mono<String> getRestResponse() {
        return webClient.get()
                .uri("/5ec267442f00009250c35251")
                .retrieve()
                .bodyToMono(String.class);
    }

    private Mono<String> getRestListOfResponse() {
        return webClient.get()
                .uri("/5ec279462f000057b5c35328")
                .retrieve()
                .bodyToMono(String.class);
    }

    private DataFetcher getMonoRestResponse() {
        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment environment) throws Exception {
                return getRestResponse();
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
            case 3 -> getRestRes();
            default -> null;
        };

    }
}
