package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
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
     * @param questionId Id of question whose answer is to be posted
     * @return {@code ResponseEntity<AnswerResponse>} after the answer is successfully posted
     * @throws AuthorizationFailedException When user is not signed in or expired access token is being used
     * @throws InvalidQuestionException When the questionId provided isn't present
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
}
