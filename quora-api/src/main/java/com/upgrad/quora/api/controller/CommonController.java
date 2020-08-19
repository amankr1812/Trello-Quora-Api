package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * Get UserProfile - gets the user details
     * Signin before getting user details is mandatory
     * @param userUuid of user whose details you want to get
     * @param accessToken(authorization) of signed in user
     */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(@PathVariable("userId") final String userUuid,
                                                           @RequestHeader("authorization") final  String authorization) throws AuthorizationFailedException, UserNotFoundException {

       // String[] bearerToken = authorization.split("Bearer ");
        UserEntity userEntity = commonService.userProfile(userUuid, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUserName())
                .emailAddress(userEntity.getEmail())
                .country(userEntity.getCountry())
                .aboutMe(userEntity.getAboutMe())
                .dob(userEntity.getDob())
                .contactNumber(userEntity.getContactNumber());

        return  new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
