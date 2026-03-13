import { Routes } from '@angular/router';
import { LoginComponent } from './features/views/login/login.component';
import { authGuard } from './core/guards/auth.guard';
import { RegisterComponent } from './features/views/register/register.component';


export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/views/dashboard-week/dashboard-week.component').then((m) => m.DashboardWeekComponent),
    canActivate: [authGuard]
  },
  {
    path: 'event-components',
    loadComponent: () => import('./features/views/event-components/event-components.component').then((m) => m.EventComponentsComponent),
    canActivate: [authGuard]
  },
  {
    path: 'party-list',
    loadComponent: () => import('./features/views/party-all-list/party-all-list.component').then((m) => m.PartyAllListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'calendar',
    loadComponent: () => import('./features/views/calendar/calendar.component').then((m) => m.CalendarComponent),
    canActivate: [authGuard]
  },
  {
    path: 'provider-parties',
    loadComponent: () => import('./features/views/provider-parties/provider-parties.component').then((m) => m.ProviderPartiesComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  },
];


