# DefaultApi

All URIs are relative to *http://localhost:8080/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create**](DefaultApi.md#create) | **POST** /token/v1 | Submit a search request for tokens
[**getIdTokenWithGet**](DefaultApi.md#getIdTokenWithGet) | **GET** /authentication/v1/idToken | Convert a previously provided access code into an ID token
[**getIdTokenWithPost**](DefaultApi.md#getIdTokenWithPost) | **POST** /authentication/v1/idToken | Convert a previously provided access code into an ID token
[**logout**](DefaultApi.md#logout) | **GET** /session/v1/logout/{sessionId} | Log a user out of their session
[**search**](DefaultApi.md#search) | **POST** /token/v1/search | Submit a search request for tokens


<a name="create"></a>
# **create**
> String create(body)

Submit a search request for tokens



### Example
```java
// Import classes:
//import stroom.auth.service.ApiException;
//import stroom.auth.service.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
CreateTokenRequest body = new CreateTokenRequest(); // CreateTokenRequest | CreateTokenRequest
try {
    String result = apiInstance.create(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#create");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**CreateTokenRequest**](CreateTokenRequest.md)| CreateTokenRequest | [optional]

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="getIdTokenWithGet"></a>
# **getIdTokenWithGet**
> String getIdTokenWithGet(accessCode, clientId)

Convert a previously provided access code into an ID token



### Example
```java
// Import classes:
//import stroom.auth.service.ApiException;
//import stroom.auth.service.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String accessCode = "accessCode_example"; // String | 
String clientId = "clientId_example"; // String | 
try {
    String result = apiInstance.getIdTokenWithGet(accessCode, clientId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getIdTokenWithGet");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **accessCode** | **String**|  |
 **clientId** | **String**|  |

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getIdTokenWithPost"></a>
# **getIdTokenWithPost**
> String getIdTokenWithPost(body)

Convert a previously provided access code into an ID token



### Example
```java
// Import classes:
//import stroom.auth.service.ApiException;
//import stroom.auth.service.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
IdTokenRequest body = new IdTokenRequest(); // IdTokenRequest | idTokenRequest
try {
    String result = apiInstance.getIdTokenWithPost(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#getIdTokenWithPost");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**IdTokenRequest**](IdTokenRequest.md)| idTokenRequest | [optional]

### Return type

**String**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="logout"></a>
# **logout**
> Response logout(sessionId)

Log a user out of their session



### Example
```java
// Import classes:
//import stroom.auth.service.ApiException;
//import stroom.auth.service.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
String sessionId = "sessionId_example"; // String | 
try {
    Response result = apiInstance.logout(sessionId);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#logout");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sessionId** | **String**|  |

### Return type

[**Response**](Response.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="search"></a>
# **search**
> SearchResponse search(body)

Submit a search request for tokens



### Example
```java
// Import classes:
//import stroom.auth.service.ApiException;
//import stroom.auth.service.api.DefaultApi;


DefaultApi apiInstance = new DefaultApi();
SearchRequest body = new SearchRequest(); // SearchRequest | SearchRequest
try {
    SearchResponse result = apiInstance.search(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling DefaultApi#search");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**SearchRequest**](SearchRequest.md)| SearchRequest | [optional]

### Return type

[**SearchResponse**](SearchResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json
