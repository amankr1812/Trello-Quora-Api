package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
     * Get all QUESTIONS
     * Signin before creating a question is mandatory
     */
//    public List<QuestionEntity> getAllQuestions() {
//        try{
//            return entityManager
//                    .createNamedQuery("getAllQuestions", QuestionEntity.class)
//                    .getResultList();
//        }
//        catch (NoResultException nre) {
//            return null;
//        }
//    }
}
