package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

  @Autowired private AnswerService answerService;

  /**
   * CREATE ANSWER FOR A QUESTION
   *
   * @param accessToken To authenticate the user who is trying to create an answer.
   * @param questionId of the question for which the answer is being created.
   * @param answerRequest Contains the answer content.
   * @return JSON response with answer creation status along with httpStatus.
   * @throws AuthorizationFailedException  If the access token which the user provides does not exist
   *                                       or If the user is not signed in.
   * @throws InvalidQuestionException  If the question uuid entered by the user whose answer
   *                                   is to be posted does not exist.
   */
  @RequestMapping(method = RequestMethod.POST,path = "/question/{questionId}/answer/create",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization")
  final String accessToken,@PathVariable("questionId") final String questionId,AnswerRequest answerRequest)
      throws AuthorizationFailedException, InvalidQuestionException {
    AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setAnswer(answerRequest.getAnswer());
    answerEntity = answerService.createAnswer(answerEntity, accessToken, questionId);
    AnswerResponse answerResponse = new AnswerResponse();
    answerResponse.setId(answerEntity.getUuid());
    answerResponse.setStatus("ANSWER CREATED");
    return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
  }

  /**
   * EDIT THE ANSWER 
   *
   * @param accessToken To authenticate the user who is trying to edit the answer.
   * @param answerId Id of the answer which is to be edited.
   * @param answerEditRequest Contains the new content of the answer.
   */
  @RequestMapping(method = RequestMethod.PUT,path = "/answer/edit/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswer(@RequestHeader("authorization")
  final String accessToken,@PathVariable("answerId") final String answerId,
      AnswerEditRequest answerEditRequest)
      throws AuthorizationFailedException, AnswerNotFoundException {
    AnswerEditResponse answerEditResponse = new AnswerEditResponse();
    AnswerEntity answerEntity =
        answerService.editAnswer(accessToken, answerId, answerEditRequest.getContent());
    answerEditResponse.setId(answerEntity.getUuid());
    answerEditResponse.setStatus("ANSWER EDITED");
    return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
  }

  /**
   * DELETE AN ANSWER
   *
   * @param answerId id of the answer to be delete.
   * @param accessToken token to authenticate user.
   */
  @RequestMapping(method = RequestMethod.DELETE,path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("answerId") String answerId)
      throws AuthorizationFailedException, AnswerNotFoundException {
    AnswerEntity answerEntity = answerService.deleteAnswer(answerId, accessToken);
    AnswerDeleteResponse answerDeleteResponse =
        new AnswerDeleteResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
    return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
  }

  /**
   * GET ALL ANSWERS TO A QUESTION
   *
   * @param questionId to fetch all the answers for a question.
   * @param accessToken access token to authenticate user.
   */
  @RequestMapping(method = RequestMethod.GET,path = "/answer/all/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("questionId") String questionId)
      throws AuthorizationFailedException, InvalidQuestionException {
    List<AnswerEntity> answers = answerService.getAllAnswersToQuestion(questionId, accessToken);
    List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
    for (AnswerEntity answerEntity : answers) {
      AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
      answerDetailsResponse.setId(answerEntity.getUuid());
      answerDetailsResponse.setQuestionContent(answerEntity.getQuestionEntity().getContent());
      answerDetailsResponse.setAnswerContent(answerEntity.getAnswer());
      answerDetailsResponses.add(answerDetailsResponse);
    }
    return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses, HttpStatus.OK);
  }
}
