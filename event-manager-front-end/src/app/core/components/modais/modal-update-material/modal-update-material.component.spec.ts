import { ComponentFixture, TestBed } from '@angular/core/testing';

import ModalUpdateMaterialComponent from './modal-update-material.component';

describe('ModalUpdateMaterialComponent', () => {
  let component: ModalUpdateMaterialComponent;
  let fixture: ComponentFixture<ModalUpdateMaterialComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalUpdateMaterialComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalUpdateMaterialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
