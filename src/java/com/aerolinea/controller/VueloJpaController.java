/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.controller;

import com.aerolinea.controller.exceptions.IllegalOrphanException;
import com.aerolinea.controller.exceptions.NonexistentEntityException;
import com.aerolinea.controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.aerolinea.entities.Avion;
import com.aerolinea.entities.Ruta;
import com.aerolinea.entities.Tiquete;
import com.aerolinea.entities.Vuelo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pablo
 */
public class VueloJpaController implements Serializable {

    public VueloJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vuelo vuelo) throws PreexistingEntityException, Exception {
        if (vuelo.getTiqueteList() == null) {
            vuelo.setTiqueteList(new ArrayList<Tiquete>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Avion avionid = vuelo.getAvionid();
            if (avionid != null) {
                avionid = em.getReference(avionid.getClass(), avionid.getId());
                vuelo.setAvionid(avionid);
            }
            Ruta rutaid = vuelo.getRutaid();
            if (rutaid != null) {
                rutaid = em.getReference(rutaid.getClass(), rutaid.getId());
                vuelo.setRutaid(rutaid);
            }
            List<Tiquete> attachedTiqueteList = new ArrayList<Tiquete>();
            for (Tiquete tiqueteListTiqueteToAttach : vuelo.getTiqueteList()) {
                tiqueteListTiqueteToAttach = em.getReference(tiqueteListTiqueteToAttach.getClass(), tiqueteListTiqueteToAttach.getId());
                attachedTiqueteList.add(tiqueteListTiqueteToAttach);
            }
            vuelo.setTiqueteList(attachedTiqueteList);
            em.persist(vuelo);
            if (avionid != null) {
                avionid.getVueloList().add(vuelo);
                avionid = em.merge(avionid);
            }
            if (rutaid != null) {
                rutaid.getVueloList().add(vuelo);
                rutaid = em.merge(rutaid);
            }
            for (Tiquete tiqueteListTiquete : vuelo.getTiqueteList()) {
                Vuelo oldVueloidOfTiqueteListTiquete = tiqueteListTiquete.getVueloid();
                tiqueteListTiquete.setVueloid(vuelo);
                tiqueteListTiquete = em.merge(tiqueteListTiquete);
                if (oldVueloidOfTiqueteListTiquete != null) {
                    oldVueloidOfTiqueteListTiquete.getTiqueteList().remove(tiqueteListTiquete);
                    oldVueloidOfTiqueteListTiquete = em.merge(oldVueloidOfTiqueteListTiquete);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVuelo(vuelo.getId()) != null) {
                throw new PreexistingEntityException("Vuelo " + vuelo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vuelo vuelo) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vuelo persistentVuelo = em.find(Vuelo.class, vuelo.getId());
            Avion avionidOld = persistentVuelo.getAvionid();
            Avion avionidNew = vuelo.getAvionid();
            Ruta rutaidOld = persistentVuelo.getRutaid();
            Ruta rutaidNew = vuelo.getRutaid();
            List<Tiquete> tiqueteListOld = persistentVuelo.getTiqueteList();
            List<Tiquete> tiqueteListNew = vuelo.getTiqueteList();
            List<String> illegalOrphanMessages = null;
            for (Tiquete tiqueteListOldTiquete : tiqueteListOld) {
                if (!tiqueteListNew.contains(tiqueteListOldTiquete)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tiquete " + tiqueteListOldTiquete + " since its vueloid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (avionidNew != null) {
                avionidNew = em.getReference(avionidNew.getClass(), avionidNew.getId());
                vuelo.setAvionid(avionidNew);
            }
            if (rutaidNew != null) {
                rutaidNew = em.getReference(rutaidNew.getClass(), rutaidNew.getId());
                vuelo.setRutaid(rutaidNew);
            }
            List<Tiquete> attachedTiqueteListNew = new ArrayList<Tiquete>();
            for (Tiquete tiqueteListNewTiqueteToAttach : tiqueteListNew) {
                tiqueteListNewTiqueteToAttach = em.getReference(tiqueteListNewTiqueteToAttach.getClass(), tiqueteListNewTiqueteToAttach.getId());
                attachedTiqueteListNew.add(tiqueteListNewTiqueteToAttach);
            }
            tiqueteListNew = attachedTiqueteListNew;
            vuelo.setTiqueteList(tiqueteListNew);
            vuelo = em.merge(vuelo);
            if (avionidOld != null && !avionidOld.equals(avionidNew)) {
                avionidOld.getVueloList().remove(vuelo);
                avionidOld = em.merge(avionidOld);
            }
            if (avionidNew != null && !avionidNew.equals(avionidOld)) {
                avionidNew.getVueloList().add(vuelo);
                avionidNew = em.merge(avionidNew);
            }
            if (rutaidOld != null && !rutaidOld.equals(rutaidNew)) {
                rutaidOld.getVueloList().remove(vuelo);
                rutaidOld = em.merge(rutaidOld);
            }
            if (rutaidNew != null && !rutaidNew.equals(rutaidOld)) {
                rutaidNew.getVueloList().add(vuelo);
                rutaidNew = em.merge(rutaidNew);
            }
            for (Tiquete tiqueteListNewTiquete : tiqueteListNew) {
                if (!tiqueteListOld.contains(tiqueteListNewTiquete)) {
                    Vuelo oldVueloidOfTiqueteListNewTiquete = tiqueteListNewTiquete.getVueloid();
                    tiqueteListNewTiquete.setVueloid(vuelo);
                    tiqueteListNewTiquete = em.merge(tiqueteListNewTiquete);
                    if (oldVueloidOfTiqueteListNewTiquete != null && !oldVueloidOfTiqueteListNewTiquete.equals(vuelo)) {
                        oldVueloidOfTiqueteListNewTiquete.getTiqueteList().remove(tiqueteListNewTiquete);
                        oldVueloidOfTiqueteListNewTiquete = em.merge(oldVueloidOfTiqueteListNewTiquete);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = vuelo.getId();
                if (findVuelo(id) == null) {
                    throw new NonexistentEntityException("The vuelo with id " + id + " no longer exists.");
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
            Vuelo vuelo;
            try {
                vuelo = em.getReference(Vuelo.class, id);
                vuelo.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vuelo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Tiquete> tiqueteListOrphanCheck = vuelo.getTiqueteList();
            for (Tiquete tiqueteListOrphanCheckTiquete : tiqueteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Vuelo (" + vuelo + ") cannot be destroyed since the Tiquete " + tiqueteListOrphanCheckTiquete + " in its tiqueteList field has a non-nullable vueloid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Avion avionid = vuelo.getAvionid();
            if (avionid != null) {
                avionid.getVueloList().remove(vuelo);
                avionid = em.merge(avionid);
            }
            Ruta rutaid = vuelo.getRutaid();
            if (rutaid != null) {
                rutaid.getVueloList().remove(vuelo);
                rutaid = em.merge(rutaid);
            }
            em.remove(vuelo);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Vuelo> findVueloEntities() {
        return findVueloEntities(true, -1, -1);
    }

    public List<Vuelo> findVueloEntities(int maxResults, int firstResult) {
        return findVueloEntities(false, maxResults, firstResult);
    }

    private List<Vuelo> findVueloEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vuelo.class));
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

    public Vuelo findVuelo(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vuelo.class, id);
        } finally {
            em.close();
        }
    }

    public int getVueloCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vuelo> rt = cq.from(Vuelo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
