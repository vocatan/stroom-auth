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


package stroom.auth.service.api.model;

import java.util.Objects;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A request to create a token.
 */
@ApiModel(description = "A request to create a token.")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2017-11-10T14:37:07.635Z")
public class CreateTokenRequest {
  @SerializedName("userEmail")
  private String userEmail = null;

  @SerializedName("tokenType")
  private String tokenType = null;

  @SerializedName("comments")
  private String comments = null;

  @SerializedName("enabled")
  private Boolean enabled = null;

  /**
   * Gets or Sets parsedTokenType
   */
  public enum ParsedTokenTypeEnum {
    @SerializedName("USER")
    USER("USER"),
    
    @SerializedName("API")
    API("API"),
    
    @SerializedName("EMAIL_RESET")
    EMAIL_RESET("EMAIL_RESET");

    private String value;

    ParsedTokenTypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
  }

  @SerializedName("parsedTokenType")
  private ParsedTokenTypeEnum parsedTokenType = null;

  public CreateTokenRequest userEmail(String userEmail) {
    this.userEmail = userEmail;
    return this;
  }

   /**
   * The email of the user whom the token is for.
   * @return userEmail
  **/
  @ApiModelProperty(example = "null", required = true, value = "The email of the user whom the token is for.")
  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public CreateTokenRequest tokenType(String tokenType) {
    this.tokenType = tokenType;
    return this;
  }

   /**
   * The type of token to create: e.g. user, api, or email_reset.
   * @return tokenType
  **/
  @ApiModelProperty(example = "null", required = true, value = "The type of token to create: e.g. user, api, or email_reset.")
  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public CreateTokenRequest comments(String comments) {
    this.comments = comments;
    return this;
  }

   /**
   * Comments about the token.
   * @return comments
  **/
  @ApiModelProperty(example = "null", value = "Comments about the token.")
  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public CreateTokenRequest enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

   /**
   * Whether or not the new token should be enabled.
   * @return enabled
  **/
  @ApiModelProperty(example = "null", value = "Whether or not the new token should be enabled.")
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public CreateTokenRequest parsedTokenType(ParsedTokenTypeEnum parsedTokenType) {
    this.parsedTokenType = parsedTokenType;
    return this;
  }

   /**
   * Get parsedTokenType
   * @return parsedTokenType
  **/
  @ApiModelProperty(example = "null", value = "")
  public ParsedTokenTypeEnum getParsedTokenType() {
    return parsedTokenType;
  }

  public void setParsedTokenType(ParsedTokenTypeEnum parsedTokenType) {
    this.parsedTokenType = parsedTokenType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CreateTokenRequest createTokenRequest = (CreateTokenRequest) o;
    return Objects.equals(this.userEmail, createTokenRequest.userEmail) &&
        Objects.equals(this.tokenType, createTokenRequest.tokenType) &&
        Objects.equals(this.comments, createTokenRequest.comments) &&
        Objects.equals(this.enabled, createTokenRequest.enabled) &&
        Objects.equals(this.parsedTokenType, createTokenRequest.parsedTokenType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userEmail, tokenType, comments, enabled, parsedTokenType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreateTokenRequest {\n");
    
    sb.append("    userEmail: ").append(toIndentedString(userEmail)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
    sb.append("    comments: ").append(toIndentedString(comments)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    parsedTokenType: ").append(toIndentedString(parsedTokenType)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
}

