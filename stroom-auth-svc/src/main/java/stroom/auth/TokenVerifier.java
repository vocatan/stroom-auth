/*
 *
 *   Copyright 2017 Crown Copyright
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package stroom.auth;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.auth.config.TokenConfig;
import stroom.auth.daos.TokenDao;
import stroom.auth.resources.token.v1.Token;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class TokenVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenDao.class);

    private RsaJsonWebKey jwk;

    @Inject
    private TokenConfig tokenConfig;

    @Inject
    private TokenDao tokenDao;

    private JwtConsumer consumer;

    @Inject
    public void init() throws NoSuchAlgorithmException, JoseException {
        jwk = RsaJwkGenerator.generateJwk(2048);
        jwk.setKeyId("k1");

        JwtConsumerBuilder builder = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setVerificationKey(jwk.getPublicKey()) // verify the signature with the public key
                .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                        new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, // which is only RS256 here
                                AlgorithmIdentifiers.RSA_USING_SHA256))
                .setRelaxVerificationKeyValidation() // relaxes key length requirement
                .setExpectedIssuer(tokenConfig.getJwsIssuer());

        if (tokenConfig.isRequireExpirationTime()) {
            builder = builder.setRequireExpirationTime();
        }

        consumer = builder.build();
    }

    public Optional<String> verifyToken(String token) {
        try {
            final JwtClaims claims = consumer.processToClaims(token);
            claims.getSubject();
        } catch (InvalidJwtException | MalformedClaimException e) {
            LOGGER.warn("There was an issue with a token!", e);
            return Optional.empty();
        }

        Optional<Token> tokenRecord = tokenDao.readByToken(token);
        if (!tokenRecord.isPresent()) {
            LOGGER.warn("I tried to verify a token but that token doesn't exist.");
            return Optional.empty();
        }

        if (!tokenRecord.get().isEnabled()) {
            LOGGER.warn("Someone tried to verify a token that is disabled.");
            return Optional.empty();
        }
        LOGGER.info("Looks like this token is fine.");
        return Optional.of(tokenRecord.get().getUser_email());
    }

    public JwtConsumer getJwtConsumer(){
        return this.consumer;
    }

    public RsaJsonWebKey getJwk() {
        return jwk;
    }
}
