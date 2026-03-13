import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { FormFieldEnum } from '../../../enums/formFieldEnum';
import { FormGroupArray } from '../../../interface/form.interface';
import { FormComponent } from '../../form-group/form/form.component';
import { ApiService } from '../../../../features/services/api.service';
import { OptionsService } from '../../../../features/services/options.service';
import { IRequestUpdateFesta } from '../../../interface/modal-update-festa.interface';
import { ToastService } from '../../../services/toast.service';
import { IResponseValues } from '../../../interface/party.interface';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { DataCardComponent } from '../../data-card/data-card.component';

@Component({
  selector: 'app-modal-update-festa',
  standalone: true,
  imports: [
    MatDialogModule,
    FormComponent,
    MatIconModule,
    MatButtonModule,
    DataCardComponent
  ],
  templateUrl: './modal-update-festa.component.html',
  styleUrl: './modal-update-festa.component.scss'
})
export class ModalUpdateFestaComponent implements OnInit {
  public form = new FormGroup({
    location: new FormControl<string | null>(null, [Validators.required]),
    nameClient: new FormControl<string | null>(null, [Validators.required]),
    date: new FormControl<string | null>(null, [Validators.required]),
    idMaterial: new FormControl<number | null>(null, [Validators.required]),
    status: new FormControl<string | null>(null, [Validators.required]),
    numberOfPeople: new FormControl<number | null>(null, [Validators.required]),
  })

  public listValues: IResponseValues[] = [];
  public alterTable: boolean = false;

  constructor(
    private readonly _service: ApiService,
    private readonly _toast: ToastService,
    private readonly _optionsService: OptionsService,
    private readonly _dialogRef: MatDialogRef<ModalUpdateFestaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { id: number }
  ) { }

  ngOnInit(): void {
    this.getParty();
  }

  public get formGroupItens(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Cliente',
        controlName: 'nameClient',
        type: 'text',
        placeholder: 'Nome do cliente',
        size: '6',
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Local da festa',
        controlName: 'location',
        type: 'text',
        placeholder: 'Local da festa',
        size: '6',
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Data da festa',
        controlName: 'date',
        type: 'date',
        placeholder: 'Data da festa',
        size: '6',
      },
      {
        component: FormFieldEnum.SELECT,
        label: 'ID do material',
        controlName: 'idMaterial',
        type: 'text',
        placeholder: 'ID do material',
        options: this._service.getMaterial(),
        size: '6',
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Quantidade de pessoas',
        controlName: 'numberOfPeople',
        type: 'text',
        placeholder: 'Quantidade de pessoas',
        size: '6',
      },
      {
        component: FormFieldEnum.SELECT,
        label: 'Status',
        controlName: 'status',
        type: 'text',
        placeholder: 'Status',
        size: '6',
        options: this._optionsService.getOptionsStatusFesta(),
      },
    ];
  }

  public getParty() {
    this._service.getPartyById(this.data.id).subscribe((response) => {
      this.form.patchValue({
        location: response.location,
        nameClient: response.nameClient,
        date: response.date.toString().substring(0, 10),
        idMaterial: response.material.id,
        status: response.status,
        numberOfPeople: response.numberOfPeople
      });
      this.listValues = response.values;
    });
  }
  public updateParty() {
    if (!this.form.dirty && this.alterTable === false) {
      this._dialogRef.close();
      return;
    }
    if (this.form.invalid && this.alterTable === false) {
      this.form.markAllAsTouched();
      this.form.markAsDirty();
      return;
    }
   const data = {
     ...this.form.value,
     date: new Date(this.form.value.date?.toString() || '').toISOString(),
     values: this.listValues
   } as IRequestUpdateFesta;

    this._service.putUpdateParty(this.data.id, data).subscribe({
      next: (response) => {
        this._dialogRef.close(true);
      },
      error: (err) => {
        this._toast.error(err.error.message || 'Erro ao atualizar a festa.');
      }
    });
  }

  public deleteValue(item: IResponseValues) {
    this.listValues = this.listValues.filter(i => i.id !== item.id);
    this.alterTable = !this.alterTable;
  }

  public getValueCardItems(item: any) {
    return [
      { label: 'Valor', value: item.value }
    ];
  }
}
