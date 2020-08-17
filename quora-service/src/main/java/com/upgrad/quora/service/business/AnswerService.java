package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AnswerService {

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    /***
     * @param accessToken Access token of the signed in user
     * @param questionId Id of question whose answer is to be posted
     * @param answer Answer Entity which is needed to be saved
     * @return {@code AnswerEntity} after the answer is successfully posted having auto generated id
     * @throws AuthorizationFailedException When user is not signed in or expired access token is being used
     * @throws InvalidQuestionException When the questionId provided isn't present
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(String accessToken, String questionId, AnswerEntity answer) throws
            InvalidQuestionException, AuthorizationFailedException {

        UserAuthEntity user = getUser(accessToken);
        if (isUserSignedOut(user)){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        QuestionEntity question = getQuestion(questionId);

        answer.setDate(ZonedDateTime.now());
        answer.setQuestionEntity(question);
        answer.setUuid(UUID.randomUUID().toString());
        answer.setUserEntity(user.getUserEntity());
        return answerDao.createAnswer(answer);
    }

    private UserAuthEntity getUser(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return userAuthEntity;
    }

    private boolean isUserSignedOut(UserAuthEntity userAuthEntity){
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime logoutAt = userAuthEntity.getLogoutAt();
        if (logoutAt == null){
            return false;
        }
        return logoutAt.isBefore(now);
    }

    private QuestionEntity getQuestion(String questionId) throws InvalidQuestionException{
        QuestionEntity question = questionDao.getQuestionById(questionId);
        if(question == null){
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        return question;
    }
}
