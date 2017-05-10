package juja.microservices.users.dao;

import juja.microservices.users.entity.User;
import juja.microservices.users.exceptions.UserException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;

import java.util.List;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */
@Repository
public class CRMUserRepository implements UserRepository{

    private final String x2_user = "apiuser";
    private final String x2_apikey = "password";
    private final String X2_BASE_URL = "http://127.0.0.1/x2engine/index.php/api2/";
    private final RestTemplate restTemplate;

    @Inject
    public CRMUserRepository(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeaders(final String username, final String password ){
        HttpHeaders headers =  new HttpHeaders(){
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.getEncoder().encode(
                        auth.getBytes(Charset.forName("US-ASCII")) );
                String authHeader = "Basic " + new String( encodedAuth );
                set( "Authorization", authHeader );
            }
        };
        return headers;
    }

    @Override
    public List<User> getAllUsers(){
        URI targetUrl= UriComponentsBuilder.fromUriString(X2_BASE_URL)
                .path("Contacts")
                .queryParam("c_isStudent","1")
                .build()
                .toUri();

        ResponseEntity<List<User>> response = restTemplate.exchange(targetUrl, HttpMethod.GET,
                new HttpEntity<>(createHeaders(x2_user, x2_apikey)),
                new ParameterizedTypeReference<List<User>>() {});

        return response.getBody();
    }

    @Override
    public User getUserBySlack(String slack) {
        UriComponentsBuilder uriComponentsBuilder= UriComponentsBuilder.fromUriString(X2_BASE_URL)
                .path("Contacts")
                .queryParam("c_isStudent","1")
                .queryParam("c_slack", slack);

        URI targetUrl= uriComponentsBuilder
                .build()
                .toUri();

        ResponseEntity<List<User>> response = restTemplate.exchange(targetUrl, HttpMethod.GET,
                new HttpEntity<>(createHeaders(x2_user, x2_apikey)),
                new ParameterizedTypeReference<List<User>>() {});

        List<User> users = (ArrayList)response.getBody();
        if (users.size() == 0) {
            throw new UserException("No users found by your request!");
        } else if (users.size() > 1) {
            throw new UserException("More than one user found with the same slack name");
        }
        return users.get(0);
    }
}
