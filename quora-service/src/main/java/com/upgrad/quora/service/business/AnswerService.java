package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerService {

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    /**
     * @param accessToken Access token of the signed in user
     * @param questionId Id of question whose answer is to be created
     * @param answer Answer Entity which is needed to be saved
     * @return {@code AnswerEntity} after the answer is successfully created having auto generated id
     * @throws AuthorizationFailedException When user is not signed in or expired access token is being used
     * @throws InvalidQuestionException When the question for corresponding questionId doesn't exist
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String accessToken, final String questionId, AnswerEntity answer) throws
            InvalidQuestionException, AuthorizationFailedException {

        UserAuthEntity user = getUser(accessToken);
        if (isUserSignedOut(user)){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        QuestionEntity question = questionDao.getQuestionById(questionId);
        if(question == null){
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        answer.setDate(ZonedDateTime.now());
        answer.setQuestionEntity(question);
        answer.setUuid(UUID.randomUUID().toString());
        answer.setUserEntity(user.getUserEntity());
        return answerDao.createAnswer(answer);
    }

    /**
     * @param accessToken Access token of the signed in user
     * @param answerId Id of answer which is to be modified
     * @param content New content for the answer which is to be modified
     * @throws AuthorizationFailedException When user is not signed in or expired access token is being used
     * @throws AnswerNotFoundException When the answer for corresponding answerId doesn't exist
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void editAnswerContent(final String accessToken, final String answerId, String content) throws
            AnswerNotFoundException, AuthorizationFailedException {
        UserAuthEntity user = getUser(accessToken);
        if (isUserSignedOut(user)){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        AnswerEntity answer = answerDao.getAnswerById(answerId);
        answer.setAnswer(content);
        if (!user.getUserEntity().getUuid().equals(answer.getUserEntity().getUuid())){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }
        answerDao.editAnswerContent(answer);
    }

    /**
     * @param accessToken Access token of the signed in user
     * @param answerId Id of answer which is to be deleted
     * @throws AuthorizationFailedException When user is not signed in, expired access token is being used,
     * user is not the admin or the owner of the answer
     * @throws AnswerNotFoundException When the answer for corresponding answerId doesn't exist
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(final String accessToken, final String answerId) throws AnswerNotFoundException,
            AuthorizationFailedException {
        UserAuthEntity user = getUser(accessToken);
        if (isUserSignedOut(user)){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        AnswerEntity answer = answerDao.getAnswerById(answerId);
        if (!user.getUserEntity().getUuid().equals(answer.getUserEntity().getUuid()) &&
                user.getUserEntity().getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        answerDao.deleteAnswer(answer);
    }

    /**
     * @param accessToken Access token of the signed in user
     * @param questionId Id of question whose answers are to be fetched
     * @return {@code List<AnswerEntity>} List of answers for that question
     * @throws AuthorizationFailedException When user is not signed in, expired access token is being used
     * @throws InvalidQuestionException When the question for corresponding questionId doesn't exist
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final String accessToken, final String questionId) throws
            InvalidQuestionException, AuthorizationFailedException {
        UserAuthEntity user = getUser(accessToken);
        if (isUserSignedOut(user)){
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        QuestionEntity question = questionDao.getQuestionById(questionId);
        if(question == null){
            throw new InvalidQuestionException("QUES-001",
                    "The question with entered uuid whose details are to be seen does not exist");
        }
        return answerDao.getAllAnswersToQuestion(question.getUuid());
    }

    /**
     * @param accessToken Access token of user
     * @return {@code UserAuthEntity} If the access token is valid
     * @throws AuthorizationFailedException If the user has not signed in
     */
    private UserAuthEntity getUser(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return userAuthEntity;
    }

    /**
     * @param userAuthEntity User auth details
     * @return {@code true} if the user have signed out else {@code false}
     */
    private boolean isUserSignedOut(UserAuthEntity userAuthEntity){
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime logoutAt = userAuthEntity.getLogoutAt();
        if (logoutAt == null){
            return false;
        }
        return logoutAt.isBefore(now);
    }

}
