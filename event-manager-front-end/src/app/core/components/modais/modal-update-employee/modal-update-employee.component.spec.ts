import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalUpdateEmployeeComponent } from './modal-update-employee.component';

describe('ModalUpdateEmployeeComponent', () => {
  let component: ModalUpdateEmployeeComponent;
  let fixture: ComponentFixture<ModalUpdateEmployeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalUpdateEmployeeComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalUpdateEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
