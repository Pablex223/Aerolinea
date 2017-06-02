/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.controller;

import com.aerolinea.controller.exceptions.IllegalOrphanException;
import com.aerolinea.controller.exceptions.NonexistentEntityException;
import com.aerolinea.controller.exceptions.PreexistingEntityException;
import com.aerolinea.entities.Movimiento;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.aerolinea.entities.Tiquete;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pablo
 */
public class MovimientoJpaController implements Serializable {

    public MovimientoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Movimiento movimiento) throws PreexistingEntityException, Exception {
        if (movimiento.getTiqueteList() == null) {
            movimiento.setTiqueteList(new ArrayList<Tiquete>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Tiquete> attachedTiqueteList = new ArrayList<Tiquete>();
            for (Tiquete tiqueteListTiqueteToAttach : movimiento.getTiqueteList()) {
                tiqueteListTiqueteToAttach = em.getReference(tiqueteListTiqueteToAttach.getClass(), tiqueteListTiqueteToAttach.getId());
                attachedTiqueteList.add(tiqueteListTiqueteToAttach);
            }
            movimiento.setTiqueteList(attachedTiqueteList);
            em.persist(movimiento);
            for (Tiquete tiqueteListTiquete : movimiento.getTiqueteList()) {
                Movimiento oldMovimientoidOfTiqueteListTiquete = tiqueteListTiquete.getMovimientoid();
                tiqueteListTiquete.setMovimientoid(movimiento);
                tiqueteListTiquete = em.merge(tiqueteListTiquete);
                if (oldMovimientoidOfTiqueteListTiquete != null) {
                    oldMovimientoidOfTiqueteListTiquete.getTiqueteList().remove(tiqueteListTiquete);
                    oldMovimientoidOfTiqueteListTiquete = em.merge(oldMovimientoidOfTiqueteListTiquete);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMovimiento(movimiento.getId()) != null) {
                throw new PreexistingEntityException("Movimiento " + movimiento + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Movimiento movimiento) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimiento persistentMovimiento = em.find(Movimiento.class, movimiento.getId());
            List<Tiquete> tiqueteListOld = persistentMovimiento.getTiqueteList();
            List<Tiquete> tiqueteListNew = movimiento.getTiqueteList();
            List<String> illegalOrphanMessages = null;
            for (Tiquete tiqueteListOldTiquete : tiqueteListOld) {
                if (!tiqueteListNew.contains(tiqueteListOldTiquete)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tiquete " + tiqueteListOldTiquete + " since its movimientoid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Tiquete> attachedTiqueteListNew = new ArrayList<Tiquete>();
            for (Tiquete tiqueteListNewTiqueteToAttach : tiqueteListNew) {
                tiqueteListNewTiqueteToAttach = em.getReference(tiqueteListNewTiqueteToAttach.getClass(), tiqueteListNewTiqueteToAttach.getId());
                attachedTiqueteListNew.add(tiqueteListNewTiqueteToAttach);
            }
            tiqueteListNew = attachedTiqueteListNew;
            movimiento.setTiqueteList(tiqueteListNew);
            movimiento = em.merge(movimiento);
            for (Tiquete tiqueteListNewTiquete : tiqueteListNew) {
                if (!tiqueteListOld.contains(tiqueteListNewTiquete)) {
                    Movimiento oldMovimientoidOfTiqueteListNewTiquete = tiqueteListNewTiquete.getMovimientoid();
                    tiqueteListNewTiquete.setMovimientoid(movimiento);
                    tiqueteListNewTiquete = em.merge(tiqueteListNewTiquete);
                    if (oldMovimientoidOfTiqueteListNewTiquete != null && !oldMovimientoidOfTiqueteListNewTiquete.equals(movimiento)) {
                        oldMovimientoidOfTiqueteListNewTiquete.getTiqueteList().remove(tiqueteListNewTiquete);
                        oldMovimientoidOfTiqueteListNewTiquete = em.merge(oldMovimientoidOfTiqueteListNewTiquete);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = movimiento.getId();
                if (findMovimiento(id) == null) {
                    throw new NonexistentEntityException("The movimiento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Movimiento movimiento;
            try {
                movimiento = em.getReference(Movimiento.class, id);
                movimiento.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The movimiento with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Tiquete> tiqueteListOrphanCheck = movimiento.getTiqueteList();
            for (Tiquete tiqueteListOrphanCheckTiquete : tiqueteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Movimiento (" + movimiento + ") cannot be destroyed since the Tiquete " + tiqueteListOrphanCheckTiquete + " in its tiqueteList field has a non-nullable movimientoid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(movimiento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Movimiento> findMovimientoEntities() {
        return findMovimientoEntities(true, -1, -1);
    }

    public List<Movimiento> findMovimientoEntities(int maxResults, int firstResult) {
        return findMovimientoEntities(false, maxResults, firstResult);
    }

    private List<Movimiento> findMovimientoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Movimiento.class));
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

    public Movimiento findMovimiento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Movimiento.class, id);
        } finally {
            em.close();
        }
    }

    public int getMovimientoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Movimiento> rt = cq.from(Movimiento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
