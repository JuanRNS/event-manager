import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardWeekComponent } from './dashboard-week.component';

describe('DashboardWeekComponent', () => {
  let component: DashboardWeekComponent;
  let fixture: ComponentFixture<DashboardWeekComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardWeekComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardWeekComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
