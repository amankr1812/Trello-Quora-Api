package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /***
     * Create Answer for a corresponding question and user then save it
     * @param answer Answer Entity which is needed to be saved
     * @return {@code AnswerEntity} after the answer is successfully posted having auto generated id
     */
    public AnswerEntity createAnswer(AnswerEntity answer) {
        entityManager.persist(answer);
        return answer;
    }

    /***
     * Modify Answer for a corresponding question and user then save it
     * @param answer Answer Entity which is needed to be saved
     */
    public void editAnswerContent(AnswerEntity answer) {
        entityManager.merge(answer);
    }

    /***
     * Delete Answer for a corresponding question and user
     * @param answer Answer Entity which is to be deleted
     */
    public void deleteAnswer(AnswerEntity answer) {
        entityManager.remove(answer);
    }

    public AnswerEntity getAnswerById(String answerId) throws AnswerNotFoundException {
        try {
            return entityManager.createNamedQuery("getAnswerById", AnswerEntity.class)
                    .setParameter("uuid", answerId).getSingleResult();
        } catch (NoResultException e) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
    }
}
