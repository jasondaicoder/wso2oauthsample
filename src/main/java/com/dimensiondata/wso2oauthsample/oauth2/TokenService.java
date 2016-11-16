package com.dimensiondata.wso2oauthsample.oauth2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

public class TokenService implements ResourceServerTokenServices {
	
	@Autowired
	private TokenValidator tokenValidator;
	
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		
        TokenValidationResponse validationResponse = tokenValidator.Validate(accessToken);
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        OAuth2Request oAuth2Request = new OAuth2Request(null, null, authorities, true, validationResponse.getScope(), null, null, null,null);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(validationResponse.getAuthorizedUserIdentifier(), null, authorities);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
        return oAuth2Authentication;
    }

	public OAuth2AccessToken readAccessToken(String accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

}
