package com.example.oie.auth;

import javax.servlet.http.HttpSession;

import com.example.oie.auth.model.AcmeUserProfile;
import com.example.oie.auth.model.RegistrationResponse;
import com.okta.idx.sdk.api.response.TokenResponse;

public interface AuthService {
	
	public RegistrationResponse login(String username, String password, HttpSession s);
	
	public RegistrationResponse signUp(HttpSession session, AcmeUserProfile profile);
	
	public RegistrationResponse answerChallenge(String answer, HttpSession s);
	
	public RegistrationResponse enrollAuthenticator(String authenticator, HttpSession session);
	
	public RegistrationResponse selectAuthenticator(String authenticator, HttpSession session);
	
	public String getSessionId();
	
	public RegistrationResponse resetPassword(String email, HttpSession session);
	
	public RegistrationResponse resendCode(HttpSession s);
	
	public void logout(TokenResponse tokenResponse);
	
	public RegistrationResponse skipAuthenticators(HttpSession s);
	
	public RegistrationResponse registerPhone(HttpSession s, String phoneNumber);
}
