/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.arquillian.managers;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jboss.arquillian.model.routing.Word;
import org.jboss.logging.Logger;

/**
 *
 * @author spriadka
 */
@Stateless
public class WordManager {

    @PersistenceContext
    private EntityManager em;

    private final Logger LOGGER = Logger.getLogger(WordManager.class);

    public Word addWord(Word word) {
        Query query = em.createQuery("SELECT w FROM WORD w WHERE w.value=:value");
        query.setParameter("value", word.getValue());
        Word result = null;
        try {
            result = (Word)query.getSingleResult();
            return result;
        }
        catch (NoResultException notFound){
            em.persist(word);
            return word;
        }
        
    }

    public Word getWord(long wordId) {
        return em.find(Word.class, wordId);
    }
    
    public Word addWordFromValue(String value){
        Word word = new Word();
        word.setValue(value);
        return addWord(word);
    }
    
    public Word getWordFromValue(String value){
        Query query = em.createQuery("SELECT w FROM WORD w WHERE w.value=:value");
        query.setParameter("value", value);
        Word result = null;
        try{
            result = (Word)query.getSingleResult();
            return result;
        }
        catch(NoResultException nre){
            return null;
        }
    }

    public void deleteWord(Word word) {
        em.remove(em.contains(word) ? word : em.merge(word));
    }

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }

}
