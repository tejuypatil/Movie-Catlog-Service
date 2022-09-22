package com.example.moviecatlogservice.controller;

import com.example.moviecatlogservice.module.CatalogItem;
import com.example.moviecatlogservice.module.Movie;
import com.example.moviecatlogservice.module.Rating;
import com.example.moviecatlogservice.module.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private WebClient.Builder webClientBuilder;
    @RequestMapping("/{userId}")
    public List<CatalogItem> getcatalog(@PathVariable("userId") String userId){

       UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);

        return ratings.getUserRating().stream().map(rating ->{
            //for each movie ID, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movie/" + rating.getMovieId(), Movie.class);
            //Put them all together
            return new CatalogItem(movie.getName(), "Desc", rating.getRating());
        }).collect(Collectors.toList());
    }
}
 /* Movie movie = webClientBuilder.build().get().uri("http://localhost:8082/movie/" + rating.getMovieId(), Movie.class)
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
            */