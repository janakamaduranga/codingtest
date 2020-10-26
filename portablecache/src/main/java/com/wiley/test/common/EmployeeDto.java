package com.wiley.test.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmployeeDto {
    private String id;
    private String name;
    private String address;
    private int age;

}
