import { Component, input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormGroupArray, FormGroupValue } from '../../../interface/form.interface';
import { InputComponent } from "../input/input.component";
import { FormFieldEnum } from '../../../enums/formFieldEnum';
import { SelectComponent } from '../select/select.component';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [
    InputComponent,
    SelectComponent
  ],
  templateUrl: './form.component.html',
  styleUrl: './form.component.scss'
})
export class FormComponent {
  public form = input.required<FormGroup>();
  public group = input.required<FormGroupArray>();
  public responsive = input<boolean>(true);

  constructor() { }


  public isInput(item: FormGroupValue): boolean {
    return item.component === FormFieldEnum.INPUT;
  }

  public isSeleect(item: FormGroupValue): boolean {
    return item.component === FormFieldEnum.SELECT;
  }
}
