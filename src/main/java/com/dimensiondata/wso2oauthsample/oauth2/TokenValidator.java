/**
 * TokenValidator.java
 *
 * @author jason
 * @version 1.0
 * @since 14Nov.,2016
 */
package com.dimensiondata.wso2oauthsample.oauth2;

/**
 *
 * @author jason
 *
 */
public interface TokenValidator {

	TokenValidationResponse Validate(String accessToken);
}
