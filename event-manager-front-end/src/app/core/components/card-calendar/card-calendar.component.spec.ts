import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardCalendarComponent } from './card-calendar.component';

describe('CardCalendarComponent', () => {
  let component: CardCalendarComponent;
  let fixture: ComponentFixture<CardCalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardCalendarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardCalendarComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('party', {
      location: 'Salão',
      nameClient: 'Cliente',
      date: '2026-10-25',
      hourStart: '18:00',
      hourEnd: '23:00',
      valuePerDay: 100,
      status: 'AGENDADA',
      numberOfPeople: 50,
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
