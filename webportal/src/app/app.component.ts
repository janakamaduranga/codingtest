import { Component, ElementRef, OnInit } from '@angular/core';
import { NgForm, FormGroup, FormControl, Validators, FormArray } from '@angular/forms';
import { Observable } from 'rxjs';
import { ServerService } from './server.service';
import {EmployeeDto} from './EmployeeDto';
import {Response} from '@angular/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  ngOnInit(): void {
    this.employee = new EmployeeDto();
  }

  employee:EmployeeDto ;

  constructor(private service:ServerService)
  {

  }
  onSubmit()
  {
    if(this.employee.id == null || this.employee.address == null ||
      this.employee.name == null || this.employee.age == null){
          alert("All fields are requied");
          return;
      }
    this.service
    .storeEmployee(this.employee)
    .subscribe((response)=>{
      alert("Record Saved")
      console.log(response)},
    (error)=>{console.log('error  ddddddddd'+error)});
  }

  onGet()
  {
    this.service.getEmployee(this.employee.id).subscribe(
      (employeeDto:EmployeeDto)=>{
        if(employeeDto == null){
          employeeDto = new EmployeeDto();
        }
        this.employee = employeeDto;
          console.log(this.employee.name+' '+this.employee.address+' '+this.employee.age)
        
      },
      (error)=>{
        console.log(error);
      }
    );
  }

  onClear(){
    this.employee = new EmployeeDto();
  }
  
}

