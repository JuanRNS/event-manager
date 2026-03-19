import { Component, OnInit, signal, computed } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { ICalendarEvent } from '../../../core/interface/calendar.interface';
import { EventDayCardComponent } from '../../../core/components/event-day-card/event-day-card.component';

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [DatePipe, EventDayCardComponent],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.scss',
})
export class CalendarComponent implements OnInit {

  public allEvents = signal<ICalendarEvent[]>([]);
  public selectedDate = signal<Date>(new Date());
  public currentMonth = signal<Date>(new Date());

  public readonly WEEKDAYS = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
  public readonly MONTHS = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro',
  ];

  public today = new Date();

  /** Grade de dias do mês visível: null = célula vazia antes do 1º dia */
  public calendarDays = computed<(Date | null)[]>(() => {
    const ref = this.currentMonth();
    const year = ref.getFullYear();
    const month = ref.getMonth();
    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    const days: (Date | null)[] = [];
    for (let i = 0; i < firstDay; i++) days.push(null);
    for (let d = 1; d <= daysInMonth; d++) days.push(new Date(year, month, d));
    return days;
  });

  /** Eventos do dia selecionado, ordenados cronologicamente */
  public eventsOfSelectedDay = computed<ICalendarEvent[]>(() => {
    const selected = this.selectedDate();
    return this.allEvents()
      .filter((e) => {
        const eventDate = new Date(e.date).toISOString().split('T')[0];
        return eventDate === selected.toISOString().split('T')[0];
      })
      .sort((a, b) => a.hourStart.localeCompare(b.hourStart));
  });

  /** Conjunto de datas (yyyy-MM-dd) que possuem eventos */
  public datesWithEvents = computed<Set<string>>(() => {
    const set = new Set<string>();
    this.allEvents().forEach((e) => {
      const d = new Date(e.date).toISOString().split('T')[0];
      set.add(d);
    });
    return set;
  });

  constructor(
    private readonly _service: ApiService,
    private readonly _router: Router,
  ) { }

  public ngOnInit(): void {
    this.getEvents();
  }

  public getEvents(): void {
    this._service.getEventsCalendar().subscribe({
      next: (events) => this.allEvents.set(events),
      error: (err) => console.error('Erro ao carregar eventos do calendário:', err),
    });
  }

  public previousMonth(): void {
    const ref = this.currentMonth();
    this.currentMonth.set(new Date(ref.getFullYear(), ref.getMonth() - 1, 1));
  }

  public nextMonth(): void {
    const ref = this.currentMonth();
    this.currentMonth.set(new Date(ref.getFullYear(), ref.getMonth() + 1, 1));
  }

  public selectDay(day: Date | null): void {
    if (!day) return;
    this.selectedDate.set(day);
  }

  public isSelected(day: Date | null): boolean {
    if (!day) return false;
    const s = this.selectedDate();
    return (
      day.getFullYear() === s.getFullYear() &&
      day.getMonth() === s.getMonth() &&
      day.getDate() === s.getDate()
    );
  }

  public isToday(day: Date | null): boolean {
    if (!day) return false;
    const t = this.today;
    return (
      day.getFullYear() === t.getFullYear() &&
      day.getMonth() === t.getMonth() &&
      day.getDate() === t.getDate()
    );
  }

  public hasEvent(day: Date | null): boolean {
    if (!day) return false;
    return this.datesWithEvents().has(
      day.toISOString().split('T')[0]
    );
  }

  public goToToday(): void {
    const today = new Date();
    this.currentMonth.set(new Date(today.getFullYear(), today.getMonth(), 1));
    this.selectedDate.set(today);
  }

  public onViewDetails(event: ICalendarEvent): void {
    this._router.navigate(['/party-list'], { queryParams: { id: event.id } });
  }

  public onEditEvent(event: ICalendarEvent): void {
    this._router.navigate(['/party-list'], { queryParams: { id: event.id, edit: true } });
  }

  public back(): void {
    this._router.navigate(['/dashboard']);
  }
}
