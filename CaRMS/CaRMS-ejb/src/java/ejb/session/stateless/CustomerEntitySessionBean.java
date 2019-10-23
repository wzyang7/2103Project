/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import javax.ejb.Stateless;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CustomerEmailExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author Zheng Yang
 */
@Stateless
@Local(CustomerEntitySessionBeanLocal.class)
@Remote(CustomerEntitySessionBeanRemote.class)

public class CustomerEntitySessionBean implements CustomerEntitySessionBeanRemote, CustomerEntitySessionBeanLocal {

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    public CustomerEntitySessionBean() 
    {
        
    }

    @Override
    public Long createNewCustomer(CustomerEntity newCustomerEntity) throws CustomerEmailExistException, UnknownPersistenceException
    {
        try
        {
            em.persist(newCustomerEntity);
            em.flush();
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new CustomerEmailExistException();
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
        
        return newCustomerEntity.getCustomerId();
    }
    
    @Override
    public CustomerEntity retrieveCustomerByCustomerId(Long customerId) throws CustomerNotFoundException
    {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        
        if(customerEntity != null)
        {
            return customerEntity;
        }
        else
        {
            throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist!");
        }
    }
    
    @Override
    public CustomerEntity retrieveCustomerByCustomerEmail(String email) throws CustomerNotFoundException
    {
        Query query = em.createQuery("SELECT g FROM CustomerEntity g WHERE g.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try
        {
            return (CustomerEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CustomerNotFoundException("Customer email " + email + " does not exist!");
        }
    }
    
    @Override
    public CustomerEntity customerLogin(String email,String password) throws InvalidLoginCredentialException
    {
       try
       {
           CustomerEntity newCustomer = retrieveCustomerByCustomerEmail(email);
           if(newCustomer.getPassword().equals(password))
           {
              //newCustomer.getOnlineReservations().size();
              return newCustomer;
           }
           else 
           {
               throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
           }
       }
       catch(CustomerNotFoundException ex)
       {
            throw new InvalidLoginCredentialException("Email does not exist or invalid password!");
       }
        
    }
}
