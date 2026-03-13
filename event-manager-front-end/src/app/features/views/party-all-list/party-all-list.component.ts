import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { IResponseParty } from '../../../core/interface/party.interface';
import { ModalAddGarcomComponent } from '../../../core/components/modais/modal-add-garcom/modal-add-garcom.component';
import { ModalViewPartyComponent } from '../../../core/components/modais/modal-view-party/modal-view-party.component';
import { ApiService } from '../../services/api.service';
import { MatDialog } from '@angular/material/dialog';
import { ParseDateUtil } from '../../../core/utils/parse-date.util';
import { ModalUpdateFestaComponent } from '../../../core/components/modais/modal-update-festa/modal-update-festa.component';
import { FormFieldEnum } from '../../../core/enums/formFieldEnum';
import { FormGroupArray } from '../../../core/interface/form.interface';
import { FormControl, FormGroup, FormsModule, Validators } from '@angular/forms';
import { ModalValuesEmployeeComponent } from '../../../core/components/modais/modal-values-employee/modal-values-employee.component';
import { ConfirmDeleteComponent } from '../../../core/components/modais/confirm-delete/confirm-delete.component';
import { IRequestParty } from '../../../core/interface/party.interface';
import { MaskEnum } from '../../../core/enums/maskEnum';
import { ToastService } from '../../../core/services/toast.service';
import { FormComponent } from '../../../core/components/form-group/form/form.component';
import { DataCardComponent } from '../../../core/components/data-card/data-card.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-party-all-list',
  standalone: true,
  imports: [MatPaginatorModule, MatIconModule, MatButtonModule, MatMenuModule, FormsModule, FormComponent, DataCardComponent, CommonModule],
  templateUrl: './party-all-list.component.html',
  styleUrl: './party-all-list.component.scss',
})
export class PartyAllListComponent implements OnInit {
  public listParty: IResponseParty[] = [];
  public page = 0;
  public pageSize = 5;
  public totalElements = 0;

  public form = new FormGroup({
    location: new FormControl<string | null>(null, [Validators.required]),
    nameClient: new FormControl<string | null>(null, [Validators.required]),
    date: new FormControl<string | null>(null, [Validators.required]),
    idMaterial: new FormControl<number | null>(null, [Validators.required]),
    numberOfPeople: new FormControl<string | null>(null, [
      Validators.required,
      Validators.min(1),
    ]),
    hourStart: new FormControl<string | null>(null, [Validators.required]),
    hourEnd: new FormControl<string | null>(null, [Validators.required]),
  });

  constructor(
    private readonly _service: ApiService,
    private readonly _dialog: MatDialog,
    private readonly _toast: ToastService,
  ) { }

  ngOnInit(): void {
    this.getListParty();
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
        mask: MaskEnum.NOME
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Local da festa',
        controlName: 'location',
        type: 'text',
        placeholder: 'Local da festa',
        size: '6',
        mask: MaskEnum.NOME
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
        label: 'Material usado',
        controlName: 'idMaterial',
        type: 'text',
        placeholder: 'Material usado',
        options: this._service.getMaterial(),
        size: '6',
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Quantidade de Pessoas',
        controlName: 'numberOfPeople',
        placeholder: 'Quantidade de Pessoas',
        type: 'text',
        size: '6',
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Horário de início',
        controlName: 'hourStart',
        type: 'time',
        placeholder: 'Horário de início',
        size: '6',
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Horário de término',
        controlName: 'hourEnd',
        type: 'time',
        placeholder: 'Horário de término',
        size: '6',
      }
    ];
  }

  public getListParty() {
    this._service.getListParty(this.page, this.pageSize).subscribe((res) => {
      this.listParty = res.content.map((party) => ({
        ...party,
        date: ParseDateUtil.parseDate(party.date),
      }));
      this.totalElements = res.page.totalElements;
    });
  }

  public getPartyCardItems(item: IResponseParty) {
    return [
      { label: 'Local', value: item.location },
      { label: 'Data', value: item.date },
      { label: 'Status', value: item.status }
    ];
  }

  public deleteParty(id: number) {
    const dialogRef = this._dialog.open(ConfirmDeleteComponent, {
      width: '400px',
      height: '200px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._service.deleteParty(id).subscribe(() => {
          this.getListParty();
        });
      }
    });
  }

  public addGarcons(id: number) {
    this._dialog.open(ModalAddGarcomComponent, {
      width: '90vw',
      maxWidth: '600px',
      data: { id: id },
    });
  }

  public viewParty(id: number) {
    this._dialog.open(ModalViewPartyComponent, {
      width: '90vw',
      maxWidth: '800px',
      autoFocus: false,
      data: { id: id },
    });
  }

  public editParty(id: number) {
    const dialogRef = this._dialog.open(ModalUpdateFestaComponent, {
      width: '90vw',
      maxWidth: '800px',
      autoFocus: false,
      data: { id: id },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getListParty();
      }
    });
  }

  public valuesParty(id: number) {
    const dialogRef = this._dialog.open(ModalValuesEmployeeComponent, {
      maxWidth: '70vw',
      maxHeight: '50vh',
      autoFocus: false,
      data: { id: id },
    })
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getListParty();
      }
    });
  }

  public onChangePage(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.getListParty();
  }

  public createParty() {
    if (this.form.invalid) {
      this.form.markAllAsDirty();
      this.form.updateValueAndValidity();
      this.form.markAllAsTouched();
      return;
    }

    const data: IRequestParty = {
      location: this.form.controls.location.value as string,
      nameClient: this.form.controls.nameClient.value as string,
      date: new Date(this.form.controls.date.value as string)
        .toISOString()
        .substring(0, 19),
      idMaterial: Number(this.form.controls.idMaterial.value),
      numberOfPeople: Number(this.form.controls.numberOfPeople.value),
      hourStart: this.form.controls.hourStart.value!,
      hourEnd: this.form.controls.hourEnd.value!,
    };

    this._service.postRegisterParty(data).subscribe({
      next: () => {
        this._toast.success('Festa criada com sucesso!');
        this.form.reset();
        this.getListParty();
      },
      error: (err) => {
        this._toast.error('Erro ao criar festa. Tente novamente.', err);
      }
    });
  }

}
