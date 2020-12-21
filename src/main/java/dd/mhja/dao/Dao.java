package dd.mhja.dao;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Dao<T, U> {
    static protected final Logger LOG = LoggerFactory.getLogger(Dao.class);

    private final Class<T> clazz;

    // workaround for template type erasure
    public Dao(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> readAll() {
        EntityManager em = null;

        try {
            em = JpaUtil.getEntityManager();
            String jpql = "SELECT e FROM " + clazz.getName() + " e";
            return em.createQuery(jpql, clazz).getResultList(); // si crea il comando SQL necessario
        } catch (Exception ex) {
            LOG.error("Can't create query: " + ex.getMessage());
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Optional<T> read(U id) {
        EntityManager em = null;

        try {
            em = JpaUtil.getEntityManager();
            T t = em.find(clazz, id); // si usa il metodo direttamente di EntityManager
            return Optional.ofNullable(t);
        } catch (Exception ex) {
            LOG.error("Can't create query: " + ex.getMessage());
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean create(T entity) {
        EntityManager em = null;

        try {
            LOG.trace("enter");
            em = JpaUtil.getEntityManager();
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.persist(entity);
            et.commit();
            return true;
        } catch (Exception ex) {
            LOG.warn("Can't persist entity", ex);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean contains(T entity) {
        EntityManager em = null;

        try {
            em = JpaUtil.getEntityManager();
            em.refresh(entity);
            return em.contains(entity);
        } catch (Exception ex) {
            LOG.warn("Can't check entity", ex);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean update(T entity) {
        EntityManager em = null;

        try {
            em = JpaUtil.getEntityManager();
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.merge(entity);
            et.commit();
            return true;
        } catch (Exception ex) {
            LOG.warn("Can't merge entity", ex);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean delete(U id) {
        EntityManager em = null;

        try {
            em = JpaUtil.getEntityManager();
            EntityTransaction et = em.getTransaction();
            et.begin();
            T entity = em.find(clazz, id);
            if (entity != null) {
                em.remove(entity);
                et.commit();
                return true;
            } else {
                LOG.info("Can't remove missing entity " + id);
                et.rollback();
                return false;
            }
        } catch (Exception ex) {
            LOG.warn("Can't remove entity " + id, ex);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
