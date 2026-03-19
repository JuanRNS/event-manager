import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalAddGarcomComponent } from './modal-add-employee.component';

describe('ModalAddGarcomComponent', () => {
  let component: ModalAddGarcomComponent;
  let fixture: ComponentFixture<ModalAddGarcomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalAddGarcomComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModalAddGarcomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
