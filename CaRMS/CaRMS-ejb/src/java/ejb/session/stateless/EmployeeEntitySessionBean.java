/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.DeleteStaffException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateStaffException;

/**
 *
 * @author Zheng Yang
 */
@Stateless
@Local(EmployeeEntitySessionBeanLocal.class)
@Remote(EmployeeEntitySessionBeanRemote.class)

public class EmployeeEntitySessionBean implements EmployeeEntitySessionBeanRemote, EmployeeEntitySessionBeanLocal {

    @PersistenceContext(unitName = "CaRMS-ejbPU")
    private EntityManager em;

    public EmployeeEntitySessionBean() {
    }
    
    @Override
    public Long createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException
    {
        try
        {
            em.persist(newEmployeeEntity);
            em.flush();

            return newEmployeeEntity.getEmployeeId();
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new EmployeeUsernameExistException();
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
    }


    @Override
    public List<EmployeeEntity> retrieveAllEmployees()
    {
        Query query = em.createQuery("SELECT e FROM EmployeeEntity e");
        
        return query.getResultList();
    }
    
    @Override
    public EmployeeEntity retrieveEmployeeByEmployeeId(Long employeeId) throws StaffNotFoundException
    {
        EmployeeEntity employeeEntity = em.find(EmployeeEntity.class, employeeId);
        
        if(employeeEntity != null)
        {
            return employeeEntity;
        }
        else
        {
            throw new StaffNotFoundException("Employee ID " + employeeId + " does not exist!");
        }
    }
    
    @Override
    public EmployeeEntity retrieveEmployeeByUsername(String username) throws StaffNotFoundException
    {
        Query query = em.createQuery("SELECT e FROM EmployeeEntity e WHERE e.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try
        {
            return (EmployeeEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new StaffNotFoundException("Employee Username " + username + " does not exist!");
        }
    }
    
    
    
    @Override
    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            EmployeeEntity employeeEntity = retrieveEmployeeByUsername(username);
            
            if(employeeEntity.getPassword().equals(password))
            {               
                return employeeEntity;
            }
            else
            {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        }
        catch(StaffNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
    @Override
    public void updateEmployee(EmployeeEntity employeeEntity) throws StaffNotFoundException, UpdateStaffException
    {
        if(employeeEntity.getEmployeeId()!= null)
        {
            EmployeeEntity employeeEntityToUpdate = retrieveEmployeeByEmployeeId(employeeEntity.getEmployeeId());
            
            if(employeeEntityToUpdate.getUsername().equals(employeeEntity.getUsername()))
            {
                employeeEntityToUpdate.setFirstName(employeeEntity.getFirstName());
                employeeEntityToUpdate.setLastName(employeeEntity.getLastName());
                employeeEntityToUpdate.setAccessRightEnum(employeeEntity.getAccessRightEnum());                
                // Username and password are deliberately NOT updated to demonstrate that client is not allowed to update account credential through this business method
            }
            else
            {
                throw new UpdateStaffException("Username of employee record to be updated does not match the existing record");
            }
        }
        else
        {
            throw new StaffNotFoundException("Employee ID not provided for staff to be updated");
        }
    }
    
    @Override
    public void deleteEmployee(Long staffId) throws StaffNotFoundException, DeleteStaffException
    {
        EmployeeEntity employeeEntityToRemove = retrieveEmployeeByEmployeeId(staffId);
        
        
        em.remove(employeeEntityToRemove);
        
//        if(staffEntityToRemove.getSaleTransactionEntities().isEmpty())
//        {
//            em.remove(staffEntityToRemove);
//        }
//        else
//        {
//            // New in v4.1 to prevent deleting staff with existing sale transaction(s)
//            throw new DeleteStaffException("Staff ID " + staffId + " is associated with existing sale transaction(s) and cannot be deleted!");
//        }
    }
}
