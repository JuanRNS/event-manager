import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { FormComponent } from "../../form-group/form/form.component";
import { FormControl, FormGroup } from '@angular/forms';
import { FormFieldEnum } from '../../../enums/formFieldEnum';
import { OptionsService } from '../../../../features/services/options.service';
import { FormGroupArray } from '../../../interface/form.interface';
import { ApiService } from '../../../../features/services/api.service';
import { IRequestEmployee } from '../../../interface/event.interface';
import { MatButtonModule } from '@angular/material/button';
import { MaskEnum } from '../../../enums/maskEnum';

@Component({
  selector: 'app-modal-update-garcom',
  standalone: true,
  imports: [
    MatDialogModule,
    FormComponent,
    MatButtonModule
  ],
  templateUrl: './modal-update-employee.component.html',
  styleUrl: './modal-update-employee.component.scss'
})
export class ModalUpdateEmployeeComponent implements OnInit {
  public form = new FormGroup({
    name: new FormControl<string | null>(null),
    pixKey: new FormControl<string | null>(null),
    phone: new FormControl<string | null>(null),
    statusEmployee: new FormControl<string | null>(null),
    idEmployeeType: new FormControl<number | null>(null),
  });

  constructor(
    private readonly _optionsService: OptionsService,
    private readonly _service: ApiService,
    private readonly _dialogRef: MatDialogRef<ModalUpdateEmployeeComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { id: number },
  ) { }

  ngOnInit(): void {
    this.getEmployeeById();
  }

  public get formGroupItensPrimary(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Nome',
        controlName: 'name',
        type: 'text',
        placeholder: 'Digite seu nome',
        size: '6',
        mask: MaskEnum.NOME,
        maxlength: 50
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Chave Pix',
        controlName: 'pixKey',
        type: 'text',
        placeholder: 'Digite sua chave pix',
        size: '6',
        mask: MaskEnum.PIX,
        maxlength: 17
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Telefone',
        controlName: 'phone',
        type: 'text',
        placeholder: 'Digite seu telefone',
        size: '6',
        mask: MaskEnum.PHONE,
        maxlength: 15
      },
      {
        component: FormFieldEnum.SELECT,
        label: 'Status',
        controlName: 'statusEmployee',
        type: 'select',
        options: this._optionsService.getOptionsStatusEmployee(),
        size: '6',
      },
      {
        component: FormFieldEnum.SELECT,
        label: 'Tipo de Funcionário',
        controlName: 'idEmployeeType',
        type: 'select',
        options: this._optionsService.getOptionsEmployeeType(),
        size: '6',
      }
    ]
  }

  public updateEmployee() {
    if (!this.form.dirty) {
      this._dialogRef.close();
      return
    };

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.form.markAsDirty();
      return
    };

    const garcom: IRequestEmployee = this.form.value as IRequestEmployee;

    this._service.putUpdateEmployee(this.data.id, garcom).subscribe({
      next: (response) => {
        this._dialogRef.close(true);
      },
    });
  }

  public getEmployeeById() {
    this._service.getEmployeeById(this.data.id).subscribe((response) => {
      this.form.patchValue({
        name: response.name,
        pixKey: response.pixKey,
        phone: response.phone,
        statusEmployee: response.statusEmployee,
        idEmployeeType: response.employeeType.id,
      });
    });
  }
}
