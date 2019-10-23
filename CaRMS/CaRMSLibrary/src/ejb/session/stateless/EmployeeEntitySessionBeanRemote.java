/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.EmployeeEntity;
import java.util.List;
import util.exception.DeleteStaffException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateStaffException;

public interface EmployeeEntitySessionBeanRemote {
    
    public Long createNewEmployee(EmployeeEntity newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException;

    public List<EmployeeEntity> retrieveAllEmployees();

    public EmployeeEntity retrieveEmployeeByEmployeeId(Long employeeId) throws StaffNotFoundException;

    public EmployeeEntity retrieveEmployeeByUsername(String username) throws StaffNotFoundException;

    public EmployeeEntity employeeLogin(String username, String password) throws InvalidLoginCredentialException;

    public void updateEmployee(EmployeeEntity employeeEntity) throws StaffNotFoundException, UpdateStaffException;

    public void deleteEmployee(Long staffId) throws StaffNotFoundException, DeleteStaffException;
}
