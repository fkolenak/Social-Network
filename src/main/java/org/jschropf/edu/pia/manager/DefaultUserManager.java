package org.jschropf.edu.pia.manager;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.jschropf.edu.pia.dao.UserDao;
import org.jschropf.edu.pia.domain.User;
import org.jschropf.edu.pia.domain.UserValidationException;
import org.jschropf.edu.pia.utils.Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Date: 26.11.15
 *
 * @author Jakub Danek
 */
@Service
@Transactional
public class DefaultUserManager implements UserManager {


    private UserDao userDao;
	
    private Encoder encoder;

    @Autowired
    public DefaultUserManager(UserDao userDao, Encoder encoder) {
        this.userDao = userDao;
        this.encoder = encoder;
    }
    
    @Override
    public boolean authenticate(String username, String password) {
        User u = userDao.findByUsername(username);
        System.out.println(u);
        return u != null && encoder.validate(password, u.getPassword());
    }

    @Override
    public void register(User newUser) throws UserValidationException {
        if(!newUser.isNew()) {
            throw new RuntimeException("User already exists, use save method for updates!");
        }

        newUser.validate();
        User existinCheck = userDao.findByUsername(newUser.getUsername());
        
        if(existinCheck != null) {
            throw new UserValidationException("Username already taken!");
        }

        newUser.setPassword(encoder.encode(newUser.getPassword()));

        System.out.println("Saving user");
        try {
            userDao.save(newUser);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while saving user");
        }
        System.out.println("User saved");
    }
    
    @Override
    public void updatePassword(String password, User user){
    	String pass = encoder.encode(password);
    	System.out.println("encoded password: "+pass);
    	
    	try {
            userDao.updatePassword(user.getId(), pass);
        } catch (Exception e) {
            System.out.println("Couldn't update password");
        }
    }
    
    @Override
    public void updatePicture(User user, String filename){
        try {
            if(userDao.updatePicture(user.getId(), filename))
            	System.out.println("Update done");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    @Override
    public long userIdFinder(String username){
    	User u = userDao.findByUsername(username);
    	System.out.println(u);
    	
    	return u.getId();
    }
    
    @Override
    public List<User> findAllSortedByDateOfBirth(boolean order){
    	List<User> u = userDao.findAllSortedByDateOfBirth(order);
    	System.out.println(u);
    	
    	return u;
    }
    
    @Override
    public List<User> findAllSortedByName(boolean order){
    	List<User> u = userDao.findAllSortedByName(order);
    	System.out.println(u);
    	
    	return u;
    }
    
    @Override
    public User findByUsername(String username){
    	User user = userDao.findByUsername(username);
    	
    	return user;
    }
    
    @Override
    public List<User> friendsSortedByDateOfBirth(Long personId, boolean order){
    	List<User> friends = userDao.friendsSortedByDateOfBirth(personId, order);
    	
    	return friends;
    }
    
    @Override
    public List <User> friendsSortedByName(Long personId, boolean order){
    	List<User> friends = userDao.friendsSortedByName(personId, order);
    	
    	return friends;
    }
    
    @Override
    public List<User> nonFriendsFor(Long personId){
    	List<User> nonFriends = userDao.nonFriendsFor(personId);
    	
    	return nonFriends;
    }
    
    @Override
    public List<User> unansweredFriendRequestsFor(Long personId){
    	List<User> unanswered = userDao.unansweredFriendRequestsFor(personId);
    	
    	return unanswered;
    }
    
    @Override
    public boolean updatePersonalInformation(Long personId, String firstName, String lastName, String dayOfBirth, String monthOfBirth, String yearOfBirth){
    	
    	return(userDao.updatePersonalInformation(personId, firstName, lastName, dayOfBirth, monthOfBirth, yearOfBirth));
    }
    
    @Override
    public boolean updatePersonalInformation(Long personId, String firstName, String lastName, Date date){
    	
    	return(userDao.updatePersonalInformation(personId, firstName, lastName, date));
    }
    
    @Override
    public User findById(Long personId){
    	return userDao.findById(personId);
    }
}
