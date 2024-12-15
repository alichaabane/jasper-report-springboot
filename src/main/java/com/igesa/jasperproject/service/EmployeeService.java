package com.igesa.jasperproject.service;

import com.igesa.jasperproject.model.Employee;
import net.sf.jasperreports.engine.JRException;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeById(Long id);

    String exportReport(String reportFormat) throws FileNotFoundException, JRException;
}
