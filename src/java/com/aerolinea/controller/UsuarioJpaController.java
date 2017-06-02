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
import com.aerolinea.entities.Autorizacion;
import com.aerolinea.entities.Tiquete;
import com.aerolinea.entities.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Pablo
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getTiqueteList() == null) {
            usuario.setTiqueteList(new ArrayList<Tiquete>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Autorizacion autorizacionid = usuario.getAutorizacionid();
            if (autorizacionid != null) {
                autorizacionid = em.getReference(autorizacionid.getClass(), autorizacionid.getId());
                usuario.setAutorizacionid(autorizacionid);
            }
            List<Tiquete> attachedTiqueteList = new ArrayList<Tiquete>();
            for (Tiquete tiqueteListTiqueteToAttach : usuario.getTiqueteList()) {
                tiqueteListTiqueteToAttach = em.getReference(tiqueteListTiqueteToAttach.getClass(), tiqueteListTiqueteToAttach.getId());
                attachedTiqueteList.add(tiqueteListTiqueteToAttach);
            }
            usuario.setTiqueteList(attachedTiqueteList);
            em.persist(usuario);
            if (autorizacionid != null) {
                autorizacionid.getUsuarioList().add(usuario);
                autorizacionid = em.merge(autorizacionid);
            }
            for (Tiquete tiqueteListTiquete : usuario.getTiqueteList()) {
                Usuario oldUsuariocedulaOfTiqueteListTiquete = tiqueteListTiquete.getUsuariocedula();
                tiqueteListTiquete.setUsuariocedula(usuario);
                tiqueteListTiquete = em.merge(tiqueteListTiquete);
                if (oldUsuariocedulaOfTiqueteListTiquete != null) {
                    oldUsuariocedulaOfTiqueteListTiquete.getTiqueteList().remove(tiqueteListTiquete);
                    oldUsuariocedulaOfTiqueteListTiquete = em.merge(oldUsuariocedulaOfTiqueteListTiquete);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getCedula()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getCedula());
            Autorizacion autorizacionidOld = persistentUsuario.getAutorizacionid();
            Autorizacion autorizacionidNew = usuario.getAutorizacionid();
            List<Tiquete> tiqueteListOld = persistentUsuario.getTiqueteList();
            List<Tiquete> tiqueteListNew = usuario.getTiqueteList();
            List<String> illegalOrphanMessages = null;
            for (Tiquete tiqueteListOldTiquete : tiqueteListOld) {
                if (!tiqueteListNew.contains(tiqueteListOldTiquete)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tiquete " + tiqueteListOldTiquete + " since its usuariocedula field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (autorizacionidNew != null) {
                autorizacionidNew = em.getReference(autorizacionidNew.getClass(), autorizacionidNew.getId());
                usuario.setAutorizacionid(autorizacionidNew);
            }
            List<Tiquete> attachedTiqueteListNew = new ArrayList<Tiquete>();
            for (Tiquete tiqueteListNewTiqueteToAttach : tiqueteListNew) {
                tiqueteListNewTiqueteToAttach = em.getReference(tiqueteListNewTiqueteToAttach.getClass(), tiqueteListNewTiqueteToAttach.getId());
                attachedTiqueteListNew.add(tiqueteListNewTiqueteToAttach);
            }
            tiqueteListNew = attachedTiqueteListNew;
            usuario.setTiqueteList(tiqueteListNew);
            usuario = em.merge(usuario);
            if (autorizacionidOld != null && !autorizacionidOld.equals(autorizacionidNew)) {
                autorizacionidOld.getUsuarioList().remove(usuario);
                autorizacionidOld = em.merge(autorizacionidOld);
            }
            if (autorizacionidNew != null && !autorizacionidNew.equals(autorizacionidOld)) {
                autorizacionidNew.getUsuarioList().add(usuario);
                autorizacionidNew = em.merge(autorizacionidNew);
            }
            for (Tiquete tiqueteListNewTiquete : tiqueteListNew) {
                if (!tiqueteListOld.contains(tiqueteListNewTiquete)) {
                    Usuario oldUsuariocedulaOfTiqueteListNewTiquete = tiqueteListNewTiquete.getUsuariocedula();
                    tiqueteListNewTiquete.setUsuariocedula(usuario);
                    tiqueteListNewTiquete = em.merge(tiqueteListNewTiquete);
                    if (oldUsuariocedulaOfTiqueteListNewTiquete != null && !oldUsuariocedulaOfTiqueteListNewTiquete.equals(usuario)) {
                        oldUsuariocedulaOfTiqueteListNewTiquete.getTiqueteList().remove(tiqueteListNewTiquete);
                        oldUsuariocedulaOfTiqueteListNewTiquete = em.merge(oldUsuariocedulaOfTiqueteListNewTiquete);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getCedula();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getCedula();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Tiquete> tiqueteListOrphanCheck = usuario.getTiqueteList();
            for (Tiquete tiqueteListOrphanCheckTiquete : tiqueteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Tiquete " + tiqueteListOrphanCheckTiquete + " in its tiqueteList field has a non-nullable usuariocedula field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Autorizacion autorizacionid = usuario.getAutorizacionid();
            if (autorizacionid != null) {
                autorizacionid.getUsuarioList().remove(usuario);
                autorizacionid = em.merge(autorizacionid);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
