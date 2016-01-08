package org.jschropf.edu.pia.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.jschropf.edu.pia.domain.User;
import org.springframework.stereotype.Repository;

/**
 * Date: 26.11.15
 *
 * @author Jakub Danek
 */
@Repository
public class UserDaoJpa extends GenericDaoJpa<User> implements UserDao, Serializable {
    private static final long serialVersionUID = 1L;
    public UserDaoJpa(EntityManager em) {
        super(em, User.class);
    }

    @Override
    public User findByUsername(String username) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.username = :uname", User.class);
        q.setParameter("uname", username);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            //no result found, ensuring the behaviour described by interface specification
            //see javadoc of the findByUsername method.
            return null;
        }
    }
    
    @Override
    public User findById(long id) {
        TypedQuery<User> q = em.createQuery("SELECT u FROM User u WHERE u.id = :uid", User.class);
        q.setParameter("uid", id);
       
        try {
        	User temp = q.getSingleResult();
            return temp;
        } catch (NoResultException e) {
        	System.out.println("No user with id: "+id+" was found");
            //no result found, ensuring the behaviour described by interface specification
            //see javadoc of the findByUsername method.
            return null;
        }
    }
    
    @Override
    public List<User> unansweredFriendRequestsFor(Long personId) {
        Query q = em.createQuery("SELECT u FROM User u WHERE EXISTS (SELECT f FROM FriendRequest f WHERE f.targetId=:targetId AND f.status='UNANSWERED' AND u.id=f.sourceId)");
        q.setParameter("targetId", personId);
        return q.getResultList();
      } 
    
    @Override
    public List<User> friendsFor(Long personId) {
        Query q = em.createQuery("SELECT u FROM User u WHERE EXISTS (SELECT f FROM FriendRequest f WHERE ((f.sourceId=:personId AND f.targetId=u.id) OR (f.sourceId=u.id AND f.targetId=:personId)) AND f.status='ACCEPTED')");
        q.setParameter("personId", personId);
        return q.getResultList();
    }
    
    @Override
    public List<User> friendsSortedByName(Long personId, boolean isAscending) {
	String order = isAscending ? "ASC" : "DESC";
        Query q = em.createQuery("SELECT u FROM User u WHERE EXISTS (SELECT f FROM FriendRequest f WHERE ((f.sourceId=:personId AND f.targetId=u.id) OR (f.sourceId=u.id AND f.targetId=:personId)) AND f.status='ACCEPTED') ORDER BY u.fName " + order + ", u.lName " + order );
        q.setParameter("personId", personId);
        return q.getResultList();
    }
    
    @Override
    public List<User> friendsSortedByDateOfBirth(Long personId, boolean isAscending) {
	String order = isAscending ? "ASC" : "DESC";
        Query q = em.createQuery("SELECT u FROM User u WHERE EXISTS (SELECT f FROM FriendRequest f WHERE ((f.sourceId=:personId AND f.targetId=u.id) OR (f.sourceId=u.id AND f.targetId=:personId)) AND f.status='ACCEPTED') ORDER BY u.dateOfBirth " + order);
        q.setParameter("personId", personId);
        return q.getResultList();
    } 
    
    @Override
    public List<User> nonFriendsFor(Long personId) {
        Query q = em.createQuery("SELECT u FROM User u WHERE u.id<>:personId AND NOT EXISTS (SELECT f FROM FriendRequest f WHERE ((f.sourceId=:personId AND f.targetId=u.id) OR (f.sourceId=u.id AND f.targetId=:personId)) AND f.status='ACCEPTED')");
        q.setParameter("personId", personId);
        return q.getResultList();
    } 
    
    @Override
    public boolean updatePicture(Long personId, String filename) {
        System.out.println("associating picture with user");
        startTransaction();
        try{
        	Query q = em.createQuery("UPDATE User u SET u.picture=:filename WHERE u.id=:personId");
        	q.setParameter("filename", filename);
        	q.setParameter("personId", personId);
	        q.executeUpdate();
	    }catch(Exception e)
        {
	    	rollbackTransaction();
        }
	    commitTransaction();
        
        return true;
    }
    
    @Override
    public List<User> findAllSortedByName(boolean isAscending) {
    	String order = isAscending ? "ASC" : "DESC";
    	Query q = em.createQuery("SELECT u FROM User u ORDER BY u.fName " + order + ", u.lName " + order);
    	return q.getResultList();
    }

    @Override
    public List<User> findAllSortedByDateOfBirth(boolean isAscending) {
    	String order = isAscending ? "ASC" : "DESC";
    	Query q = em.createQuery("SELECT u FROM User u ORDER BY p.birthDate " + order);
    	return q.getResultList();
     } 
}
