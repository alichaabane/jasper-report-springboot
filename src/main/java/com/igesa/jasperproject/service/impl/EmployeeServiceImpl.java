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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        String path = reportPath;
        List<Employee> employees = employeeRepository.findAll();

        // Load and compile the JRXML file
        File file = ResourceUtils.getFile("classpath:reports/employees.jrxml");
        InputStream reportStream = new FileInputStream(file);
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Create DataSource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(employees);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("createdBy", "Ali");

        // Fill report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export report
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

        if ("html".equalsIgnoreCase(reportFormat)) {
            String timestamp = LocalDateTime.now().format(formatter);
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path + "\\employees_" + timestamp + ".html");
        } else if ("pdf".equalsIgnoreCase(reportFormat)) {
            String timestamp = LocalDateTime.now().format(formatter);
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + "\\employees_" + timestamp + ".pdf");
        }

        return "Report generated in path: " + path;
    }
}
