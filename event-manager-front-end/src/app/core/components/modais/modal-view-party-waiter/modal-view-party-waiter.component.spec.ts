import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { ModalViewPartyWaiterComponent } from './modal-view-party-waiter.component';

describe('ModalViewPartyWaiterComponent', () => {
  let component: ModalViewPartyWaiterComponent;
  let fixture: ComponentFixture<ModalViewPartyWaiterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalViewPartyWaiterComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MatDialogRef, useValue: jasmine.createSpyObj<MatDialogRef<ModalViewPartyWaiterComponent>>('MatDialogRef', ['close']) },
        { provide: MAT_DIALOG_DATA, useValue: { id: 1 } },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalViewPartyWaiterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
