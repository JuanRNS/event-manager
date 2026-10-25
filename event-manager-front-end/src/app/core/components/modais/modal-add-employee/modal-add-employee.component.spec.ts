import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ModalAddEmployeeComponent } from './modal-add-employee.component';

describe('ModalAddEmployeeComponent', () => {
  let component: ModalAddEmployeeComponent;
  let fixture: ComponentFixture<ModalAddEmployeeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalAddEmployeeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MatDialogRef, useValue: jasmine.createSpyObj<MatDialogRef<ModalAddEmployeeComponent>>('MatDialogRef', ['close']) },
        { provide: MAT_DIALOG_DATA, useValue: { id: 1 } },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalAddEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
