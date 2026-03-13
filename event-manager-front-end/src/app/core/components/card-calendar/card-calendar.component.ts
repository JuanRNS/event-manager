import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { IPartyByWaiter } from '../../interface/modal-view-party-waiter.interface';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-card-calendar',
  standalone: true,
  imports: [MatIconModule, DatePipe],
  templateUrl: './card-calendar.component.html',
  styleUrl: './card-calendar.component.scss',
})
export class CardCalendarComponent {
  public party = input.required<IPartyByWaiter>();

  public getStatusClass(): string {
    const status = this.party().status?.toLowerCase();
    if (status === 'realizada' || status === 'confirmed') return 'status-confirmed';
    if (status === 'cancelada' || status === 'cancelled') return 'status-cancelled';
    if (status === 'pendente' || status === 'pending') return 'status-pending';
    return 'status-default';
  }
}
