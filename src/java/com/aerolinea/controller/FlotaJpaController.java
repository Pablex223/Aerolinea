/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.controller;

import com.aerolinea.controller.exceptions.NonexistentEntityException;
import com.aerolinea.controller.exceptions.PreexistingEntityException;
import com.aerolinea.entities.Flota;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Pablo
 */
public class FlotaJpaController implements Serializable {

    public FlotaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Flota flota) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(flota);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findFlota(flota.getId()) != null) {
                throw new PreexistingEntityException("Flota " + flota + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Flota flota) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            flota = em.merge(flota);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = flota.getId();
                if (findFlota(id) == null) {
                    throw new NonexistentEntityException("The flota with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Flota flota;
            try {
                flota = em.getReference(Flota.class, id);
                flota.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The flota with id " + id + " no longer exists.", enfe);
            }
            em.remove(flota);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Flota> findFlotaEntities() {
        return findFlotaEntities(true, -1, -1);
    }

    public List<Flota> findFlotaEntities(int maxResults, int firstResult) {
        return findFlotaEntities(false, maxResults, firstResult);
    }

    private List<Flota> findFlotaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Flota.class));
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

    public Flota findFlota(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Flota.class, id);
        } finally {
            em.close();
        }
    }

    public int getFlotaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Flota> rt = cq.from(Flota.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
