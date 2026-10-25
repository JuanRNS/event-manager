import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ModalUpdateFestaComponent } from './modal-update-festa.component';

describe('ModalUpdateFestaComponent', () => {
  let component: ModalUpdateFestaComponent;
  let fixture: ComponentFixture<ModalUpdateFestaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalUpdateFestaComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MatDialogRef, useValue: jasmine.createSpyObj<MatDialogRef<ModalUpdateFestaComponent>>('MatDialogRef', ['close']) },
        { provide: MAT_DIALOG_DATA, useValue: { id: 1 } },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalUpdateFestaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
