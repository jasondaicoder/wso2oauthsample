/**
 * TokenValidatorWSO2.java
 *
 * @author jason
 * @version 1.0
 * @since 14Nov.,2016
 */
package com.dimensiondata.wso2oauthsample.wso2;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dimensiondata.wso2oauthsample.oauth2.AuthenticationException;
import com.dimensiondata.wso2oauthsample.oauth2.TokenValidationResponse;
import com.dimensiondata.wso2oauthsample.oauth2.TokenValidator;

/**
 * Token validator for WSO2.
 * @author jason
 *
 */
@Component
public class TokenValidatorWSO2 implements TokenValidator {
	
	private static final int TIMEOUT_IN_MILLIS = 15 * 60 * 1000;
	
	private static final Log log = LogFactory.getLog(TokenValidatorWSO2.class);

	@Value("${wso2isOAuthUserInfo}")
	private String wso2isOAuthUserInfo;
	
	/* (non-Javadoc)
	 * @see com.dimensiondata.wso2oauthsample.oauth2.TokenValidator#Validate(java.lang.String)
	 */
	public TokenValidationResponse Validate(String accessToken) {
		TokenValidationResponse tokenValidationResponse = new TokenValidationResponse();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(TIMEOUT_IN_MILLIS)
					.build();
			HttpGet httpGet = new HttpGet(wso2isOAuthUserInfo + "?schema=openid");
			log.debug(String.format("Getting usr info from %s", httpGet.getURI()));
			httpGet.setConfig(requestConfig);
			httpGet.addHeader("Authorization", "Bearer " + accessToken);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				String content = EntityUtils.toString(entity);
				if (response.getStatusLine().getStatusCode() != 200) {
					log.error("Failed to retreive user info. \n " + content);
					throw new AuthenticationException("Failed to retreive user info, status code " + response.getStatusLine().getStatusCode());
				}
				JSONObject result = new JSONObject(content);
				Object idObject = result.get("sub");
				if (idObject != null) {
					tokenValidationResponse.setAuthorizedUserIdentifier(idObject.toString());
				} else {
					throw new AuthenticationException("No sub found in claims.");
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			throw new AuthenticationException(e);
		} catch (IOException e) {
			throw new AuthenticationException(e);
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				throw new AuthenticationException(e);
			}
		}
		return tokenValidationResponse;
    }
}
