import { Injectable } from '@angular/core';
import { Headers,Http, RequestOptions,Response } from '@angular/http';
import 'rxjs/Rx';
import { EmployeeDto } from './EmployeeDto';

@Injectable()
export class ServerService
{
    constructor(private http:Http)
    {

    }

    storeEmployee(employee:EmployeeDto){
        const header:Headers = new Headers({
            'Content-Type':'application/json',
            'Access-Control-Allow-Origin':'*'
        });
        return this.http.post("http://localhost:8899/portalwebservice/employee",employee,
        {headers:header}
        );
    }

    getEmployee(id:string)
{
    const url:string = "http://localhost:8899/portalwebservice/employee/".concat(id);
    return this.http.get(url)
    .map((response:Response)=>{
        console.log(response);
        return response.json();
    });
}

}