/**
 * TokenValidatorWSO2.java
 *
 * @author jason
 * @version 1.0
 * @since 14Nov.,2016
 */
package com.dimensiondata.wso2oauthsample.wso2;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wso2.carbon.identity.oauth2.stub.OAuth2TokenValidationServiceStub;
import org.wso2.carbon.identity.oauth2.stub.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.identity.oauth2.stub.dto.OAuth2TokenValidationRequestDTO_OAuth2AccessToken;
import org.wso2.carbon.identity.oauth2.stub.dto.OAuth2TokenValidationResponseDTO;
import org.wso2.carbon.utils.CarbonUtils;

import com.dimensiondata.wso2oauthsample.oauth2.TokenValidationResponse;
import com.dimensiondata.wso2oauthsample.oauth2.TokenValidator;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

/**
 *
 * @author jason
 *
 */
@Component
public class TokenValidatorWSO2 implements TokenValidator {
	
	private static final int TIMEOUT_IN_MILLIS = 15 * 60 * 1000;
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TokenValidatorWSO2.class);
	
	@Value("${tokenValidationServiceUrl}")
	private String tokenValidationServiceUrl;
	
	@Value("${adminUserName}")
	private String adminUserName;
	
	@Value("${adminPassword}")
	private String adminPassword;
	
	/* (non-Javadoc)
	 * @see com.dimensiondata.wso2oauthsample.oauth2.TokenValidator#Validate(java.lang.String)
	 */
	public TokenValidationResponse Validate(String accessToken) {
		OAuth2TokenValidationServiceStub stub;
		try {
			stub = initializeValidationService();
	        OAuth2TokenValidationRequestDTO  oauthRequest;
	        TokenValidationResponse validationResponse = null;
	        OAuth2TokenValidationRequestDTO_OAuth2AccessToken oAuth2AccessToken;

            oauthRequest = new OAuth2TokenValidationRequestDTO();
            oAuth2AccessToken = new OAuth2TokenValidationRequestDTO_OAuth2AccessToken();
            oAuth2AccessToken.setIdentifier(accessToken);
            oAuth2AccessToken.setTokenType("bearer");
            oauthRequest.setAccessToken(oAuth2AccessToken);
            OAuth2TokenValidationResponseDTO response = stub.validate(oauthRequest);

            if(!response.getValid()) {
                throw new RuntimeException("Token is invalid.");
            }

            String jwtToken = response.getAuthorizationContextToken().getTokenString();
            
//            if (!isJWTTokenSignatureValid(jwtToken))
//            	throw new RuntimeException("JWT token is invalid.");
            
            validationResponse = new TokenValidationResponse();
            validationResponse.setAuthorizedUserIdentifier(response.getAuthorizedUser());
            validationResponse.setJwtToken(jwtToken);
            validationResponse.setScope(new LinkedHashSet<String>(Arrays.asList(response.getScope())));
            validationResponse.setValid(response.getValid());

	        return validationResponse;	
	        
		} catch (RuntimeException e) {
			throw e; 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

    }

    private OAuth2TokenValidationServiceStub initializeValidationService() throws AxisFault  {
    	OAuth2TokenValidationServiceStub  stub = new OAuth2TokenValidationServiceStub(null, tokenValidationServiceUrl);
        ServiceClient client = stub._getServiceClient();
        CarbonUtils.setBasicAccessSecurityHeaders(adminUserName, adminPassword, true, client);
        Options options = client.getOptions();
        options.setTimeOutInMilliSeconds(TIMEOUT_IN_MILLIS);
        options.setProperty(HTTPConstants.SO_TIMEOUT, TIMEOUT_IN_MILLIS);
        options.setProperty(HTTPConstants.CONNECTION_TIMEOUT, TIMEOUT_IN_MILLIS);
        options.setCallTransportCleanup(true);
        options.setManageSession(true);
        return stub;
    }	
    
    private boolean isJWTTokenSignatureValid(String jwtToken) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, ParseException, JOSEException {
    	
        RSAPublicKey publicKey = null;
        InputStream file = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("/wso2carbon.jks");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(file, "wso2carbon".toCharArray());
 
        String alias = "wso2carbon";
 
        // Get certificate of public key
        Certificate cert = keystore.getCertificate(alias);
        // Get public key
        publicKey = (RSAPublicKey) cert.getPublicKey();
 
        SignedJWT signedJWT = SignedJWT.parse(jwtToken);
 
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
 
        return signedJWT.verify(verifier);
    }
}
