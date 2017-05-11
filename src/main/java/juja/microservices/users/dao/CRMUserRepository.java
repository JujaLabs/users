package juja.microservices.users.dao;

import juja.microservices.users.entity.User;
import juja.microservices.users.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CRMUserRepository implements UserRepository {

    private final String x2_user = "apiuser";
    private final String x2_apikey = "password";
    private final String X2_BASE_URL = "http://127.0.0.1/x2engine/index.php/api2/";
    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public CRMUserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders createHeaders(final String username, final String password) {
        HttpHeaders headers = new HttpHeaders() {
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.getEncoder().encode(
                        auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String(encodedAuth);
                set("Authorization", authHeader);
            }
        };
        return headers;
    }

    @Override
    public List<User> getAllUsers() {
        URI targetUrl = UriComponentsBuilder.fromUriString(X2_BASE_URL)
                .path("Contacts")
                .queryParam("c_isStudent", "1")
                .build()
                .toUri();

        ResponseEntity<List<User>> response = restTemplate.exchange(targetUrl, HttpMethod.GET,
                new HttpEntity<>(createHeaders(x2_user, x2_apikey)),
                new ParameterizedTypeReference<List<User>>() {});

        return response.getBody();
    }

    @Override
    public User getUserBySlack(String slack) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(X2_BASE_URL)
                .path("Contacts")
                .queryParam("c_isStudent", "1")
                .queryParam("c_slack", slack);

        return getUser(uriComponentsBuilder);
    }

    @Override
    public User getUserByUuid(String uuid) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(X2_BASE_URL)
                .path("Contacts")
                .queryParam("c_isStudent", "1")
                .queryParam("c_uuid", uuid);

        return getUser(uriComponentsBuilder);
    }

    private User getUser(UriComponentsBuilder uriComponentsBuilder) {
        URI targetUrl = uriComponentsBuilder
                .build()
                .toUri();

        ResponseEntity<List<User>> response = restTemplate.exchange(targetUrl, HttpMethod.GET,
                new HttpEntity<>(createHeaders(x2_user, x2_apikey)),
                new ParameterizedTypeReference<List<User>>() {
                });

        List<User> users = (ArrayList) response.getBody();
        if (users.size() == 0) {
            String message = "No users found by your request!";
            logger.info(message);
            throw new UserException(message);
        } else if (users.size() > 1) {
            String message = "More than one user found with the same unique field";
            logger.warn(message);
            throw new UserException(message);
        }
        logger.debug("Founded user {}", users.get(0));
        return users.get(0);
    }
}