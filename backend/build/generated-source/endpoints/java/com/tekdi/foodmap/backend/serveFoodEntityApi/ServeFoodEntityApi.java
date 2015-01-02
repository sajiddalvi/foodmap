/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-11-17 18:43:33 UTC)
 * on 2015-01-01 at 05:12:53 UTC 
 * Modify at your own risk.
 */

package com.tekdi.foodmap.backend.serveFoodEntityApi;

/**
 * Service definition for ServeFoodEntityApi (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link ServeFoodEntityApiRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class ServeFoodEntityApi extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.19.0 of the serveFoodEntityApi library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://myApplicationId.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "serveFoodEntityApi/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public ServeFoodEntityApi(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  ServeFoodEntityApi(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "get".
   *
   * This request holds the parameters needed by the serveFoodEntityApi server.  After setting any
   * optional parameters, call the {@link Get#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   */
  public Get get(java.lang.Long id) throws java.io.IOException {
    Get result = new Get(id);
    initialize(result);
    return result;
  }

  public class Get extends ServeFoodEntityApiRequest<com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity> {

    private static final String REST_PATH = "serveFoodEntity/{id}";

    /**
     * Create a request for the method "get".
     *
     * This request holds the parameters needed by the the serveFoodEntityApi server.  After setting
     * any optional parameters, call the {@link Get#execute()} method to invoke the remote operation.
     * <p> {@link
     * Get#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must be
     * called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected Get(java.lang.Long id) {
      super(ServeFoodEntityApi.this, "GET", REST_PATH, null, com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public Get setAlt(java.lang.String alt) {
      return (Get) super.setAlt(alt);
    }

    @Override
    public Get setFields(java.lang.String fields) {
      return (Get) super.setFields(fields);
    }

    @Override
    public Get setKey(java.lang.String key) {
      return (Get) super.setKey(key);
    }

    @Override
    public Get setOauthToken(java.lang.String oauthToken) {
      return (Get) super.setOauthToken(oauthToken);
    }

    @Override
    public Get setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (Get) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public Get setQuotaUser(java.lang.String quotaUser) {
      return (Get) super.setQuotaUser(quotaUser);
    }

    @Override
    public Get setUserIp(java.lang.String userIp) {
      return (Get) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public Get setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public Get set(String parameterName, Object value) {
      return (Get) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertServeFoodEntity".
   *
   * This request holds the parameters needed by the serveFoodEntityApi server.  After setting any
   * optional parameters, call the {@link InsertServeFoodEntity#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity}
   * @return the request
   */
  public InsertServeFoodEntity insertServeFoodEntity(com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity content) throws java.io.IOException {
    InsertServeFoodEntity result = new InsertServeFoodEntity(content);
    initialize(result);
    return result;
  }

  public class InsertServeFoodEntity extends ServeFoodEntityApiRequest<com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity> {

    private static final String REST_PATH = "servefoodentity";

    /**
     * Create a request for the method "insertServeFoodEntity".
     *
     * This request holds the parameters needed by the the serveFoodEntityApi server.  After setting
     * any optional parameters, call the {@link InsertServeFoodEntity#execute()} method to invoke the
     * remote operation. <p> {@link InsertServeFoodEntity#initialize(com.google.api.client.googleapis.
     * services.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param content the {@link com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity}
     * @since 1.13
     */
    protected InsertServeFoodEntity(com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity content) {
      super(ServeFoodEntityApi.this, "POST", REST_PATH, content, com.tekdi.foodmap.backend.serveFoodEntityApi.model.ServeFoodEntity.class);
    }

    @Override
    public InsertServeFoodEntity setAlt(java.lang.String alt) {
      return (InsertServeFoodEntity) super.setAlt(alt);
    }

    @Override
    public InsertServeFoodEntity setFields(java.lang.String fields) {
      return (InsertServeFoodEntity) super.setFields(fields);
    }

    @Override
    public InsertServeFoodEntity setKey(java.lang.String key) {
      return (InsertServeFoodEntity) super.setKey(key);
    }

    @Override
    public InsertServeFoodEntity setOauthToken(java.lang.String oauthToken) {
      return (InsertServeFoodEntity) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertServeFoodEntity setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertServeFoodEntity) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertServeFoodEntity setQuotaUser(java.lang.String quotaUser) {
      return (InsertServeFoodEntity) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertServeFoodEntity setUserIp(java.lang.String userIp) {
      return (InsertServeFoodEntity) super.setUserIp(userIp);
    }

    @Override
    public InsertServeFoodEntity set(String parameterName, Object value) {
      return (InsertServeFoodEntity) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listServers".
   *
   * This request holds the parameters needed by the serveFoodEntityApi server.  After setting any
   * optional parameters, call the {@link ListServers#execute()} method to invoke the remote
   * operation.
   *
   * @return the request
   */
  public ListServers listServers() throws java.io.IOException {
    ListServers result = new ListServers();
    initialize(result);
    return result;
  }

  public class ListServers extends ServeFoodEntityApiRequest<com.tekdi.foodmap.backend.serveFoodEntityApi.model.CollectionResponseServeFoodEntity> {

    private static final String REST_PATH = "servefoodentity";

    /**
     * Create a request for the method "listServers".
     *
     * This request holds the parameters needed by the the serveFoodEntityApi server.  After setting
     * any optional parameters, call the {@link ListServers#execute()} method to invoke the remote
     * operation. <p> {@link
     * ListServers#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ListServers() {
      super(ServeFoodEntityApi.this, "GET", REST_PATH, null, com.tekdi.foodmap.backend.serveFoodEntityApi.model.CollectionResponseServeFoodEntity.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ListServers setAlt(java.lang.String alt) {
      return (ListServers) super.setAlt(alt);
    }

    @Override
    public ListServers setFields(java.lang.String fields) {
      return (ListServers) super.setFields(fields);
    }

    @Override
    public ListServers setKey(java.lang.String key) {
      return (ListServers) super.setKey(key);
    }

    @Override
    public ListServers setOauthToken(java.lang.String oauthToken) {
      return (ListServers) super.setOauthToken(oauthToken);
    }

    @Override
    public ListServers setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListServers) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListServers setQuotaUser(java.lang.String quotaUser) {
      return (ListServers) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListServers setUserIp(java.lang.String userIp) {
      return (ListServers) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Integer count;

    /**

     */
    public java.lang.Integer getCount() {
      return count;
    }

    public ListServers setCount(java.lang.Integer count) {
      this.count = count;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListServers setCursor(java.lang.String cursor) {
      this.cursor = cursor;
      return this;
    }

    @Override
    public ListServers set(String parameterName, Object value) {
      return (ListServers) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link ServeFoodEntityApi}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link ServeFoodEntityApi}. */
    @Override
    public ServeFoodEntityApi build() {
      return new ServeFoodEntityApi(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link ServeFoodEntityApiRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setServeFoodEntityApiRequestInitializer(
        ServeFoodEntityApiRequestInitializer servefoodentityapiRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(servefoodentityapiRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
