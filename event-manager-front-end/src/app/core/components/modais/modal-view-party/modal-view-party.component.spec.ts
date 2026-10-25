import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

import { ModalViewPartyComponent } from './modal-view-party.component';

describe('ModalViewPartyComponent', () => {
  let component: ModalViewPartyComponent;
  let fixture: ComponentFixture<ModalViewPartyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalViewPartyComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MAT_DIALOG_DATA, useValue: { id: 1 } },
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalViewPartyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
