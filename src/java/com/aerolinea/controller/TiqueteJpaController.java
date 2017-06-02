/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.controller;

import com.aerolinea.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.aerolinea.entities.Movimiento;
import com.aerolinea.entities.Tiquete;
import com.aerolinea.entities.Usuario;
import com.aerolinea.entities.Vuelo;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pablo
 */
public class TiqueteJpaController implements Serializable {

    public TiqueteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tiquete tiquete) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimiento movimientoid = tiquete.getMovimientoid();
            if (movimientoid != null) {
                movimientoid = em.getReference(movimientoid.getClass(), movimientoid.getId());
                tiquete.setMovimientoid(movimientoid);
            }
            Usuario usuariocedula = tiquete.getUsuariocedula();
            if (usuariocedula != null) {
                usuariocedula = em.getReference(usuariocedula.getClass(), usuariocedula.getCedula());
                tiquete.setUsuariocedula(usuariocedula);
            }
            Vuelo vueloid = tiquete.getVueloid();
            if (vueloid != null) {
                vueloid = em.getReference(vueloid.getClass(), vueloid.getId());
                tiquete.setVueloid(vueloid);
            }
            em.persist(tiquete);
            if (movimientoid != null) {
                movimientoid.getTiqueteList().add(tiquete);
                movimientoid = em.merge(movimientoid);
            }
            if (usuariocedula != null) {
                usuariocedula.getTiqueteList().add(tiquete);
                usuariocedula = em.merge(usuariocedula);
            }
            if (vueloid != null) {
                vueloid.getTiqueteList().add(tiquete);
                vueloid = em.merge(vueloid);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tiquete tiquete) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tiquete persistentTiquete = em.find(Tiquete.class, tiquete.getId());
            Movimiento movimientoidOld = persistentTiquete.getMovimientoid();
            Movimiento movimientoidNew = tiquete.getMovimientoid();
            Usuario usuariocedulaOld = persistentTiquete.getUsuariocedula();
            Usuario usuariocedulaNew = tiquete.getUsuariocedula();
            Vuelo vueloidOld = persistentTiquete.getVueloid();
            Vuelo vueloidNew = tiquete.getVueloid();
            if (movimientoidNew != null) {
                movimientoidNew = em.getReference(movimientoidNew.getClass(), movimientoidNew.getId());
                tiquete.setMovimientoid(movimientoidNew);
            }
            if (usuariocedulaNew != null) {
                usuariocedulaNew = em.getReference(usuariocedulaNew.getClass(), usuariocedulaNew.getCedula());
                tiquete.setUsuariocedula(usuariocedulaNew);
            }
            if (vueloidNew != null) {
                vueloidNew = em.getReference(vueloidNew.getClass(), vueloidNew.getId());
                tiquete.setVueloid(vueloidNew);
            }
            tiquete = em.merge(tiquete);
            if (movimientoidOld != null && !movimientoidOld.equals(movimientoidNew)) {
                movimientoidOld.getTiqueteList().remove(tiquete);
                movimientoidOld = em.merge(movimientoidOld);
            }
            if (movimientoidNew != null && !movimientoidNew.equals(movimientoidOld)) {
                movimientoidNew.getTiqueteList().add(tiquete);
                movimientoidNew = em.merge(movimientoidNew);
            }
            if (usuariocedulaOld != null && !usuariocedulaOld.equals(usuariocedulaNew)) {
                usuariocedulaOld.getTiqueteList().remove(tiquete);
                usuariocedulaOld = em.merge(usuariocedulaOld);
            }
            if (usuariocedulaNew != null && !usuariocedulaNew.equals(usuariocedulaOld)) {
                usuariocedulaNew.getTiqueteList().add(tiquete);
                usuariocedulaNew = em.merge(usuariocedulaNew);
            }
            if (vueloidOld != null && !vueloidOld.equals(vueloidNew)) {
                vueloidOld.getTiqueteList().remove(tiquete);
                vueloidOld = em.merge(vueloidOld);
            }
            if (vueloidNew != null && !vueloidNew.equals(vueloidOld)) {
                vueloidNew.getTiqueteList().add(tiquete);
                vueloidNew = em.merge(vueloidNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tiquete.getId();
                if (findTiquete(id) == null) {
                    throw new NonexistentEntityException("The tiquete with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tiquete tiquete;
            try {
                tiquete = em.getReference(Tiquete.class, id);
                tiquete.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tiquete with id " + id + " no longer exists.", enfe);
            }
            Movimiento movimientoid = tiquete.getMovimientoid();
            if (movimientoid != null) {
                movimientoid.getTiqueteList().remove(tiquete);
                movimientoid = em.merge(movimientoid);
            }
            Usuario usuariocedula = tiquete.getUsuariocedula();
            if (usuariocedula != null) {
                usuariocedula.getTiqueteList().remove(tiquete);
                usuariocedula = em.merge(usuariocedula);
            }
            Vuelo vueloid = tiquete.getVueloid();
            if (vueloid != null) {
                vueloid.getTiqueteList().remove(tiquete);
                vueloid = em.merge(vueloid);
            }
            em.remove(tiquete);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tiquete> findTiqueteEntities() {
        return findTiqueteEntities(true, -1, -1);
    }

    public List<Tiquete> findTiqueteEntities(int maxResults, int firstResult) {
        return findTiqueteEntities(false, maxResults, firstResult);
    }

    private List<Tiquete> findTiqueteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tiquete.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Tiquete findTiquete(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tiquete.class, id);
        } finally {
            em.close();
        }
    }

    public int getTiqueteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tiquete> rt = cq.from(Tiquete.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
