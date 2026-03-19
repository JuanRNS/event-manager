import { Component, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { ICalendarEvent } from '../../interface/calendar.interface';

@Component({
  selector: 'app-event-day-card',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './event-day-card.component.html',
  styleUrl: './event-day-card.component.scss',
})
export class EventDayCardComponent {
  public event = input.required<ICalendarEvent>();

  public viewDetails = output<ICalendarEvent>();
  public editEvent = output<ICalendarEvent>();

  public getStatusClass(): string {
    const status = this.event().status?.toLowerCase() ?? '';
    if (status === 'realizada' || status === 'confirmada' || status === 'confirmed') return 'status-confirmed';
    if (status === 'cancelada' || status === 'cancelled') return 'status-cancelled';
    if (status === 'pendente' || status === 'pending') return 'status-pending';
    return 'status-default';
  }

  public getStatusLabel(): string {
    const status = this.event().status?.toLowerCase() ?? '';
    if (status === 'realizada' || status === 'confirmada' || status === 'confirmed') return 'Confirmado';
    if (status === 'cancelada' || status === 'cancelled') return 'Cancelado';
    if (status === 'pendente' || status === 'pending') return 'Pendente';
    return this.event().status ?? 'Indefinido';
  }

  public onViewDetails(): void {
    this.viewDetails.emit(this.event());
  }

  public onEditEvent(): void {
    this.editEvent.emit(this.event());
  }
}
