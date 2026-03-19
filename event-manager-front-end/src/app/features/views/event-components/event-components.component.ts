import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  Validators,
} from '@angular/forms';

import {
  FormGroupArray,
  IOptions,
} from '../../../core/interface/form.interface';
import { FormFieldEnum } from '../../../core/enums/formFieldEnum';
import { FormComponent } from '../../../core/components/form-group/form/form.component';
import { ApiService } from '../../services/api.service';
import {
  IRequestEmployee,
  IRequestEmployeeType,
  IRequestMaterial,
  IResponseEmployee,
  IResponseMaterial,
} from '../../../core/interface/event.interface';
import { MatButtonModule } from '@angular/material/button';
import { OptionsService } from '../../services/options.service';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ModalUpdateEmployeeComponent } from '../../../core/components/modais/modal-update-employee/modal-update-employee.component';
import ModalUpdateMaterialComponent from '../../../core/components/modais/modal-update-material/modal-update-material.component';
import { MaskEnum } from '../../../core/enums/maskEnum';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { ModalViewPartyWaiterComponent } from '../../../core/components/modais/modal-view-party-waiter/modal-view-party-waiter.component';
import { BehaviorSubject } from 'rxjs';
import { ToastService } from '../../../core/services/toast.service';
import { ModalDeleteConfirmationComponent } from '../../../core/components/modais/modal-delete-confirmation/modal-delete-confirmation.component';
import { ModalPixTelConfirmationComponent } from '../../../core/components/modais/modal-pix-tel-confirmation/modal-pix-tel-confirmation.component';
import { DataCardComponent } from '../../../core/components/data-card/data-card.component';

@Component({
  selector: 'app-event-components',
  standalone: true,
  imports: [
    FormsModule,
    MatButtonModule,
    FormComponent,
    MatPaginatorModule,
    MatMenuModule,
    MatIconModule,
    DataCardComponent
  ],
  templateUrl: './event-components.component.html',
  styleUrl: './event-components.component.scss',
})
export class EventComponentsComponent implements OnInit {
  public formPrimary = new FormGroup({
    name: new FormControl<string | null>(null, [Validators.required]),
    pixKey: new FormControl<string | null>(null, [Validators.required]),
    phone: new FormControl<string | null>(null, [Validators.required]),
    idEmployeeType: new FormControl<number | null>(null, [Validators.required]),
  });

  public formThird = new FormGroup({
    description: new FormControl<string | null>(null),
  });

  public formEmployeeType = new FormGroup({
    type: new FormControl<string | null>(null, [Validators.required]),
  });

  public ListEmployees: IResponseEmployee[] = [];
  public ListMaterials: IResponseMaterial[] = [];
  public page = 0;
  public pageSize = 5;
  public totalElements = 0;
  public createEmployeeType: boolean = false;
  public employeeTypesOptions$ = new BehaviorSubject<IOptions[]>([]);

  public formGroupItensPrimary: FormGroupArray = [];

  constructor(
    private readonly _service: ApiService,
    private readonly _optionsService: OptionsService,
    private readonly _dialog: MatDialog,
    private readonly _toast: ToastService
  ) {
    this.formGroupItensPrimary = this._formGroupItensPrimary();
  }

  ngOnInit(): void {
    this.getEmployee();
    this.getMaterials();
    this.getEmployeeTypesOptions();
    this.formPrimary.controls.phone.valueChanges.subscribe((value) => {
      if (value && value.length >= 11) {
        this._dialog.open(ModalPixTelConfirmationComponent, {
          width: '400px',
          height: '200px',
          autoFocus: false,
        }).afterClosed().subscribe((confirmed: boolean) => {
          if (confirmed) {
            this.formPrimary.controls.pixKey.setValue(value);
          } else {
            const pix = this.formGroupItensPrimary.findIndex((item => item.controlName === 'pixKey'));
            this.formGroupItensPrimary[pix].hidden = false;
            this.formPrimary.controls.pixKey.setValue(null);
          }
        });
      }
    });
  }

  private _formGroupItensPrimary(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Nome',
        controlName: 'name',
        type: 'text',
        placeholder: 'Digite seu nome',
        size: '6',
        maxlength: 50,
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Telefone',
        controlName: 'phone',
        type: 'text',
        placeholder: 'Digite seu telefone',
        size: '6',
        mask: MaskEnum.PHONE,
        maxlength: 15,
      },
      {
        component: FormFieldEnum.SELECT,
        label: 'Tipo de Funcionário',
        controlName: 'idEmployeeType',
        type: 'select',
        options: this.employeeTypesOptions$.asObservable(),
        size: '6',
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Chave Pix',
        controlName: 'pixKey',
        type: 'text',
        placeholder: 'Digite sua chave pix',
        size: '6',
        mask: MaskEnum.PIX,
        maxlength: 17,
        hidden: true,
      },
    ];
  }

  public get formGroupItensThird(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Descrição do Material',
        controlName: 'description',
        type: 'text',
        size: '12',
      },
    ];
  }

  public get formGroupItensEmployeeType(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Novo Tipo de Funcionário',
        controlName: 'type',
        type: 'text',
        size: '12',
      },
    ];
  }

  public createEmployee() {
    const employee = this.formPrimary.value;
    const data = {
      ...employee,
      idEmployeeType: this.formPrimary.value.idEmployeeType,
    } as IRequestEmployee;

    this._service.postCreateEmployee(data).subscribe({
      next: (value) => {
        this.formPrimary.reset();
        this.getEmployee();
        const pix = this.formGroupItensPrimary.findIndex((item) => item.controlName === 'pixKey');
        this.formGroupItensPrimary[pix].hidden = true;
        this._toast.success('Funcionário criado com sucesso!');
      },
      error: (err) => {
        this._toast.error('Erro ao criar funcionário. Tente novamente.', err);
      },
    });
  }

  public createEmployeeTypeRequest() {
    if (this.formEmployeeType.invalid) {
      this.formEmployeeType.markAllAsTouched();
      this.formEmployeeType.updateValueAndValidity();
      this.formEmployeeType.markAsDirty();
      return;
    }
    const data = this.formEmployeeType.value as IRequestEmployeeType;
    this._service.postCreateEmployeeType(data).subscribe((res) => {
      this.createEmployeeType = false;
      this.formEmployeeType.reset();
      this.getEmployeeTypesOptions();
    });
  }

  public getEmployeeTypesOptions() {
    this._optionsService.getOptionsEmployeeType().subscribe((options) => {
      this.employeeTypesOptions$.next(options);
    });
  }

  public getEmployee() {
    this._service
      .getEmployees(this.page, this.pageSize)
      .subscribe((response) => {
        this.ListEmployees = response.content;
        this.totalElements = response.page.totalElements;
        this.pageSize = response.page.size;
      });
  }
  public onChangePage(event: PageEvent) {
    this.page = event.pageIndex;
    this.getEmployee();
  }

  public getMaterials() {
    this._service.getMaterial().subscribe((response) => {
      this.ListMaterials = response;
    });
  }

  public getEmployeeCardItems(item: IResponseEmployee) {
    return [
      { label: 'Pix', value: item.pixKey },
      { label: 'Telefone', value: item.phone },
      { label: 'Status', value: item.statusEmployee },
      { label: 'Tipo', value: item.employeeType.type }
    ];
  }

  public getMaterialCardItems(item: IResponseMaterial) {
    return [
      { label: 'Tipo', value: item.description }
    ];
  }

  public editEmployee(id: number) {
    const dialogRef = this._dialog.open(ModalUpdateEmployeeComponent, {
      width: '90vw',
      maxWidth: '600px',
      data: {
        id: id,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getEmployee();
      }
    });
  }

  public viewFestas(id: number) {
    const dialogRef = this._dialog.open(ModalViewPartyWaiterComponent, {
      width: '90vw',
      maxWidth: '800px',
      autoFocus: false,
      data: {
        id: id,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getEmployee();
      }
    });
  }

  public deleteEmployee(id: number) {
    const dialog = this._dialog.open(ModalDeleteConfirmationComponent, {
      width: '400px',
      height: '200px',
      autoFocus: false,
    });
    dialog.afterClosed().subscribe((result) => {
      if (result) {
        this._service.deleteEmployee(id).subscribe(() => {
          this.getEmployee();
        });
      }
    });
  }
  public createMaterial() {
    const material: IRequestMaterial = this.formThird.value as IRequestMaterial;
    this._service.postCreateMaterial(material).subscribe(() => {
      this.getMaterials();
      this.formThird.reset();
    });
  }

  public deleteMaterial(id: number) {
    const dialog = this._dialog.open(ModalDeleteConfirmationComponent, {
      width: '400px',
      height: '200px',
      autoFocus: false,
    });
    dialog.afterClosed().subscribe((result) => {
      if (result) {
        this._service.deleteMaterial(id).subscribe(() => {
          this.getMaterials();
        });
      }
    });
  }

  public editMaterial(id: number) {
    const dialogRef = this._dialog.open(ModalUpdateMaterialComponent, {
      width: '400px',
      height: '300px',
      data: {
        id: id,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getMaterials();
      }
    });
  }
}
