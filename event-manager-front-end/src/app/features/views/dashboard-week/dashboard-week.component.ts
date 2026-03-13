import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { IResponseDashboard } from '../../../core/interface/dashboard.interface';
import { ApiService } from '../../services/api.service';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ToastService } from '../../../core/services/toast.service';
import { DashboardComponent } from '../../../core/components/dashboard/dashboard.component';
import { FormControl, FormGroup } from '@angular/forms';
import { FormFieldEnum } from '../../../core/enums/formFieldEnum';
import { FormGroupArray } from '../../../core/interface/form.interface';
import { FormComponent } from '../../../core/components/form-group/form/form.component';

@Component({
  selector: 'app-dashboard-week',
  standalone: true,
  imports: [
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatPaginatorModule,
    DashboardComponent,
    FormComponent
  ],
  templateUrl: './dashboard-week.component.html',
  styleUrl: './dashboard-week.component.scss'
})
export class DashboardWeekComponent implements OnInit {
  public listDashboard: IResponseDashboard[] = [];
  public page = 0;
  public pageSize = 3;
  public totalElements = 0;

  public isFilteringByDate = false;

  public form = new FormGroup({
    fromDate: new FormControl<string | null>(null),
    toDate: new FormControl<string | null>(null),
  });

  constructor(
    private readonly _service: ApiService,
    private readonly _toast: ToastService
  ) { }

  ngOnInit(): void {
    this.getListDashboard();
  }

  public get formGroupItens(): FormGroupArray {
    return [
      {
        component: FormFieldEnum.INPUT,
        label: 'Data Inicial',
        controlName: 'fromDate',
        type: 'date',
        size: '6',
        maxlength: 10,
      },
      {
        component: FormFieldEnum.INPUT,
        label: 'Data Final',
        controlName: 'toDate',
        type: 'date',
        size: '6',
        maxlength: 10,
      },
    ];
  }

  public getListDashboard() {
    this.isFilteringByDate = false;
    this.form.reset();
    this._service.getListDashboard(this.page, this.pageSize).subscribe({
      next: (res) => {
        this.listDashboard = res.content;
        this.totalElements = res.page.totalElements;
      },
      error: (err) => {
        this._toast.error(err.error?.message || 'Erro ao carregar o dashboard semanal.');
      },
    });
  }

  public getDashBoardFromTo() {
    if (!this.form.value.fromDate || !this.form.value.toDate) {
      this._toast.error('Por favor, preencha as datas para filtrar o dashboard');
      return;
    }

    if (!this.isFilteringByDate) {
      this.page = 0;
    }
    this.isFilteringByDate = true;

    const from = new Date(this.form.value.fromDate ?? '').toISOString().substring(0, 10);
    const to = new Date(this.form.value.toDate ?? '').toISOString().substring(0, 10);

    this._service.getDashboardFromTo(from, to).subscribe({
      next: (res) => {
        this.listDashboard = res.content;
        this.totalElements = res.page.totalElements;
      },
      error: (err) => {
        this._toast.error(err.error?.message || 'Erro ao carregar o dashboard filtrado.');
      },
    });
  }

  public downloadReport(id: number, name: string) {
    let from: string | undefined;
    let to: string | undefined;

    if (this.isFilteringByDate && this.form.value.fromDate && this.form.value.toDate) {
      from = new Date(this.form.value.fromDate).toISOString().substring(0, 10);
      to = new Date(this.form.value.toDate).toISOString().substring(0, 10);
    }

    this._service.getFileDownload(id, from, to).subscribe({
      next: (res) => {
        const blob = new Blob([res], { type: 'application/pdf' });
        const url = URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.target = '_blank';
        link.download = `relatorio_garcom_${name}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        URL.revokeObjectURL(url);
      },
      error: (err) => {
        this._toast.error('Erro ao baixar o relatório', err);
      },
    });
  }

  public onPageChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;

    if (this.isFilteringByDate) {
      this.getDashBoardFromTo();
    } else {
      this.getListDashboard();
    }
  }
}
