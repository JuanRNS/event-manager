import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef } from '@angular/material/dialog';

import { ModalPixTelConfirmationComponent } from './modal-pix-tel-confirmation.component';

describe('ModalPixTelConfirmationComponent', () => {
  let component: ModalPixTelConfirmationComponent;
  let fixture: ComponentFixture<ModalPixTelConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalPixTelConfirmationComponent],
      providers: [
        { provide: MatDialogRef, useValue: jasmine.createSpyObj<MatDialogRef<ModalPixTelConfirmationComponent>>('MatDialogRef', ['close']) },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalPixTelConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
