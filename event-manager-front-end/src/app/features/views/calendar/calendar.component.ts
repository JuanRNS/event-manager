import { Component, LOCALE_ID, OnInit } from '@angular/core';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import {
  CalendarA11y,
  CalendarDateFormatter,
  CalendarEvent,
  CalendarEventTitleFormatter,
  CalendarModule,
  CalendarUtils,
  CalendarView,
  DateAdapter,
} from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { CustomDateFormatter } from '../../../core/custom/custom-date-formatter.custom';
import { ApiService } from '../../services/api.service';
import { Router } from "@angular/router";

@Component({
  selector: 'app-calendar',
  imports: [CalendarModule],
  providers: [
    {
      provide: DateAdapter,
      useFactory: adapterFactory,
    },
    {
      provide: CalendarDateFormatter,
      useClass: CustomDateFormatter,
    },
    {
      provide: CalendarUtils,
    },
    {
      provide: CalendarA11y,
    },
    {
      provide: CalendarEventTitleFormatter,
    },
    {
      provide: LOCALE_ID,
      useValue: 'pt-BR',
    },
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: { appearance: 'outline', floatLabel: 'always' },
    },
  ],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.scss',
})
export class CalendarComponent implements OnInit {
  public view: CalendarView = CalendarView.Month;
  public viewDate: Date = new Date();
  public events: CalendarEvent[] = [];
  CalendarView = CalendarView;

  constructor(
    private readonly _service: ApiService,
    private readonly _route: Router
  ) { }

  ngOnInit(): void {
    this.getEvents();
  }


  public setView(view: CalendarView) {
    this.view = view;
  }

  public dayClicked({
    date,
    events,
  }: {
    date: Date;
    events: CalendarEvent[];
  }): void {
    if (this.view === CalendarView.Month) {
      this.viewDate = date;
      this.view = CalendarView.Day;
    }
  }

  public getEvents(): void {
    this._service.getEventsCalendar().subscribe({
      next: (events) => {
        this.events = events.map((event) => ({
          start: new Date(event.date),
          title: event.location + ' - ' + event.nameClient,
          end: new Date(event.hourEnd),
        }));
      },
      error: (err) => {
        console.error('Erro ao carregar eventos do calendário:', err);
      },
    });
  }

  public back() {
    this._route.navigate(['/dashboard']);
  }
}
