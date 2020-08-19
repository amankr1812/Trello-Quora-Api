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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    /**
     * @param accessToken Access token of the signed in user
     * @param request Request body of answer
     * @param questionId Id of question whose answer is to be created
     * @return {@code ResponseEntity<AnswerResponse>} after the answer is successfully created
     * @throws AuthorizationFailedException When user is not signed in or expired access token is being used
     * @throws InvalidQuestionException When the question for corresponding questionId doesn't exist
     */
    @RequestMapping(path = "/question/{questionId}/answer/create", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(
            @RequestHeader("authorization") final String accessToken,
            @RequestBody AnswerRequest request,
            @PathVariable("questionId") String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answer = new AnswerEntity();
        answer.setAnswer(request.getAnswer());
        answer = answerService.createAnswer(accessToken, questionId, answer);

        AnswerResponse response = new AnswerResponse().id(answer.getUuid()).status("CREATED - Answer created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * @param accessToken Access token of the signed in user
     * @param request Request body of edit answer
     * @param answerId Id of answer which is to be modified
     * @return {@code ResponseEntity<AnswerResponse>} after the answer is successfully posted
     * @throws AuthorizationFailedException When user is not signed in or expired access token is being used
     * @throws AnswerNotFoundException When the answer for corresponding answerId doesn't exist
     */
    @RequestMapping(path = "/answer/edit/{answerId}", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(
            @RequestHeader("authorization") final String accessToken,
            @RequestBody AnswerEditRequest request,
            @PathVariable("answerId") final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {

        answerService.editAnswerContent(accessToken, answerId, request.getContent());
        AnswerEditResponse response = new AnswerEditResponse().status("OK - Answer changed successfully");
        return new ResponseEntity<AnswerEditResponse>(response, HttpStatus.OK);
    }

    /**
     * @param accessToken Access token of the signed in user
     * @param answerId Id of answer which is to be deleted
     * @return {@code ResponseEntity<AnswerResponse>} after the answer is successfully posted
     * @throws AuthorizationFailedException When user is not signed in, expired access token is being used,
     * user is not the admin or the owner of the answer
     * @throws AnswerNotFoundException When the answer for corresponding answerId doesn't exist
     */
    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("answerId") final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerDeleteResponse response = new AnswerDeleteResponse().id(answerId).status("OK - Answer deleted successfully");
        answerService.deleteAnswer(accessToken, answerId);
        return new ResponseEntity<AnswerDeleteResponse>(response, HttpStatus.OK);
    }

    /**
     * @param accessToken Access token of the signed in user
     * @param questionId Id of question whose answers are to be fetched
     * @return {@code List<AnswerDetailsResponse>} List of answers for that question
     * @throws AuthorizationFailedException When user is not signed in, expired access token is being used
     * @throws InvalidQuestionException When the question for corresponding questionId doesn't exist
     */
    @RequestMapping(path = "answer/all/{questionId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("questionId") String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> result = answerService.getAllAnswersToQuestion(accessToken, questionId);
        List<AnswerDetailsResponse> response = result.stream().map(answer -> {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.setId(answer.getUuid());
            answerDetailsResponse.setAnswerContent(answer.getAnswer());
            answerDetailsResponse.setQuestionContent(answer.getQuestionEntity().getContent());
            return answerDetailsResponse;
        }).collect(Collectors.toList());
        return new ResponseEntity<List<AnswerDetailsResponse>>(response, HttpStatus.OK);
    }
}
