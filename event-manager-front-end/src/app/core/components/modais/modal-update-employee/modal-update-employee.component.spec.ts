import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ModalUpdateEmployeeComponent } from './modal-update-employee.component';

describe('ModalUpdateEmployeeComponent', () => {
  let component: ModalUpdateEmployeeComponent;
  let fixture: ComponentFixture<ModalUpdateEmployeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalUpdateEmployeeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MatDialogRef, useValue: jasmine.createSpyObj<MatDialogRef<ModalUpdateEmployeeComponent>>('MatDialogRef', ['close']) },
        { provide: MAT_DIALOG_DATA, useValue: { id: 1 } },
      ]
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
