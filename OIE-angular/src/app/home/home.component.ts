import { Route } from '@angular/compiler/src/core';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  idToken: any;
  accessToken: any;
  constructor(
    private router: Router,
    private authService: AuthService) { }

  ngOnInit(): void {
    this.accessToken = localStorage.getItem('accessToken')
    this.idToken = localStorage.getItem('idToken')
  }

  logout(){
    this.authService.logout(this.accessToken).subscribe((x:any) => {
      localStorage.clear();
      this.router.navigateByUrl('/login')
    })
  }

}
