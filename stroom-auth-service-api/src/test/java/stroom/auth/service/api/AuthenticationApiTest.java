/*
 * Stroom Auth API
 * Various APIs for interacting with authentication, users, and tokens.
 *
 * OpenAPI spec version: v1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package stroom.auth.service.api;

import stroom.auth.service.ApiException;
import stroom.auth.service.api.model.IdTokenRequest;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for AuthenticationApi
 */
@Ignore
public class AuthenticationApiTest {

    private final AuthenticationApi api = new AuthenticationApi();

    
    /**
     * Convert a previously provided access code into an ID token
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getIdTokenWithGetTest() throws ApiException {
        String accessCode = null;
        String clientId = null;
        String response = api.getIdTokenWithGet(accessCode, clientId);

        // TODO: test validations
    }
    
    /**
     * Convert a previously provided access code into an ID token
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getIdTokenWithPostTest() throws ApiException {
        IdTokenRequest body = null;
        String response = api.getIdTokenWithPost(body);

        // TODO: test validations
    }
    
    /**
     * Submit an OpenId AuthenticationRequest.
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void handleAuthenticationRequestTest() throws ApiException {
        String clientId = null;
        String redirectUrl = null;
        String nonce = null;
        String scope = null;
        String responseType = null;
        String state = null;
        String sessionId = null;
        String response = api.handleAuthenticationRequest(clientId, redirectUrl, nonce, scope, responseType, state, sessionId);

        // TODO: test validations
    }
    
}
