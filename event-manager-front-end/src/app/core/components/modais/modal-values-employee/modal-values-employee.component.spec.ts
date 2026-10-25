import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ModalValuesEmployeeComponent } from './modal-values-employee.component';

describe('ModalValuesEmployeeComponent', () => {
  let component: ModalValuesEmployeeComponent;
  let fixture: ComponentFixture<ModalValuesEmployeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalValuesEmployeeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MatDialogRef, useValue: jasmine.createSpyObj<MatDialogRef<ModalValuesEmployeeComponent>>('MatDialogRef', ['close']) },
        { provide: MAT_DIALOG_DATA, useValue: { id: 1 } },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalValuesEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
