package com.example.oie.auth;

import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.oie.auth.model.AcmeUserProfile;
import com.example.oie.auth.model.RegistrationResponse;
import com.okta.idx.sdk.api.client.Authenticator;
import com.okta.idx.sdk.api.client.IDXAuthenticationWrapper;
import com.okta.idx.sdk.api.client.ProceedContext;
import com.okta.idx.sdk.api.model.AuthenticationOptions;
import com.okta.idx.sdk.api.model.Authenticators;
import com.okta.idx.sdk.api.model.TokenType;
import com.okta.idx.sdk.api.model.UserProfile;
import com.okta.idx.sdk.api.model.VerifyAuthenticatorOptions;
import com.okta.idx.sdk.api.response.AuthenticationResponse;
import com.okta.idx.sdk.api.response.TokenResponse;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
    private IDXAuthenticationWrapper idxAuthenticationWrapper;

	/**
	 * Login to app. If password is setup as only factor in app sign in , users will be authenticated. 
	 * If there are any additional factors required
	 *  , users will be asked to select the next factor
	 */
	@Override
	public RegistrationResponse login(String username, String password, HttpSession s) {
		AuthenticationResponse beginResponse = begin();
		 AuthenticationResponse authenticationResponse =
	                idxAuthenticationWrapper.authenticate(new AuthenticationOptions(username, password), beginResponse.getProceedContext());

		 
		 
		 return generateResponse(authenticationResponse, s);
	}
	
	/**
	 * Self service registration. Based on MFA enrollment policy, users will be prompted to enroll
	 */
	@Override
	public RegistrationResponse signUp(HttpSession session, AcmeUserProfile profile) {
		AuthenticationResponse newUserRegistrationResponse = idxAuthenticationWrapper.fetchSignUpFormValues(begin().getProceedContext());
		UserProfile up = new UserProfile();
		up.addAttribute("firstName", profile.getFirstName());
		up.addAttribute("lastName", profile.getLastName());
		up.addAttribute("email", profile.getEmail());
		up.addAttribute("login", profile.getEmail());
		up.addAttribute("primaryPhone", profile.getPhone());
		
		ProceedContext proceedContext = newUserRegistrationResponse.getProceedContext();
		AuthenticationResponse authenticationResponse = 
				idxAuthenticationWrapper.register(proceedContext, up);
		
		
		return generateResponse(authenticationResponse, session);
		
	}
	
	/**
	 *  Verify an MFA during login or registration or recover password flow
	 */
	@Override
	public RegistrationResponse answerChallenge(String answer, HttpSession s) {
		VerifyAuthenticatorOptions verifyAuthenticatorOptions = 
				new VerifyAuthenticatorOptions(answer);
        AuthenticationResponse authenticationResponse =
                idxAuthenticationWrapper.verifyAuthenticator(
                		(ProceedContext) s.getAttribute("proceedContext"), 
                		verifyAuthenticatorOptions);
		return generateResponse(authenticationResponse, s);
	}

	@Override
	public String getSessionId() {
		return "a";
	}

	/**
	 *  Select a factor to enroll in during registration
	 */
	@Override
	public RegistrationResponse enrollAuthenticator(String authenticator, HttpSession s) {
		// TODO Auto-generated method stub
		
		authenticator = URLDecoder.decode(authenticator);
		ProceedContext pc = (ProceedContext) s.getAttribute("proceedContext");
		List<Authenticator> as = (List<Authenticator>) s.getAttribute("authenticators");
		
		AuthenticationResponse authenticationResponse = null;
		
		Authenticator foundAuthenticator = null;
		
		for(Authenticator a : as) {
			if(authenticator.equalsIgnoreCase(a.getLabel())) {
				foundAuthenticator = a;
				if(foundAuthenticator != null) {
					if(foundAuthenticator.getFactors().size() != 0) {		
						authenticationResponse = idxAuthenticationWrapper.enrollAuthenticator(pc, 
								foundAuthenticator.getFactors().get(0));
					}
					
					
				}
			}
		}
		return generateResponse(authenticationResponse, s);
		
	}

	/**
	 * Select a factor to authenticate with during sign in process
	 */
	@Override
	public RegistrationResponse selectAuthenticator(String authenticator, HttpSession s) {
		ProceedContext pc = (ProceedContext) s.getAttribute("proceedContext");
		List<Authenticator> as = (List<Authenticator>) s.getAttribute("authenticators");
		
		AuthenticationResponse authenticationResponse = null;
		
		Authenticator foundAuthenticator = null;
		
		for(Authenticator a : as) {
			if(authenticator.equalsIgnoreCase(a.getLabel())) {
				foundAuthenticator = a;
				if(foundAuthenticator != null) {
					if(foundAuthenticator.getFactors().size() != 0) {		
						authenticationResponse = idxAuthenticationWrapper.enrollAuthenticator(pc,
								foundAuthenticator.getFactors().get(0));
						System.out.println(authenticationResponse.getAuthenticationStatus());
					}
					
				}
			}
		}
		return generateResponse(authenticationResponse, s);
		
	}
	
	/**
	 * Maintains proceed context in session for multi-step registration or sign in flows
	 * @param authenticationResponse
	 * @param s
	 * @return
	 */
	private RegistrationResponse generateResponse(AuthenticationResponse authenticationResponse, HttpSession s) {
		RegistrationResponse response = new RegistrationResponse();
		if(authenticationResponse.getErrors() == null || authenticationResponse.getErrors().isEmpty()) {
			s.setAttribute("proceedContext", authenticationResponse.getProceedContext());
			if("AWAITING_AUTHENTICATOR_ENROLLMENT_SELECTION".equals(authenticationResponse.getAuthenticationStatus().name())
					|| "AWAITING_AUTHENTICATOR_SELECTION".equals(authenticationResponse.getAuthenticationStatus().name())) {
				s.setAttribute("authenticators", authenticationResponse.getAuthenticators());
			}
			
			
			List<Authenticator> authenticatorList	=	authenticationResponse.getAuthenticators();
			if(authenticatorList != null) {
			Authenticator foundAuthenticator = null;
			for(Authenticator a : authenticatorList) {
				if("phone".equalsIgnoreCase(a.getLabel())) {
					foundAuthenticator = a;
					if(foundAuthenticator != null) {
						
						//get user profile phone
						
					}
				}
			}
			
			response.setProfilePhone("401402402");
		}
			response.setStatus(authenticationResponse.getAuthenticationStatus().name());
			response.setErrors(authenticationResponse.getErrors());
			response.setAuthenticators(authenticationResponse.getAuthenticators());
			response.setTokens(authenticationResponse.getTokenResponse());
			response.setSessionId(s.getId());
			response.setCanSkip(canSkipAuthenticators(authenticationResponse.getProceedContext()));
		}
		else {
			response.setErrors(authenticationResponse.getErrors());
		}
		
		return response;
	}
	
	private AuthenticationResponse begin() {
		return idxAuthenticationWrapper.begin();
	}

	/**
	 * Initiate password recovery process. Prompt users for secondary factors to identify themselves
	 */
	@Override
	public RegistrationResponse resetPassword(String email, HttpSession s) {
		AuthenticationResponse beginResponse = begin();
		AuthenticationResponse authenticationResponse = 
				idxAuthenticationWrapper.recoverPassword(email, beginResponse.getProceedContext());
		return generateResponse(authenticationResponse, s);
	}

	/**
	 * Resend code during email/phone verification process
	 */
	@Override
	public RegistrationResponse resendCode(HttpSession s) {
		ProceedContext pc = (ProceedContext) s.getAttribute("proceedContext");
		AuthenticationResponse authenticationResponse = idxAuthenticationWrapper.resend(pc);
		return generateResponse(authenticationResponse, s);
	}

	/**
	 * Revoke the tokens
	 */
	@Override
	public void logout(TokenResponse tokenResponse) {
		idxAuthenticationWrapper.revokeToken(TokenType.ACCESS_TOKEN, tokenResponse.getAccessToken());
	}
	
	/**
	 * Skip and complete registration if users have already enrolled in all the required factors
	 */
	@Override
	public RegistrationResponse skipAuthenticators(HttpSession s) {
		ProceedContext pc = (ProceedContext) s.getAttribute("proceedContext");
		AuthenticationResponse authenticationResponse = 
				idxAuthenticationWrapper.skipAuthenticatorEnrollment(pc);
		return generateResponse(authenticationResponse, s);
	}
	
	/**
	 * Register phone number to enroll in phone SMS/Voice call factor
	 */
	@Override
	public RegistrationResponse registerPhone(HttpSession s, String phoneNumber) {
		phoneNumber ="+"+phoneNumber;
		ProceedContext pc = (ProceedContext) s.getAttribute("proceedContext");
		List<Authenticator> as = (List<Authenticator>) s.getAttribute("authenticators");
		
		AuthenticationResponse authenticationResponse = null;
		
		Authenticator foundAuthenticator = null;
		
		for(Authenticator a : as) {
			if("Phone".equalsIgnoreCase(a.getLabel())) {
				foundAuthenticator = a;
				if(foundAuthenticator != null) {
					if(foundAuthenticator.getFactors().size() != 0) {
						System.out.println("name >> "+foundAuthenticator.getLabel());			
						authenticationResponse = idxAuthenticationWrapper.submitPhoneAuthenticator(pc, phoneNumber, foundAuthenticator.getFactors().get(0));
						//idxAuthenticationWrapper.
						System.out.println(authenticationResponse.getAuthenticationStatus());
					}
					
				}
			}
		}
		return generateResponse(authenticationResponse, s);
	}
	
	/**
	 * Check if remaining factors are optional
	 * @param pc
	 * @return
	 */
	private Boolean canSkipAuthenticators(ProceedContext pc) {
		return pc != null ? idxAuthenticationWrapper.isSkipAuthenticatorPresent(pc) : false;
	}

}
