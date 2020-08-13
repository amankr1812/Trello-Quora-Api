package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserAuthenticationService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/")
public class UserController {

  @Autowired private UserAuthenticationService userAuthService;

  /**
   * This method is for user signup. This method receives the object of SignupUserRequest type with
   * its attributes being set.
   *
   * @return SignupUserResponse - UUID of the user created.
   * @throws SignUpRestrictedException - if the username or email already exist in the database.
   */
  @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupUserResponse> signup(SignupUserRequest signupUserRequest)
      throws SignUpRestrictedException {

    UserEntity userEntity = new UserEntity();
    userEntity.setFirstName(signupUserRequest.getFirstName());
    userEntity.setLastName(signupUserRequest.getLastName());
    userEntity.setUserName(signupUserRequest.getUserName());
    userEntity.setEmail(signupUserRequest.getEmailAddress());
    userEntity.setPassword(signupUserRequest.getPassword());
    userEntity.setCountry(signupUserRequest.getCountry());
    userEntity.setAboutMe(signupUserRequest.getAboutMe());
    userEntity.setDob(signupUserRequest.getDob());
    userEntity.setRole("nonadmin");
    userEntity.setContactNumber(signupUserRequest.getContactNumber());

    UserEntity createdUserEntity = userAuthService.signup(userEntity);
    SignupUserResponse userResponse =
        new SignupUserResponse()
            .id(createdUserEntity.getUuid())
            .status("USER SUCCESSFULLY REGISTERED");
    return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
  }


  /*
   * This method used for signin of the user.
   * In this method the username and password fields are authenticated
   * and respective result or message is being displayed to the use
   */
  @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization)
          throws AuthenticationFailedException {

    byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
    String decodedText = new String(decode);
    String[] decodedArray = decodedText.split(":");
    UserAuthEntity userAuthEntity = userAuthService.signin(decodedArray[0], decodedArray[1]);
    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", userAuthEntity.getAccessToken());
    SigninResponse signinResponse = new SigninResponse();
    signinResponse.setId(userAuthEntity.getUserEntity().getUuid());
    signinResponse.message("SIGNED IN SUCCESSFULLY");

    return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
  }

  }
