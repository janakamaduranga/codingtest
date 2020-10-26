package com.wiley.test.employee.service.testemployeeservice.api;

import com.wiley.test.common.EmployeeDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This is just a dummy implementation, just to show the usage
 * of the case in portal, this should persist to db in real scenario.
 * just for testing purpose record is kept in a map
 */
@RestController
public class EmployeeAPI {

    private Map<String, EmployeeDto> employeeDtoMap = new HashMap<>();

    //This is just dummy impl
    @GetMapping(path = "employee/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable("id") String empId) {
        EmployeeDto employeeDto = employeeDtoMap.get(empId);
        if (employeeDto != null) {
            return new ResponseEntity<>(employeeDto, HttpStatus.OK);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "employee")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        employeeDtoMap.put(employeeDto.getId(), employeeDto);
        return new ResponseEntity<>(employeeDto, HttpStatus.OK);
    }

}
