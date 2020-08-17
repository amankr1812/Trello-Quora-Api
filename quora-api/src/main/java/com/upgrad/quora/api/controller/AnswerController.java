package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
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

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    /***
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
            @RequestHeader("authorization") final String accessToken, @RequestBody AnswerRequest request,
            @PathVariable("questionId") String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answer = new AnswerEntity();
        answer.setAnswer(request.getAnswer());
        answer = answerService.createAnswer(accessToken, questionId, answer);

        AnswerResponse response = new AnswerResponse().id(answer.getUuid()).status("CREATED - Answer created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /***
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
            @PathVariable("answerId") String answerId) throws AuthorizationFailedException, AnswerNotFoundException {

        answerService.editAnswerContent(accessToken, answerId, request.getContent());
        AnswerEditResponse response = new AnswerEditResponse().status("OK - Answer changed successfully");
        return new ResponseEntity<AnswerEditResponse>(response, HttpStatus.OK);
    }
}
