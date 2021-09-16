package com.example.oie.auth.model;

import java.util.List;

import com.okta.idx.sdk.api.client.Authenticator;
import com.okta.idx.sdk.api.response.TokenResponse;

public class RegistrationResponse {
	
	private String status;
	
	private List<Authenticator> authenticators;
	
	private TokenResponse tokens;
	
	private List<String> errors;
	
	private String sessionId;
	
	private String profilePhone;
	
	public String getProfilePhone() {
		return profilePhone;
	}

	public void setProfilePhone(String profilePhone) {
		this.profilePhone = profilePhone;
	}

	private Boolean canSkip;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Authenticator> getAuthenticators() {
		return authenticators;
	}

	public void setAuthenticators(List<Authenticator> authenticators) {
		this.authenticators = authenticators;
	}

	public TokenResponse getTokens() {
		return tokens;
	}

	public void setTokens(TokenResponse tokens) {
		this.tokens = tokens;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public Boolean getCanSkip() {
		return canSkip;
	}

	public void setCanSkip(Boolean canSkip) {
		this.canSkip = canSkip;
	}
	
	

}
