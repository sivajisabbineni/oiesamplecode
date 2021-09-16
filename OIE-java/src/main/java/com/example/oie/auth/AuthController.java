package com.example.oie.auth;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.oie.auth.model.AcmeUserProfile;
import com.example.oie.auth.model.RegistrationResponse;
import com.okta.idx.sdk.api.response.TokenResponse;


@RestController
@CrossOrigin(exposedHeaders = {"X-Auth-Token"})
@RequestMapping("/api/v1/auth/")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	/**
	 * Register a new user. 
	 * If the auth status is AWAITING_AUTHENTICATOR_ENROLLMENT_SELECTION
	 * then present the user with the list of available authenticators to choose
	 * @param s
	 * @return
	 */
	@PostMapping("/register")
	public ResponseEntity<RegistrationResponse> greet(final HttpSession s, @RequestBody AcmeUserProfile profile){
		
		return ResponseEntity.ok(authService.signUp(s, profile));
	}
	
	/**
	 * Enroll the user in a factor
	 * Based on user selection from available factors
	 * @param s
	 * @param authenticator
	 * @return
	 */
	@GetMapping("/enroll-authenticator")
	public ResponseEntity<RegistrationResponse> enrollAuthenticator(final HttpSession s, @RequestParam(name="authenticator") String authenticator){
		
		return ResponseEntity.ok(authService.enrollAuthenticator(authenticator, s));
	}
	
	/**
	 * Select the factor to use for authentication
	 * Based on user selection from available factors
	 * @param s
	 * @param authenticator
	 * @return
	 */
	@GetMapping("/select-authenticator")
	public ResponseEntity<RegistrationResponse> selectAuthenticator(final HttpSession s, @RequestParam(name="authenticator") String authenticator){
		
		return ResponseEntity.ok(authService.selectAuthenticator(authenticator, s));
	}
	
	/**
	 * Answer the enrollment challenge.
	 * If enrollment is password, users must enter a new password
	 * If enrollment is for email, the user must enter the code they received in their email
	 * @param s
	 * @param answer
	 * @return
	 */
	@GetMapping("/answer-enrollment-challenge")
	public ResponseEntity<RegistrationResponse> answerChallenge(final HttpSession s, @RequestParam(name="answer") String answer){
		
		return ResponseEntity.ok(authService.answerChallenge(answer, s));
	}
	
	@PostMapping("/login")
	public ResponseEntity<RegistrationResponse> login(final HttpSession s, 
			@RequestBody Map<String, String> credentials){
		return ResponseEntity.ok(authService.login(credentials.get("username"), credentials.get("password"), s));
		
	}
	
	@GetMapping("/recover-password")
	public ResponseEntity<RegistrationResponse> recoverPassword(final HttpSession s, @RequestParam(name="email") String email){
		
		return ResponseEntity.ok(authService.resetPassword(email, s));
	}
	
	@GetMapping("/resend-code")
	public ResponseEntity<RegistrationResponse> resendCode(final HttpSession s){
		
		return ResponseEntity.ok(authService.resendCode(s));
	}
	
	@PostMapping("/logout")
	public ResponseEntity logout(final HttpSession s, @RequestBody TokenResponse tokens) {
		authService.logout(tokens);
		s.invalidate();
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/skip")
	public ResponseEntity<RegistrationResponse> skip(final HttpSession s){
		
		return ResponseEntity.ok(authService.skipAuthenticators(s));
	}
	
	@GetMapping("/enroll-phone")
	public ResponseEntity<RegistrationResponse> enrollPhone(final HttpSession s, @RequestParam(name="phoneNumber") String phoneNumber){
		
		return ResponseEntity.ok(authService.registerPhone(s, phoneNumber));
	}

}
