package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * CREATE QUESTION
     * Signin before creating a question is mandatory
     * @param QuestionRequest of user to be created
     * @param accessToken of user who logged in
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final  String authorization)
    throws AuthorizationFailedException {

        String[] bearerToken = authorization.split("Bearer ");
        QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());


        QuestionEntity createdQuestion = questionService.createQuestion(questionEntity, bearerToken[1]);

        QuestionResponse questionResponse = new QuestionResponse()
                .id(createdQuestion.getUuid())
                .status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }



    /**
     * Get All QUESTION
     * Signin before creating a question is mandatory
     * @param accessToken of user who logged in
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final  String authorization)
            throws AuthorizationFailedException{

        String[] bearerToken = authorization.split("Bearer ");
        List<QuestionEntity> questionEntity = questionService.getAllQuestions(bearerToken[1]);
        List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<>();
        for(QuestionEntity questions :questionEntity){
            QuestionDetailsResponse response = new QuestionDetailsResponse();
            response.setId(questions.getUuid());
            response.setContent(questions.getContent());
            questionDetailsResponse.add(response);
        }

        return  new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponse, HttpStatus.OK);
    }

}
