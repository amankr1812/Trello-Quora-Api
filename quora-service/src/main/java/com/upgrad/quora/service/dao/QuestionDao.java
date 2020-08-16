package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * CREATE QUESTION
     * Signin before creating a question is mandatory
     * @param accessToken of user who logged in
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     * GET ALL QUESTIONS
     * Signin before creating a question is mandatory
     */
    public List<QuestionEntity> getAllQuestions() {
        try{
            return entityManager
                    .createNamedQuery("getAllQuestions", QuestionEntity.class)
                    .getResultList();
        }
        catch (NoResultException nre) {
            return null;
        }
    }
    

    /**
     * GET QUESTION FOR GIVEN ID
     * @param questionId id of the required question.
     */
    public QuestionEntity getQuestionById(final String questionId) {
      try {
        return entityManager.createNamedQuery("getQuestionById", QuestionEntity.class).setParameter("uuid", questionId).getSingleResult();
      } catch (NoResultException nre) {
        return null;
      }
    }

    /**
     * UPDATE/EDIT QUESTION
     * @param questionEntity to be updated.
     */
    public void updateQuestion(QuestionEntity questionEntity) {
      entityManager.merge(questionEntity);
    }

    /**
     * DELETE QUESTION
     * @param questionEntity to be deleted.
     */
    public void deleteQuestion(QuestionEntity questionEntity) {
      entityManager.remove(questionEntity);
    }

    /**
     * GET ALL QUESTION BY SPECIFIC USER
     *
     * @param uuid of the user are questions are to be seen
     */
    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userId) {
      return entityManager.createNamedQuery("getQuestionByUser", QuestionEntity.class).setParameter("user", userId).getResultList();
    }
}
