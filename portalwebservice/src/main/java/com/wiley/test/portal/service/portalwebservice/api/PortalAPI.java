package com.wiley.test.portal.service.portalwebservice.api;

import com.wiley.test.common.EmployeeDto;
import com.wiley.test.portal.service.portalwebservice.service.PortalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@Slf4j
public class PortalAPI {

    @Autowired
    private PortalService portalService;

    //TODO;add hystrix
    @GetMapping(path = "employee/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable("id") String empId) {
        log.info("Start executing get by id: {}", empId);
        /**
         * first try to read from the cache if not go to employee service and read
         */
        EmployeeDto loadedEmployeeDto = portalService.getEmployeeDtoByIdFromCache(empId);
        if (loadedEmployeeDto != null) {
            return new ResponseEntity<>(loadedEmployeeDto, HttpStatus.OK);
        }
        log.info("record not found in cache, emp id {}", empId);
        loadedEmployeeDto = portalService.getEmployeeDtoByIdFromEmployeeService(empId);
        if (loadedEmployeeDto != null) {
            log.info("record found in employee service");
            return new ResponseEntity<>(loadedEmployeeDto, HttpStatus.OK);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "employee", consumes = "application/json", produces = "application/json")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) throws URISyntaxException {
        log.info("Start executing createEmployee: {}", employeeDto.getId());
        EmployeeDto createdEmpDto = portalService.createEmployeeDtoInEmployeeService(employeeDto);
        if (createdEmpDto != null) {
            createdEmpDto = portalService.createEmployeeDtoInCache(createdEmpDto);
            if (createdEmpDto == null) {
                log.warn("employee id : {} is not added to cache", employeeDto.getId());
            }
            return new ResponseEntity<>(employeeDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path = "/")
    public String test() {
        return "connected";
    }
}
