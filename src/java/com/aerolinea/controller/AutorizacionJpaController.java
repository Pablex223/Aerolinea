/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aerolinea.controller;

import com.aerolinea.controller.exceptions.IllegalOrphanException;
import com.aerolinea.controller.exceptions.NonexistentEntityException;
import com.aerolinea.controller.exceptions.PreexistingEntityException;
import com.aerolinea.entities.Autorizacion;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.aerolinea.entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pablo
 */
public class AutorizacionJpaController implements Serializable {

    public AutorizacionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Autorizacion autorizacion) throws PreexistingEntityException, Exception {
        if (autorizacion.getUsuarioList() == null) {
            autorizacion.setUsuarioList(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Usuario> attachedUsuarioList = new ArrayList<Usuario>();
            for (Usuario usuarioListUsuarioToAttach : autorizacion.getUsuarioList()) {
                usuarioListUsuarioToAttach = em.getReference(usuarioListUsuarioToAttach.getClass(), usuarioListUsuarioToAttach.getCedula());
                attachedUsuarioList.add(usuarioListUsuarioToAttach);
            }
            autorizacion.setUsuarioList(attachedUsuarioList);
            em.persist(autorizacion);
            for (Usuario usuarioListUsuario : autorizacion.getUsuarioList()) {
                Autorizacion oldAutorizacionidOfUsuarioListUsuario = usuarioListUsuario.getAutorizacionid();
                usuarioListUsuario.setAutorizacionid(autorizacion);
                usuarioListUsuario = em.merge(usuarioListUsuario);
                if (oldAutorizacionidOfUsuarioListUsuario != null) {
                    oldAutorizacionidOfUsuarioListUsuario.getUsuarioList().remove(usuarioListUsuario);
                    oldAutorizacionidOfUsuarioListUsuario = em.merge(oldAutorizacionidOfUsuarioListUsuario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findAutorizacion(autorizacion.getId()) != null) {
                throw new PreexistingEntityException("Autorizacion " + autorizacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Autorizacion autorizacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Autorizacion persistentAutorizacion = em.find(Autorizacion.class, autorizacion.getId());
            List<Usuario> usuarioListOld = persistentAutorizacion.getUsuarioList();
            List<Usuario> usuarioListNew = autorizacion.getUsuarioList();
            List<String> illegalOrphanMessages = null;
            for (Usuario usuarioListOldUsuario : usuarioListOld) {
                if (!usuarioListNew.contains(usuarioListOldUsuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuario " + usuarioListOldUsuario + " since its autorizacionid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Usuario> attachedUsuarioListNew = new ArrayList<Usuario>();
            for (Usuario usuarioListNewUsuarioToAttach : usuarioListNew) {
                usuarioListNewUsuarioToAttach = em.getReference(usuarioListNewUsuarioToAttach.getClass(), usuarioListNewUsuarioToAttach.getCedula());
                attachedUsuarioListNew.add(usuarioListNewUsuarioToAttach);
            }
            usuarioListNew = attachedUsuarioListNew;
            autorizacion.setUsuarioList(usuarioListNew);
            autorizacion = em.merge(autorizacion);
            for (Usuario usuarioListNewUsuario : usuarioListNew) {
                if (!usuarioListOld.contains(usuarioListNewUsuario)) {
                    Autorizacion oldAutorizacionidOfUsuarioListNewUsuario = usuarioListNewUsuario.getAutorizacionid();
                    usuarioListNewUsuario.setAutorizacionid(autorizacion);
                    usuarioListNewUsuario = em.merge(usuarioListNewUsuario);
                    if (oldAutorizacionidOfUsuarioListNewUsuario != null && !oldAutorizacionidOfUsuarioListNewUsuario.equals(autorizacion)) {
                        oldAutorizacionidOfUsuarioListNewUsuario.getUsuarioList().remove(usuarioListNewUsuario);
                        oldAutorizacionidOfUsuarioListNewUsuario = em.merge(oldAutorizacionidOfUsuarioListNewUsuario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = autorizacion.getId();
                if (findAutorizacion(id) == null) {
                    throw new NonexistentEntityException("The autorizacion with id " + id + " no longer exists.");
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
            Autorizacion autorizacion;
            try {
                autorizacion = em.getReference(Autorizacion.class, id);
                autorizacion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autorizacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Usuario> usuarioListOrphanCheck = autorizacion.getUsuarioList();
            for (Usuario usuarioListOrphanCheckUsuario : usuarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Autorizacion (" + autorizacion + ") cannot be destroyed since the Usuario " + usuarioListOrphanCheckUsuario + " in its usuarioList field has a non-nullable autorizacionid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(autorizacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Autorizacion> findAutorizacionEntities() {
        return findAutorizacionEntities(true, -1, -1);
    }

    public List<Autorizacion> findAutorizacionEntities(int maxResults, int firstResult) {
        return findAutorizacionEntities(false, maxResults, firstResult);
    }

    private List<Autorizacion> findAutorizacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Autorizacion.class));
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

    public Autorizacion findAutorizacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Autorizacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutorizacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Autorizacion> rt = cq.from(Autorizacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
