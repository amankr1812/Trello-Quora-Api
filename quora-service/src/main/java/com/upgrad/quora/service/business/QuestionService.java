package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthDao userAuthDao;


    /**
     * CREATE QUESTION
     * Signin before creating a question is mandatory
     * @param QuestionRequest of user to be created
     * @param accessToken of user who logged in
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String accesstoken)
                    throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accesstoken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to post a question");
        }

        // Assign a UUID to the user that is being created.
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUserEntity(userAuthEntity.getUserEntity());

        return questionDao.createQuestion(questionEntity);
    }

    /**
     * Get All QUESTION
     * Signin before creating a question is mandatory
     * @param accessToken of user who logged in
     */
    public List<QuestionEntity> getAllQuestions(String accessToken) throws AuthorizationFailedException{

        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to get all questions");
        }

        List<QuestionEntity> questionEntity = questionDao.getAllQuestions();

        return questionEntity;
    }
}
