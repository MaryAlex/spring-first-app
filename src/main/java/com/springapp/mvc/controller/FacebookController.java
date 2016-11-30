package com.springapp.mvc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapp.mvc.model.User;
import com.springapp.mvc.service.Impl.UserDetailsServiceImpl;
import com.springapp.mvc.service.UserService;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

@Controller
public class FacebookController
{

	@Autowired
	UserService service;
	UserDetailsManager userManager;
	@Autowired
	UserDetailsService userDetailsServiceImpl;
	public static final String STATE = "state";
	private String applicationHost = "http://localhost:8080";
	private OAuthService oAuthService;
	// Jackson ObjectMapper
	private ObjectMapper objectMapper;
	Module module;



	@Autowired
	public void FacebookScribeAuthenticator(
	  @Value("778613018927893")
	  String clientId,
	  @Value("ac247e3c467f0866145c466f33ab539a")
	  String clientSecret,
	  @Value("http://localhost:8080")
	  String applicationHost) {
		this.applicationHost = applicationHost;
		this.oAuthService = buildOAuthService(clientId, clientSecret);
		this.objectMapper = new ObjectMapper();
	}

	private OAuthService buildOAuthService(String clientId, String clientSecret) {
	// The callback must match Site-Url in the Facebook app settings
	return new ServiceBuilder()
	  .apiKey(clientId)
	  .apiSecret(clientSecret)
	  .callback("http://localhost:8080/callback")
	  .provider(FacebookApi.class)
	  .build();
	}

	private String getFacebookUserId(Response response) throws IOException {
	  String responseBody = response.getBody();
	  JsonNode jsonNode = objectMapper.readTree(responseBody);
	  JsonNode idNode = jsonNode.get("id");
	  return idNode.asText();
	}

	private String getFacebookUserName(Response response) throws IOException {
	  String responseBody = response.getBody();
	  JsonNode jsonNode = objectMapper.readTree(responseBody);
	  JsonNode idNode = jsonNode.get("name");
	  return idNode.asText();
	}

	@RequestMapping("/facebook")
	public RedirectView startAuthentication(HttpSession session){
	String state = UUID.randomUUID().toString();
	session.setAttribute(STATE, state);
	String authorizationUrl = oAuthService.getAuthorizationUrl(Token.empty()) + "&" + STATE + "=" + state;
	return new RedirectView(authorizationUrl);
	}

	@RequestMapping(value = "/callback", method = RequestMethod.GET)
	public String callback(@RequestParam("code") String code, @RequestParam(STATE) String state, HttpSession session, HttpServletRequest request)throws IOException {
	// Check the state parameter
	String stateFromSession = (String) session.getAttribute(STATE);
	session.removeAttribute(STATE);
	Token accessToken = getRequestToken(code);
	Response response = getResponseForProfile(accessToken);
	String facebookUserId = getFacebookUserId(response);
	String userName = getFacebookUserName(response);
	User user = new User();
	user.setEmail("facebook@" + facebookUserId +  ".fb");
	user.setUsername(userName);
	user.setPassword(facebookUserId);
	user.setNickname(facebookUserId + ".fb");

	try {
		service.addUserIntoDB(user);
		} catch (Exception ex)
		{
		}
	UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
	Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
	SecurityContextHolder.getContext().setAuthentication(authentication);
	return "redirect:welcome";
	}


	private Response getResponseForProfile(Token accessToken) {
	  OAuthRequest oauthRequest =
	      new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
	  oAuthService.signRequest(accessToken, oauthRequest);
	  return oauthRequest.send();
	}

	private Token getRequestToken(String code) {
	  Verifier verifier = new Verifier(code);
	  return oAuthService.getAccessToken(Token.empty(), verifier);
	}

	}
