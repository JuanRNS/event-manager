import { HttpClient } from '@angular/common/http';
import { Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { ToastService } from '../../core/services/toast.service';

declare const google: any;

declare global {
  interface Window {
    handleCredentialResponse: (response: any) => void;
  }
}

@Injectable({
  providedIn: 'root',
})
export class AuthGoogle {
  constructor(
    private readonly _http: HttpClient,
    private readonly _router: Router,
    private readonly _zone: NgZone,
    private readonly _toast: ToastService
  ) {
    window.handleCredentialResponse = this.handleCredentialResponse.bind(this);
  }

  public async initializeGoogleSignIn(containerId: string): Promise<void> {
    return this.waitForGoogleScript().then(() => {
      google.accounts.id.initialize({
        client_id: environment.googleClientId,
        callback: this.handleCredentialResponse.bind(this)
      });

      google.accounts.id.renderButton(
        document.getElementById(containerId),
        { theme: "outline", size: "large" }
      );
    });
  }

  private waitForGoogleScript(): Promise<void> {
    const MAX_WAIT_MS = 5000;
    const INTERVAL_MS = 100;

    return new Promise((resolve, reject) => {
      if (typeof google !== 'undefined') {
        resolve();
        return;
      }

      let elapsed = 0;
      const interval = setInterval(() => {
        elapsed += INTERVAL_MS;

        if (typeof google !== 'undefined') {
          clearInterval(interval);
          resolve();
        } else if (elapsed >= MAX_WAIT_MS) {
          clearInterval(interval);
          reject(new Error('Timeout ao carregar o script do Google Sign-In'));
        }
      }, INTERVAL_MS);
    });
  }

  private handleCredentialResponse(response: any) {
    this._zone.run(() => {
      const idToken = response.credential;
      this.sendTokenToBackend(idToken);
    });
  }

  private sendTokenToBackend(idToken: string) {
    this._http.post<string>(`${environment.api}auth/google`, { token: idToken }, { responseType: 'text' as 'json' })
      .subscribe({
        next: (myJwt) => {
          localStorage.setItem('token', myJwt);
          this._router.navigate(['dashboard']);
        },
        error: (err) => {
          this._toast.error('Falha na autenticação Google:', err);
        }
      });
  }
}
