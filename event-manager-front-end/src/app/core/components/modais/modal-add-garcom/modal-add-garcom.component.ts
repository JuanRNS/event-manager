import { Component, Inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { IResponseListAddEmployee } from '../../../interface/modal-add-garcom.interface';
import { ApiService } from '../../../../features/services/api.service';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { FormControl, FormGroup } from '@angular/forms';
import { FormGroupArray } from '../../../interface/form.interface';
import { FormFieldEnum } from '../../../enums/formFieldEnum';
import { FormComponent } from "../../form-group/form/form.component";
import { ToastService } from '../../../services/toast.service';
import { DataCardComponent } from '../../data-card/data-card.component';

@Component({
  selector: 'app-modal-add-garcom',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatPaginatorModule,
    MatIconModule,
    FormComponent,
    DataCardComponent
  ],
  templateUrl: './modal-add-garcom.component.html',
  styleUrl: './modal-add-garcom.component.scss'
})
export class ModalAddGarcomComponent implements OnInit {
  public listGarcom: IResponseListAddEmployee[] = [];
  public listGarcomOriginal: IResponseListAddEmployee[] = [];
  public listGarcomAdd: number[] = [];
  public form = new FormGroup({
    search: new FormControl<string | null>(null),
  });
  public isSubmit: boolean = false;
  public page = 0;
  public totalElements = 0;
  public pageSize = 4;

  constructor(
    private readonly _service: ApiService,
    private readonly _toast: ToastService,
    private readonly _dialogRef: MatDialogRef<ModalAddGarcomComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { id: number },
  ) { }

  ngOnInit(): void {
    this.getEmployeeIdsByFestaId();
    this.getListEmployee();

    this.form.controls.search.valueChanges.subscribe(value => {
      if (!value) {
        this.listGarcom = [...this.listGarcomOriginal];
        return;
      }
      this.listGarcom = this.listGarcom.filter(garcom => garcom.name.toLowerCase().includes(value.toLowerCase()));
    })
  }

  public get formGroupItens(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Buscar Funcionário',
        controlName: 'search',
        type: 'text',
        placeholder: 'Digite o nome do funcionário',
        size: '12',
      }
    ]
  }

  public getListEmployee() {
    this._service.getListAddEmployee(this.page, this.pageSize).subscribe({
      next: (res) => {
        this.listGarcom = res.content;
        this.listGarcomOriginal = res.content;
        this.totalElements = res.page.totalElements;
      },
      error: (err) => {
        this._toast.error(err.error.message || 'Erro ao carregar a lista de funcionários.');
      }
    });
  }
  public getEmployeeIdsByFestaId() {
    this._service.getEmployeeIdsByFestaId(this.data.id).subscribe({
      next: (res) => {
        this.listGarcomAdd = res;
      },
      error: (err) => {
        this._toast.error(err.error.message || 'Erro ao carregar os garçons da festa.');
      }
    });
  }

  public addGarcom(id: number) {
    this.isSubmit = true;
    this.listGarcomAdd.push(id);
  }

  public removeGarcom(id: number) {
    this.isSubmit = true;
    const index = this.listGarcomAdd.indexOf(id);
    this.listGarcomAdd.splice(index, 1);
  }

  public onPageChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.getListEmployee();
  }

  public submit() {
    if (!this.isSubmit) {
      this._dialogRef.close();
      return;
    }
    this._service.postAddEmployeeParty({
      partyId: this.data.id,
      employeeIds: this.listGarcomAdd
    }).subscribe({
      next: (res) => {
        this._dialogRef.close(true);
      },
      error: (err) => {
        this._toast.error(err.error.message || 'Erro ao adicionar o garçom à festa.');
      }
    });
  }

  public getGarcomCardItems(item: any) {
    return [
      { label: 'Contato', value: item.phone }
    ];
  }
}
