package com.demo.spring_security;

import com.demo.spring_security.domain.User;
import com.demo.spring_security.domain.UserDto;
import com.demo.spring_security.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author Parisana
 */
@Configuration
class OAuth2LoginConfig{

    // The ClientRegistrationRepository serves as a repository for OAuth 2.0 / OpenID Connect 1.0 ClientRegistration
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
    }

    // Configuring predefined providers are not required for spring boot 2
    // its primary role is to manage OAuthAuthorizedClient instances
    // it provides the ability to lookup an OAuth2AccessToken associated with a specific ClientRegistration (client).
    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(this.clientRegistrationRepository());
    }

    // configure google client registration
    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId("CLIENT_ID")
                .clientSecret("CLIENT_SECRET")
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email", "address", "phone")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }

    /*// configure custom client registration
    private ClientRegistration myClientRegistration() {
        return ClientRegistration.withRegistrationId("custom")
                .clientId("app")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .tokenUri("http://localhost:8080/auth/oauth/token")
                .authorizationUri("http://localhost:8080/auth/oauth/authorize")
                .userInfoUri("http://localhost:8080/auth/userinfo")
                .build();
    }
*/
}

@Slf4j
@Controller
@RequestMapping("/")
class OAuth2Controller{
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    UserRepo userRepo;

    @GetMapping
    public String oauthHome(OAuth2AuthenticationToken authenticationToken, Model model) throws Exception {
        log.debug("***Getting oauthHome");
        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
        String email= String.valueOf(attributes.get("email"));

        if (userRepo.findByEmail(email).isPresent()){
            //todo
            return "redirect:/";
        }else {
            log.debug("***Creating new User");
            UserDto userDto= new UserDto();
            userDto.setFirstName(String.valueOf(attributes.get("given_name")));
            userDto.setLastName(String.valueOf(attributes.get("family_name")));
            userDto.setEmail(email);
            model.addAttribute("userDto",userDto);

            return "oauth2/index";
        }
    }
    @RequestMapping(value = "/oauth2/newUser", method = RequestMethod.POST)
    public String oauth2Register(@Valid UserDto userDto, BindingResult result, RedirectAttributes redirectAttributes){
        log.debug("***Registering new user");

        if (result.hasErrors())
            return "oauth2/index";

        User userToSave= new User(userDto.getFirstName(), userDto.getLastName(), userDto.getEmail(), userDto.getPassword());
        User savedUser = userRepo.save(userToSave);
        redirectAttributes.addFlashAttribute("globalMessage", "Successfully signed up");

        List<GrantedAuthority> authorities =
                AuthorityUtils.createAuthorityList("ROLE_USER");
        Authentication auth =
                new UsernamePasswordAuthenticationToken(savedUser, savedUser.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/";
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public String userinfo(OAuth2AuthenticationToken authentication) {
        log.debug("***get user-info page");
        // authentication.getAuthorizedClientRegistrationId() returns the
        // registrationId of the Client that was authorized during the Login flow
        OAuth2AuthorizedClient authorizedClient =
                this.authorizedClientService.loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        log.debug(accessToken.toString());

        return "userinfo";
    }
}
