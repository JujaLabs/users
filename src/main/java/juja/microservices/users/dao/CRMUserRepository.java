package juja.microservices.users.dao;


import juja.microservices.users.entity.User;
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
import java.util.Map;


/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */
@Repository
public class CRMUserRepository implements UserRepository{

    //TODO: read credentials from file (or something better) and remove this test account
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

        ResponseEntity<List<User>> response;
        response = restTemplate.exchange(
                targetUrl, HttpMethod.GET, new HttpEntity<Object>(createHeaders(x2_user, x2_apikey)), new ParameterizedTypeReference<List<User>>() {
                });

        List<User> users = response.getBody();

        return users;
    }

    @Override
    public List<User> getUsersByParameters(Map<String, String> fields) {

        UriComponentsBuilder uriComponentsBuilder= UriComponentsBuilder.fromUriString(X2_BASE_URL)
                .path("Contacts")
                .queryParam("c_isStudent","1");

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getKey().equals("uuid") && !entry.getValue().isEmpty())
                uriComponentsBuilder.queryParam("c_uuid", entry.getValue());
            if (entry.getKey().equals("slack") && !entry.getValue().isEmpty())
                uriComponentsBuilder.queryParam("c_slack", entry.getValue());
            if (entry.getKey().equals("email") && !entry.getValue().isEmpty())
                uriComponentsBuilder.queryParam("email", entry.getValue());
        }
        URI targetUrl= uriComponentsBuilder
                .build()
                .toUri();

        ResponseEntity<List<User>> response;
        response = restTemplate.exchange(
                targetUrl, HttpMethod.GET, new HttpEntity<Object>(createHeaders(x2_user, x2_apikey)), new ParameterizedTypeReference<List<User>>() {
                });

        ArrayList<User> users = (ArrayList)response.getBody();
        return users;
    }
}
