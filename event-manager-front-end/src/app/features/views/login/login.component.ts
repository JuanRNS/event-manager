import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, FormsModule, Validators } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { FormGroupArray } from '../../../core/interface/form.interface';
import { FormFieldEnum } from '../../../core/enums/formFieldEnum';
import { FormComponent } from "../../../core/components/form-group/form/form.component";
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../services/user.service';
import { IRequestLogin } from '../../../core/interface/login.interface';
import { ToastService } from '../../../core/services/toast.service';
import { HomePageSideComponent } from "../../../core/components/home-page-side/home-page-side.component";
import { AuthGoogle } from '../../services/auth-google.service';
import { MaskEnum } from '../../../core/enums/maskEnum';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    FormComponent,
    MatButtonModule,
    RouterLink,
    HomePageSideComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {

  public isGoogleButtonLoaded = false;

  public formGroup = new FormGroup({
    userName: new FormControl<string | null>(null, [Validators.required]),
    password: new FormControl<string | null>(null, [Validators.required]),
  })

  constructor(
    private readonly router: Router,
    private readonly userService: UserService,
    private readonly _toast: ToastService,
    private readonly _authGoogle: AuthGoogle
  ) { }

  ngOnInit(): void {
    this._authGoogle.initializeGoogleSignIn('google-signin-button')
      .then(() => this.isGoogleButtonLoaded = true)
      .catch(() => this.isGoogleButtonLoaded = true);
  }

  public login() {
    if (localStorage.getItem('token')) {
      localStorage.removeItem('token');
    }
    if (this.formGroup.invalid) {
      this.formGroup.markAllAsDirty();
      this.formGroup.updateValueAndValidity();
      this.formGroup.markAllAsTouched();
      return;
    }
    const form: IRequestLogin = {
      username: this.formGroup.controls.userName.value as string,
      password: this.formGroup.controls.password.value as string
    }

    this.userService.userLogin(form).subscribe({
      next: (value) => {
        localStorage.setItem('token', value);
        this.router.navigate(['dashboard']);
      },
      error: (err) => {
        const error = JSON.parse(err.error);
        this._toast.error(error.message);
      }
    })
  }

  public get formGroupItens(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Usuário',
        controlName: 'userName',
        type: 'text',
        size: '12',
        mask: MaskEnum.ALPHANUMERIC
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Senha',
        controlName: 'password',
        type: 'password',
        size: '12',
      }
    ]
  }
}

