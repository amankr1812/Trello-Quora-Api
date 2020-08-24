package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @throws AuthorizationFailedException If the access token which the user provides does not exist
     *                                      or If the user is not signed in.
     * @return JSON Response with Question creation status and HTTPStatus.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final  String authorization)
    throws AuthorizationFailedException {

        //String[] bearerToken = authorization.split("Bearer ");
        QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());


        QuestionEntity createdQuestion = questionService.createQuestion(questionEntity, authorization);

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

       // String[] bearerToken = authorization.split("Bearer ");
        List<QuestionEntity> questionEntity = questionService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> questionDetailsResponse = new ArrayList<>();
        for(QuestionEntity questions :questionEntity){
            QuestionDetailsResponse response = new QuestionDetailsResponse();
            response.setId(questions.getUuid());
            response.setContent(questions.getContent());
            questionDetailsResponse.add(response);
        }

        return  new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponse, HttpStatus.OK);
    }
    
    /**
     * Get all questions by a user
     *
     * @param uuid of the user whose questions are to be seen
     * @param accessToken of the user user.
     */
    @RequestMapping( method = RequestMethod.GET, path = "question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE) 
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserId(@RequestHeader("authorization") final String accessToken, @PathVariable("userId") String userId)
        throws AuthorizationFailedException, UserNotFoundException {

      List<QuestionEntity> questions = questionService.getAllQuestionsByUser(userId, accessToken);
      List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();
      for (QuestionEntity questionEntity : questions) {
        QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();
        questionDetailResponse.setId(questionEntity.getUuid());
        questionDetailResponse.setContent(questionEntity.getContent());
        questionDetailResponses.add(questionDetailResponse);
      }
      return new ResponseEntity<List<QuestionDetailsResponse>>(
          questionDetailResponses, HttpStatus.OK);
    }
    
    /**
     * EDIT QUESTION BY USER
     * @param accessToken of the user signed in.
     * @param questionId of the question to be edited.
     * @param questionEditRequest new content for the question.
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(
    		@RequestHeader("authorization") final String accessToken,
    		@PathVariable("questionId")final String questionId,QuestionEditRequest questionEditRequest)
        throws AuthorizationFailedException, InvalidQuestionException {
    	
      QuestionEntity questionEntity = questionService.editQuestion(accessToken, questionId, questionEditRequest.getContent());
      QuestionEditResponse questionEditResponse = new QuestionEditResponse();
      questionEditResponse.setId(questionEntity.getUuid());
      questionEditResponse.setStatus("QUESTION EDITED");
      return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
    
    /**
     * DELETE A QUESTION
     *
     * @param accessToken of the signed in user
     * @param questionId of the question to be edited.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
        @RequestHeader("authorization") final String accessToken,
        @PathVariable("questionId") final String questionId)
        throws AuthorizationFailedException, InvalidQuestionException {

      QuestionEntity questionEntity = questionService.deleteQuestion(accessToken, questionId);
      QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
      questionDeleteResponse.setId(questionEntity.getUuid());
      questionDeleteResponse.setStatus("QUESTION DELETED");
      return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }


}
