package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

  @PersistenceContext private EntityManager entityManager;

  /**
   * CREATE ANSWER
   * @param answerEntity represents a row of information which is to be persisted.
   */
  public AnswerEntity createAnswer(AnswerEntity answerEntity) {
    entityManager.persist(answerEntity);
    return answerEntity;
  }

  /**
   * GET ANSWER BY ID
   * @param answerId id of the answer to be fetched.
   */
  public AnswerEntity getAnswerById(final String answerId) {
    try {
      return entityManager
          .createNamedQuery("getAnswerById", AnswerEntity.class)
          .setParameter("uuid", answerId)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  /**
   * EDIT ANSWER
   * @param answerEntity answer to be updated.
   */
  public void updateAnswer(AnswerEntity answerEntity) {
    entityManager.merge(answerEntity);
  }

  /**
   * DELETE ANSWER
   * @param answerId Id of the answer whose information is to be fetched.
   */
  public AnswerEntity deleteAnswer(final String answerId) {
    AnswerEntity deleteAnswer = getAnswerById(answerId);
    if (deleteAnswer != null) {
      entityManager.remove(deleteAnswer);
    }
    return deleteAnswer;
  }

  /**
   * GET ALL ANSWERS TO A QUESTION
   * @param questionId of the question whose answer if to be fetched.
   */
  public List<AnswerEntity> getAllAnswersToQuestion(final String questionId) {
    return entityManager
        .createNamedQuery("getAllAnswersToQuestion", AnswerEntity.class)
        .setParameter("uuid", questionId)
        .getResultList();
  }
}
