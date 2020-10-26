package com.wiley.test.portal.service.portalwebservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.wiley.test.common.EmployeeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class PortalService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "employeeDtoLoadingFailure")
    public EmployeeDto getEmployeeDtoByIdFromCache(String empId) {
        URI uri = new UriTemplate("http://portablecache/cache/employee/{id}").expand(empId);
        ResponseEntity<EmployeeDto> employeeResponse = restTemplate
                .getForEntity(uri.toString(), EmployeeDto.class);
        if (employeeResponse.getStatusCodeValue() == HttpStatus.OK.value()
                && employeeResponse.getBody() != null) {
            return employeeResponse.getBody();
        }
        return null;
    }

    @HystrixCommand(fallbackMethod = "employeeDtoLoadingFailure")
    public EmployeeDto getEmployeeDtoByIdFromEmployeeService(String empId) {
        URI uri = new UriTemplate("http://testemployeeservice/employee/{id}").expand(empId);

        ResponseEntity<EmployeeDto> employeeResponse = restTemplate
                .getForEntity(uri.toString(), EmployeeDto.class);
        if (employeeResponse.getStatusCodeValue() == HttpStatus.OK.value()
                && employeeResponse.getBody() != null) {
            log.info("record found in employee service");
            return employeeResponse.getBody();
        }
        return null;
    }

    @HystrixCommand(fallbackMethod = "employeeDtoCreationFailure")
    public EmployeeDto createEmployeeDtoInEmployeeService(EmployeeDto employeeDto) throws URISyntaxException {
        URI uri = new URI("http://testemployeeservice/employee");
        ResponseEntity<EmployeeDto> result = restTemplate.postForEntity(uri, employeeDto, EmployeeDto.class);
        if (result.getStatusCodeValue() == HttpStatus.OK.value()) {
            return employeeDto;
        }
        return null;
    }

    @HystrixCommand(fallbackMethod = "employeeDtoCreationFailure")
    public EmployeeDto createEmployeeDtoInCache(EmployeeDto employeeDto) throws URISyntaxException {
        URI uri = new URI("http://portablecache/cache/employee");
        restTemplate.postForEntity(uri, employeeDto, EmployeeDto.class);
        log.info("added to cache : {}", employeeDto.getId());
        return employeeDto;
    }

    private EmployeeDto employeeDtoLoadingFailure(String empId) {
        return null;
    }

    private EmployeeDto employeeDtoCreationFailure(EmployeeDto employeeDto) {
        return null;
    }
}
