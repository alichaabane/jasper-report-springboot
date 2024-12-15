package com.igesa.jasperproject.service.impl;

import com.igesa.jasperproject.model.Employee;
import com.igesa.jasperproject.repository.EmployeeRepository;
import com.igesa.jasperproject.service.EmployeeService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Value("${report.export.path}")
    private String reportPath;

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public String exportReport(String reportFormat) throws FileNotFoundException, JRException {
        String path = "C:\\Users\\mss-g\\OneDrive\\Bureau\\reports";
        List<Employee> employees = employeeRepository.findAll();

        // Load and compile the JRXML file
        File file = ResourceUtils.getFile("classpath:reports/employees.jrxml");
        System.out.println("Loading file: " + file.getAbsolutePath());
        InputStream reportStream = new FileInputStream(file);
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Create DataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Java Techie");

        // Fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export report
        if (reportFormat.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "\\employees.html");
        } else if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\employees.pdf");
        }

        return "Report generated in path: " + path;
    }
}
