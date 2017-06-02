/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.controller;

import com.aerolinea.controller.exceptions.IllegalOrphanException;
import com.aerolinea.controller.exceptions.NonexistentEntityException;
import com.aerolinea.controller.exceptions.PreexistingEntityException;
import com.aerolinea.entities.Ruta;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.aerolinea.entities.Vuelo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pablo
 */
public class RutaJpaController implements Serializable {

    public RutaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ruta ruta) throws PreexistingEntityException, Exception {
        if (ruta.getVueloList() == null) {
            ruta.setVueloList(new ArrayList<Vuelo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Vuelo> attachedVueloList = new ArrayList<Vuelo>();
            for (Vuelo vueloListVueloToAttach : ruta.getVueloList()) {
                vueloListVueloToAttach = em.getReference(vueloListVueloToAttach.getClass(), vueloListVueloToAttach.getId());
                attachedVueloList.add(vueloListVueloToAttach);
            }
            ruta.setVueloList(attachedVueloList);
            em.persist(ruta);
            for (Vuelo vueloListVuelo : ruta.getVueloList()) {
                Ruta oldRutaidOfVueloListVuelo = vueloListVuelo.getRutaid();
                vueloListVuelo.setRutaid(ruta);
                vueloListVuelo = em.merge(vueloListVuelo);
                if (oldRutaidOfVueloListVuelo != null) {
                    oldRutaidOfVueloListVuelo.getVueloList().remove(vueloListVuelo);
                    oldRutaidOfVueloListVuelo = em.merge(oldRutaidOfVueloListVuelo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRuta(ruta.getId()) != null) {
                throw new PreexistingEntityException("Ruta " + ruta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ruta ruta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ruta persistentRuta = em.find(Ruta.class, ruta.getId());
            List<Vuelo> vueloListOld = persistentRuta.getVueloList();
            List<Vuelo> vueloListNew = ruta.getVueloList();
            List<String> illegalOrphanMessages = null;
            for (Vuelo vueloListOldVuelo : vueloListOld) {
                if (!vueloListNew.contains(vueloListOldVuelo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vuelo " + vueloListOldVuelo + " since its rutaid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Vuelo> attachedVueloListNew = new ArrayList<Vuelo>();
            for (Vuelo vueloListNewVueloToAttach : vueloListNew) {
                vueloListNewVueloToAttach = em.getReference(vueloListNewVueloToAttach.getClass(), vueloListNewVueloToAttach.getId());
                attachedVueloListNew.add(vueloListNewVueloToAttach);
            }
            vueloListNew = attachedVueloListNew;
            ruta.setVueloList(vueloListNew);
            ruta = em.merge(ruta);
            for (Vuelo vueloListNewVuelo : vueloListNew) {
                if (!vueloListOld.contains(vueloListNewVuelo)) {
                    Ruta oldRutaidOfVueloListNewVuelo = vueloListNewVuelo.getRutaid();
                    vueloListNewVuelo.setRutaid(ruta);
                    vueloListNewVuelo = em.merge(vueloListNewVuelo);
                    if (oldRutaidOfVueloListNewVuelo != null && !oldRutaidOfVueloListNewVuelo.equals(ruta)) {
                        oldRutaidOfVueloListNewVuelo.getVueloList().remove(vueloListNewVuelo);
                        oldRutaidOfVueloListNewVuelo = em.merge(oldRutaidOfVueloListNewVuelo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = ruta.getId();
                if (findRuta(id) == null) {
                    throw new NonexistentEntityException("The ruta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ruta ruta;
            try {
                ruta = em.getReference(Ruta.class, id);
                ruta.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ruta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Vuelo> vueloListOrphanCheck = ruta.getVueloList();
            for (Vuelo vueloListOrphanCheckVuelo : vueloListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ruta (" + ruta + ") cannot be destroyed since the Vuelo " + vueloListOrphanCheckVuelo + " in its vueloList field has a non-nullable rutaid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(ruta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ruta> findRutaEntities() {
        return findRutaEntities(true, -1, -1);
    }

    public List<Ruta> findRutaEntities(int maxResults, int firstResult) {
        return findRutaEntities(false, maxResults, firstResult);
    }

    private List<Ruta> findRutaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ruta.class));
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

    public Ruta findRuta(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ruta.class, id);
        } finally {
            em.close();
        }
    }

    public int getRutaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ruta> rt = cq.from(Ruta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
