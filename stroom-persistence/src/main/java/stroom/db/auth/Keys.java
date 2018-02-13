/*
 * This file is generated by jOOQ.
*/
package stroom.db.auth;


import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import stroom.db.auth.tables.JsonWebKey;
import stroom.db.auth.tables.TokenTypes;
import stroom.db.auth.tables.Tokens;
import stroom.db.auth.tables.Users;
import stroom.db.auth.tables.records.JsonWebKeyRecord;
import stroom.db.auth.tables.records.TokenTypesRecord;
import stroom.db.auth.tables.records.TokensRecord;
import stroom.db.auth.tables.records.UsersRecord;


/**
 * A class modelling foreign key relationships between tables of the <code>auth</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<JsonWebKeyRecord, Integer> IDENTITY_JSON_WEB_KEY = Identities0.IDENTITY_JSON_WEB_KEY;
    public static final Identity<TokensRecord, Integer> IDENTITY_TOKENS = Identities0.IDENTITY_TOKENS;
    public static final Identity<TokenTypesRecord, Integer> IDENTITY_TOKEN_TYPES = Identities0.IDENTITY_TOKEN_TYPES;
    public static final Identity<UsersRecord, Integer> IDENTITY_USERS = Identities0.IDENTITY_USERS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<JsonWebKeyRecord> KEY_JSON_WEB_KEY_PRIMARY = UniqueKeys0.KEY_JSON_WEB_KEY_PRIMARY;
    public static final UniqueKey<JsonWebKeyRecord> KEY_JSON_WEB_KEY_KEYID = UniqueKeys0.KEY_JSON_WEB_KEY_KEYID;
    public static final UniqueKey<JsonWebKeyRecord> KEY_JSON_WEB_KEY_JSON = UniqueKeys0.KEY_JSON_WEB_KEY_JSON;
    public static final UniqueKey<TokensRecord> KEY_TOKENS_PRIMARY = UniqueKeys0.KEY_TOKENS_PRIMARY;
    public static final UniqueKey<TokensRecord> KEY_TOKENS_ID = UniqueKeys0.KEY_TOKENS_ID;
    public static final UniqueKey<TokenTypesRecord> KEY_TOKEN_TYPES_PRIMARY = UniqueKeys0.KEY_TOKEN_TYPES_PRIMARY;
    public static final UniqueKey<TokenTypesRecord> KEY_TOKEN_TYPES_ID = UniqueKeys0.KEY_TOKEN_TYPES_ID;
    public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = UniqueKeys0.KEY_USERS_PRIMARY;
    public static final UniqueKey<UsersRecord> KEY_USERS_EMAIL = UniqueKeys0.KEY_USERS_EMAIL;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<TokensRecord, UsersRecord> FK_ISSUED_TO = ForeignKeys0.FK_ISSUED_TO;
    public static final ForeignKey<TokensRecord, TokenTypesRecord> FK_TOKEN_TYPE_ID = ForeignKeys0.FK_TOKEN_TYPE_ID;
    public static final ForeignKey<TokensRecord, UsersRecord> FK_ISSUED_BY_USER = ForeignKeys0.FK_ISSUED_BY_USER;
    public static final ForeignKey<TokensRecord, UsersRecord> FK_UPDATED_BY_USER = ForeignKeys0.FK_UPDATED_BY_USER;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 extends AbstractKeys {
        public static Identity<JsonWebKeyRecord, Integer> IDENTITY_JSON_WEB_KEY = createIdentity(JsonWebKey.JSON_WEB_KEY, JsonWebKey.JSON_WEB_KEY.ID);
        public static Identity<TokensRecord, Integer> IDENTITY_TOKENS = createIdentity(Tokens.TOKENS, Tokens.TOKENS.ID);
        public static Identity<TokenTypesRecord, Integer> IDENTITY_TOKEN_TYPES = createIdentity(TokenTypes.TOKEN_TYPES, TokenTypes.TOKEN_TYPES.ID);
        public static Identity<UsersRecord, Integer> IDENTITY_USERS = createIdentity(Users.USERS, Users.USERS.ID);
    }

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<JsonWebKeyRecord> KEY_JSON_WEB_KEY_PRIMARY = createUniqueKey(JsonWebKey.JSON_WEB_KEY, "KEY_json_web_key_PRIMARY", JsonWebKey.JSON_WEB_KEY.ID);
        public static final UniqueKey<JsonWebKeyRecord> KEY_JSON_WEB_KEY_KEYID = createUniqueKey(JsonWebKey.JSON_WEB_KEY, "KEY_json_web_key_keyId", JsonWebKey.JSON_WEB_KEY.KEYID);
        public static final UniqueKey<JsonWebKeyRecord> KEY_JSON_WEB_KEY_JSON = createUniqueKey(JsonWebKey.JSON_WEB_KEY, "KEY_json_web_key_json", JsonWebKey.JSON_WEB_KEY.JSON);
        public static final UniqueKey<TokensRecord> KEY_TOKENS_PRIMARY = createUniqueKey(Tokens.TOKENS, "KEY_tokens_PRIMARY", Tokens.TOKENS.ID);
        public static final UniqueKey<TokensRecord> KEY_TOKENS_ID = createUniqueKey(Tokens.TOKENS, "KEY_tokens_id", Tokens.TOKENS.ID);
        public static final UniqueKey<TokenTypesRecord> KEY_TOKEN_TYPES_PRIMARY = createUniqueKey(TokenTypes.TOKEN_TYPES, "KEY_token_types_PRIMARY", TokenTypes.TOKEN_TYPES.ID);
        public static final UniqueKey<TokenTypesRecord> KEY_TOKEN_TYPES_ID = createUniqueKey(TokenTypes.TOKEN_TYPES, "KEY_token_types_id", TokenTypes.TOKEN_TYPES.ID);
        public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = createUniqueKey(Users.USERS, "KEY_users_PRIMARY", Users.USERS.ID);
        public static final UniqueKey<UsersRecord> KEY_USERS_EMAIL = createUniqueKey(Users.USERS, "KEY_users_email", Users.USERS.EMAIL);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<TokensRecord, UsersRecord> FK_ISSUED_TO = createForeignKey(stroom.db.auth.Keys.KEY_USERS_PRIMARY, Tokens.TOKENS, "fk_issued_to", Tokens.TOKENS.USER_ID);
        public static final ForeignKey<TokensRecord, TokenTypesRecord> FK_TOKEN_TYPE_ID = createForeignKey(stroom.db.auth.Keys.KEY_TOKEN_TYPES_PRIMARY, Tokens.TOKENS, "fk_token_type_id", Tokens.TOKENS.TOKEN_TYPE_ID);
        public static final ForeignKey<TokensRecord, UsersRecord> FK_ISSUED_BY_USER = createForeignKey(stroom.db.auth.Keys.KEY_USERS_PRIMARY, Tokens.TOKENS, "fk_issued_by_user", Tokens.TOKENS.ISSUED_BY_USER);
        public static final ForeignKey<TokensRecord, UsersRecord> FK_UPDATED_BY_USER = createForeignKey(stroom.db.auth.Keys.KEY_USERS_PRIMARY, Tokens.TOKENS, "fk_updated_by_user", Tokens.TOKENS.UPDATED_BY_USER);
    }
}
