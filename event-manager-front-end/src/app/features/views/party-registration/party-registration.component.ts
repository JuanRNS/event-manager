import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, Validators } from '@angular/forms';
import { FormFieldEnum } from '../../../core/enums/formFieldEnum';
import { FormGroupArray } from '../../../core/interface/form.interface';
import { FormComponent } from '../../../core/components/form-group/form/form.component';
import { ApiService } from '../../services/api.service';
import { IRequestParty, IResponseParty } from '../../../core/interface/party.interface';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from "@angular/material/button";
import { MatDialog } from '@angular/material/dialog';
import { ParseDateUtil } from '../../../core/utils/parse-date.util';
import { MaskEnum } from '../../../core/enums/maskEnum';
import { ToastService } from '../../../core/services/toast.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-party-registration',
  standalone: true,
  imports: [
    FormsModule,
    FormComponent,
    MatPaginatorModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './party-registration.component.html',
  styleUrl: './party-registration.component.scss',
})
export class PartyRegistrationComponent implements OnInit {
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

  public listParty: IResponseParty[] = [];
  public page = 0;
  public pageSize = 5;
  public totalElements = 0;

  constructor(
    private readonly _service: ApiService,
    private readonly _dialog: MatDialog,
    private readonly _toast: ToastService,
    private readonly _route: Router
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
    this._service.getListPartyStatus(this.page, this.pageSize).subscribe((res) => {
      this.listParty = res.content.map((party) => ({
        ...party,
        date: ParseDateUtil.parseDate(party.date),
      }));
      this.totalElements = res.page.totalElements;
    });
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

    this._service.postRegisterParty(data).subscribe(({
      next: () => {
        this._toast.success('Festa criada com sucesso!');
        this._route.navigate(['/party-list']);
      },
      error: (err) => {
        this._toast.error('Erro ao criar festa. Tente novamente.', err);
      }
    }));
    this.form.reset();
  }
}
