import {HttpHeaders} from "@angular/common/http";

export const apiUrl = 'http://localhost:8080';

export const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/x-www-form-urlencoded',
    'Authorization': 'Basic ZmluYW5jZS1zZXJ2aWNlOnNlY3JldA==',
    'Cache-Control': 'no-cache'
  })
};
