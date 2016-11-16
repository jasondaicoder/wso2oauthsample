/**
 * TokenValidationResponse.java
 *
 * @author jason
 * @version 1.0
 * @since 11Nov.,2016
 */
package com.dimensiondata.wso2oauthsample.oauth2;

import java.util.Set;

/**
 * @author jason
 *
 */
public class TokenValidationResponse {
    private String jwtToken;
    private boolean valid;
    private Set<String> scope;
    private String authorizedUserIdentifier;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public String getAuthorizedUserIdentifier() {
        return authorizedUserIdentifier;
    }

    public void setAuthorizedUserIdentifier(String authorizedUserIdentifier) {
        this.authorizedUserIdentifier = authorizedUserIdentifier;
    }
}
