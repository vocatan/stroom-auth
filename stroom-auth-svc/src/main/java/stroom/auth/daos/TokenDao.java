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

package stroom.auth.daos;

import com.google.common.base.Strings;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSelectStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jose4j.jwt.NumericDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.auth.TokenBuilder;
import stroom.auth.TokenBuilderFactory;
import stroom.auth.config.TokenConfig;
import stroom.auth.exceptions.BadRequestException;
import stroom.auth.exceptions.NoSuchUserException;
import stroom.auth.exceptions.UnsupportedFilterException;
import stroom.auth.resources.token.v1.SearchRequest;
import stroom.auth.resources.token.v1.SearchResponse;
import stroom.auth.resources.token.v1.Token;
import stroom.auth.resources.user.v1.User;
import stroom.db.auth.tables.Users;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static stroom.db.auth.Tables.TOKENS;
import static stroom.db.auth.Tables.TOKEN_TYPES;
import static stroom.db.auth.Tables.USERS;

@Singleton
public class TokenDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenDao.class);

    @Inject
    private Configuration jooqConfig;

    @Inject
    private TokenBuilderFactory tokenBuilderFactory;

    private DSLContext database = null;

    @Inject
    private void init() {
        database = DSL.using(this.jooqConfig);
    }

    public SearchResponse searchTokens(SearchRequest searchRequest) {
        // Create some vars to allow the rest of this method to be more succinct.
        int page = searchRequest.getPage();
        int limit = searchRequest.getLimit();
        String orderBy = searchRequest.getOrderBy();
        String orderDirection = searchRequest.getOrderDirection();
        Map<String, String> filters = searchRequest.getFilters();

        // We need these aliased tables because we're joining tokens to users twice.
        Users issueingUsers = USERS.as("issueingUsers");
        Users tokenOwnerUsers = USERS.as("tokenOwnerUsers");
        Users updatingUsers = USERS.as("updatingUsers");

        // Use a default if there's no order direction specified in the request
        if(orderDirection == null){
            orderDirection = "asc";
        }

        Field userEmail = tokenOwnerUsers.EMAIL.as("userEmail");
        // Special cases
        Optional<SortField> orderByField;
        if (orderBy != null && orderBy.equals("userEmail")) {
            // Why is this a special case? Because the property on the target table is 'email' but the param is 'user_email'
            // 'user_email' is a clearer param
            if (orderDirection.equals("asc")) {
                orderByField = Optional.of(userEmail.asc());
            } else {
                orderByField = Optional.of(userEmail.desc());
            }
        } else {
            orderByField = TokenDao.getOrderBy(orderBy, orderDirection);
            if (!orderByField.isPresent()) {
                throw new BadRequestException("Invalid orderBy: " + orderBy);
            }
        }

        Optional<List<Condition>> conditions;
        conditions = getConditions(filters, issueingUsers, tokenOwnerUsers, updatingUsers);

        int offset = limit * page;
        SelectJoinStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>> selectFrom =
                TokenDao.getSelectFrom(database, issueingUsers, tokenOwnerUsers, updatingUsers, userEmail);

        Result<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>> results =
                selectFrom
                        .where(conditions.get())
                        .orderBy(orderByField.get())
                        .limit(limit)
                        .offset(offset)
                        .fetch();
        List<Token> tokens = results.into(Token.class);

        // Finally we need to get the number of tokens so we can calculate the total number of pages
        SelectSelectStep<Record1<Integer>> selectCount =
                database.selectCount();
        SelectJoinStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>>
                fromCount = TokenDao.getFrom(selectCount, issueingUsers, tokenOwnerUsers, updatingUsers, userEmail);
        int count = fromCount
                .where(conditions.get())
                .fetchOne(0, int.class);
        // We need to round up so we always have enough pages even if there's a remainder.
        int pages = (int) Math.ceil((double) count / limit);

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setTokens(tokens);
        searchResponse.setTotalPages(pages);
        return searchResponse;
    }

    public String createEmailResetToken(String emailAddress) throws NoSuchUserException {
        return createToken(
                Token.TokenType.EMAIL_RESET, "authenticationResourceUser",
                emailAddress,
                true, "Created for password reset")
                .getToken();
    }

    public Token createIdToken(String idToken, String subject, Timestamp expiresOn) {
        Record1<Integer> userRecord = database
                .select(USERS.ID)
                .from(USERS)
                .where(USERS.EMAIL.eq(subject))
                .fetchOne();
        if (userRecord == null) {
            throw new NoSuchUserException("Cannot find user to associate with this token!");
        }
        int recipientUserId = userRecord.get(USERS.ID);

        int tokenTypeId = database
                .select(TOKEN_TYPES.ID)
                .from(TOKEN_TYPES)
                .where(TOKEN_TYPES.TOKEN_TYPE.eq(Token.TokenType.USER.getText().toLowerCase()))
                .fetchOne()
                .get(TOKEN_TYPES.ID);

        Token tokenRecord = database
                .insertInto((Table) TOKENS)
                .set(TOKENS.USER_ID, recipientUserId)
                .set(TOKENS.TOKEN_TYPE_ID, tokenTypeId)
                .set(TOKENS.TOKEN, idToken)
                .set(TOKENS.EXPIRES_ON, expiresOn)
                .set(TOKENS.ISSUED_ON, Instant.now())
                .set(TOKENS.ENABLED, true)
                .set(TOKENS.COMMENTS, "This is an OpenId idToken created by the Authentication Service.")
                .returning(new Field[]{TOKENS.ID})
                .fetchOne()
                .into(Token.class);

        return tokenRecord;
    }

    public String createToken(String recipientUserEmail) throws NoSuchUserException {
        return createToken(
                Token.TokenType.USER,
                "authenticationResource",
                recipientUserEmail,
                true,
                "Created for username/password user")
                .getToken();
    }

    /**
     * Create a token for a specific user.
     */
    public Token createToken(
            Token.TokenType tokenType,
            String issuingUserEmail,
            String recipientUserEmail,
            boolean isEnabled,
            String comment) throws NoSuchUserException {

        Record1<Integer> userRecord = database
                .select(USERS.ID)
                .from(USERS)
                .where(USERS.EMAIL.eq(recipientUserEmail))
                .fetchOne();
        if (userRecord == null) {
            throw new NoSuchUserException("Cannot find user to associate with this API key!");
        }
        int recipientUserId = userRecord.get(USERS.ID);

        TokenBuilder tokenBuilder = tokenBuilderFactory
                .newBuilder(tokenType)
                .subject(recipientUserEmail);
        NumericDate expiresOn = tokenBuilder.expiresOn();
        String idToken = tokenBuilder.build();

        int issuingUserId = database
                .select(USERS.ID)
                .from(USERS)
                .where(USERS.EMAIL.eq(issuingUserEmail))
                .fetchOne()
                .get(USERS.ID);

        int tokenTypeId = database
                .select(TOKEN_TYPES.ID)
                .from(TOKEN_TYPES)
                .where(TOKEN_TYPES.TOKEN_TYPE.eq(tokenType.getText().toLowerCase()))
                .fetchOne()
                .get(TOKEN_TYPES.ID);

        Token tokenRecord = database
                .insertInto((Table) TOKENS)
                .set(TOKENS.USER_ID, recipientUserId)
                .set(TOKENS.TOKEN_TYPE_ID, tokenTypeId)
                .set(TOKENS.TOKEN, idToken)
                .set(TOKENS.EXPIRES_ON, new Timestamp(expiresOn.getValueInMillis()))
                .set(TOKENS.ISSUED_ON, Instant.now())
                .set(TOKENS.ISSUED_BY_USER, issuingUserId)
                .set(TOKENS.ENABLED, isEnabled)
                .set(TOKENS.COMMENTS, comment)
                .returning()
                .fetchOne()
                .into(Token.class);

        return tokenRecord;
    }

    public void deleteAllTokensExceptAdmins() {
        Integer adminUserId = database.select(USERS.ID).from(USERS)
                .where(USERS.EMAIL.eq("admin")).fetchOne().into(Integer.class);

        database
                .deleteFrom(TOKENS)
                .where(TOKENS.USER_ID.ne(adminUserId))
                .execute();
    }

    public void deleteTokenById(int tokenId) {
        database.deleteFrom(TOKENS).where(TOKENS.ID.eq(tokenId)).execute();
    }

    public void deleteTokenByTokenString(String token) {
        database.deleteFrom(TOKENS).where(TOKENS.TOKEN.eq(token)).execute();
    }

    public Optional<Token> readById(int tokenId) {

        // We need these aliased tables because we're joining tokens to users twice.
        Users issueingUsers = USERS.as("issueingUsers");
        Users tokenOwnerUsers = USERS.as("tokenOwnerUsers");
        Users updatingUsers = USERS.as("updatingUsers");

        Field userEmail = tokenOwnerUsers.EMAIL.as("user_email");
        SelectJoinStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>> selectFrom =
                getSelectFrom(database, issueingUsers, tokenOwnerUsers, updatingUsers, userEmail);

        Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer> token =
                selectFrom
                        .where(new Condition[]{TOKENS.ID.eq(tokenId)})
                        .fetchOne();
        if (token == null) {
            return Optional.empty();
        }

        return Optional.of(token.into(Token.class));
    }


    public Optional<Token> readByToken(String token) {
        // We need these aliased tables because we're joining tokens to users twice.
        Users tokenOwnerUsers = USERS.as("tokenOwnerUsers");
        Field userEmail = tokenOwnerUsers.EMAIL.as("user_email");

         Record tokenResult = database.select(
                TOKENS.ID.as("id"),
                TOKENS.ENABLED.as("enabled"),
                TOKENS.EXPIRES_ON.as("expires_on"),
                userEmail,
                TOKENS.ISSUED_ON.as("issued_on"),
                TOKENS.TOKEN.as("token"),
                TOKEN_TYPES.TOKEN_TYPE.as("token_type"),
                TOKENS.UPDATED_ON.as("updated_on"),
                TOKENS.USER_ID.as("user_id"))
        .from(TOKENS
                .join(TOKEN_TYPES)
                .on(TOKENS.TOKEN_TYPE_ID.eq(TOKEN_TYPES.ID))
                .join(tokenOwnerUsers)
                .on(TOKENS.USER_ID.eq(tokenOwnerUsers.ID)))
        .where(new Condition[]{TOKENS.TOKEN.eq(token)})
                 .fetchOne();

         if(tokenResult == null){
             return Optional.empty();
         }
         return Optional.of(tokenResult.into(Token.class));
    }


    public static SelectJoinStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>>
    getSelectFrom(DSLContext database, Users issueingUsers, Users tokenOwnerUsers, Users updatingUsers, Field userEmail) {
        SelectSelectStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>>
                select = getSelect(database, issueingUsers, tokenOwnerUsers, updatingUsers, userEmail);

        SelectJoinStep from = getFrom(select, issueingUsers, tokenOwnerUsers, updatingUsers, userEmail);
        return from;
    }

    public static SelectSelectStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>>
    getSelect(DSLContext database, Users issueingUsers, Users tokenOwnerUsers, Users updatingUsers, Field userEmail) {
        SelectSelectStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>>
                select = database.select(
                TOKENS.ID.as("id"),
                TOKENS.ENABLED.as("enabled"),
                TOKENS.EXPIRES_ON.as("expires_on"),
                userEmail,
                TOKENS.ISSUED_ON.as("issued_on"),
                issueingUsers.EMAIL.as("issued_by_user"),
                TOKENS.TOKEN.as("token"),
                TOKEN_TYPES.TOKEN_TYPE.as("token_type"),
                updatingUsers.EMAIL.as("updated_by_user"),
                TOKENS.UPDATED_ON.as("updated_on"),
                TOKENS.USER_ID.as("user_id"));

        return select;
    }

    public static SelectJoinStep
    getFrom(SelectSelectStep select,
            Users issueingUsers, Users tokenOwnerUsers, Users updatingUsers, Field userEmail) {
        SelectJoinStep<Record11<Integer, Boolean, Timestamp, String, Timestamp, String, String, String, String, Timestamp, Integer>>
                from = select.from(TOKENS
                .join(TOKEN_TYPES)
                .on(TOKENS.TOKEN_TYPE_ID.eq(TOKEN_TYPES.ID))
                .join(issueingUsers)
                .on(TOKENS.ISSUED_BY_USER.eq(issueingUsers.ID))
                .join(tokenOwnerUsers)
                .on(TOKENS.USER_ID.eq(tokenOwnerUsers.ID))
                .join(updatingUsers)
                .on(TOKENS.ISSUED_BY_USER.eq(updatingUsers.ID)));

        return from;
    }


    public static Optional<SortField> getOrderBy(String orderBy, String orderDirection) {
        // We might be ordering by TOKENS or USERS or TOKEN_TYPES - we join and select on all
        SortField orderByField;
        if (orderBy != null) {
            switch(orderBy){
                case "userEmail":
                    orderByField = orderDirection.equals("asc") ? USERS.EMAIL.asc() : USERS.EMAIL.desc();
                    break;
                case "enabled":
                    orderByField = orderDirection.equals("asc") ? TOKENS.ENABLED.asc() : TOKENS.ENABLED.desc();
                    break;
                case "tokenType":
                    orderByField = orderDirection.equals("asc") ? TOKEN_TYPES.TOKEN_TYPE.asc() : TOKEN_TYPES.TOKEN_TYPE.desc();
                    break;
                case "issuedOn":
                default:  orderByField = orderDirection.equals("asc") ? TOKENS.ISSUED_ON.asc() : TOKENS.ISSUED_ON.desc();
            }
        } else {
            // We don't have an orderBy so we'll use the default ordering
            orderByField = TOKENS.ISSUED_ON.desc();
        }
        return Optional.of(orderByField);
    }

    public void enableOrDisableToken(int tokenId, boolean enabled, User updatingUser) {
        Object result = database
                .update(TOKENS)
                .set(TOKENS.ENABLED, enabled)
                .set(TOKENS.UPDATED_ON, Timestamp.from(Instant.now()))
                .set(TOKENS.UPDATED_BY_USER, updatingUser.getId())
                .where(TOKENS.ID.eq((tokenId)))
                .execute();
    }

    /**
     * How do we match on dates? Must match exactly? Must match part of the date? What if the given date is invalid?
     * Is this what a user would want? Maybe they want greater than or less than? This would need additional UI
     * For now we can't sensible implement anything unless we have a better idea of requirements.
     */
    private static Optional<List<Condition>> getConditions(Map<String, String> filters, Users issueingUsers,
                                                           Users tokenOwnerUsers, Users updatingUsers) {
        // We need to set up conditions
        List<Condition> conditions = new ArrayList<>();
        final String unsupportedFilterMessage = "Unsupported filter: ";
        final String unknownFilterMessage = "Unknown filter: ";
        if (filters != null) {
            for (String key : filters.keySet()) {
                Condition condition = null;
                switch (key) {
                    case "enabled":
                        condition = TOKENS.ENABLED.eq(Boolean.valueOf(filters.get(key)));
                        break;
                    case "expiresOn":
                        throw new UnsupportedFilterException(unsupportedFilterMessage + key);
                    case "userEmail":
                        condition = tokenOwnerUsers.EMAIL.contains(filters.get(key));
                        break;
                    case "issuedOn":
                        throw new UnsupportedFilterException(unsupportedFilterMessage + key);
                    case "issuedByUser":
                        condition = issueingUsers.EMAIL.contains(filters.get(key));
                        break;
                    case "token":
                        // It didn't initally make sense that one might want to filter on token, because it's encrypted.
                        // But if someone has a token copy/pasting some or all of it into the search might be the
                        // fastest way to find the token.
                        condition = TOKENS.TOKEN.contains(filters.get(key));
                        break;
                    case "tokenType":
                        condition = TOKEN_TYPES.TOKEN_TYPE.contains(filters.get(key));
                        break;
                    case "updatedByUser":
                        condition = updatingUsers.EMAIL.contains(filters.get(key));
                        break;
                    case "updatedOn":
                        throw new UnsupportedFilterException(unsupportedFilterMessage + key);
                    case "userId":
                        throw new UnsupportedFilterException(unsupportedFilterMessage + key);
                    default:
                        throw new UnsupportedFilterException(unknownFilterMessage + key);
                }

                conditions.add(condition);
            }
        }
        return Optional.of(conditions);
    }

}
