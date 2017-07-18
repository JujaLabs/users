package juja.microservices.users.dao;

import juja.microservices.users.entity.User;
import juja.microservices.users.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 * @author Olga Kulykova
 */
@Repository
public class CRMUserRepository implements UserRepository {

    @Value("${x2.user}")
    private String x2User;

    @Value("${x2.password}")
    private String x2Password;

    @Value("${x2.baseUrl}")
    private String x2BaseUrl;

    @Value("${x2.contactsUrl}")
    private String x2ContactsUrl;

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
        URI targetUrl = UriComponentsBuilder.fromUriString(x2BaseUrl)
                .path(x2ContactsUrl)
                .queryParam("c_isStudent", "1")
                .build()
                .toUri();
        logger.debug("Prepared target URL for response to CRM: {}", targetUrl.toString());

        logger.debug("Send response to CRM");
        ResponseEntity<List<User>> response = restTemplate.exchange(targetUrl, HttpMethod.GET,
                new HttpEntity<>(createHeaders(x2User, x2Password)),
                new ParameterizedTypeReference<List<User>>() {
                });
        logger.debug("Received data from CRM: {}", response.getBody());

        return response.getBody();
    }

    @Override
    public User getUserBySlack(String slack) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(x2BaseUrl)
                .path(x2ContactsUrl)
                .queryParam("c_isStudent", "1")
                .queryParam("c_slack", slack);
        logger.debug("Prepared target URI for response to CRM: {}", uriComponentsBuilder.toUriString());

        return getUser(uriComponentsBuilder);
    }

    @Override
    public User getUserByUuid(String uuid) {

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(x2BaseUrl)
                .path(x2ContactsUrl)
                .queryParam("c_isStudent", "1")
                .queryParam("c_uuid", uuid);
        logger.debug("Prepared target URI for response to CRM: {}", uriComponentsBuilder.toUriString());

        return getUser(uriComponentsBuilder);
    }

    private User getUser(UriComponentsBuilder uriComponentsBuilder) {
        URI targetUrl = uriComponentsBuilder
                .build()
                .toUri();
        logger.debug("Send response to CRM, target URL: {}", targetUrl.toString());
        ResponseEntity<List<User>> response = restTemplate.exchange(targetUrl, HttpMethod.GET,
                new HttpEntity<>(createHeaders(x2User, x2Password)),
                new ParameterizedTypeReference<List<User>>() {
                });

        List<User> users = response.getBody();
        logger.debug("Received data from CRM: {}", users);

        if (users.size() == 0) {
            String message = "No users found by your request!";
            logger.warn(message);
            throw new UserException(message);
        } else if (users.size() > 1) {
            String message = "More than one user found with the same unique field";
            logger.warn(message);
            throw new UserException(message);
        }
        logger.debug("Founded user {}", users.get(0));
        return users.get(0);
    }

    @Override
    public User getUserById(String id) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(x2BaseUrl)
                .path(x2ContactsUrl)
                .queryParam("c_isStudent", "1")
                .queryParam("c_id", id);

        return getUser(uriComponentsBuilder);
    }
}