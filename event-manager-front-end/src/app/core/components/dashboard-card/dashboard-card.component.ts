import { Component, EventEmitter, input, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { SummaryComponent } from '../summary/summary.component';
import { IResponseDashboard } from '../../interface/dashboard.interface';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatIconModule,
    MatPaginatorModule,
    SummaryComponent
  ],
  templateUrl: './dashboard-card.component.html',
  styleUrl: './dashboard-card.component.scss'
})
export class DashboardCardComponent {
  public listDashboard = input<IResponseDashboard[]>([]);
  public totalElements = input<number>(0);
  public pageSize = input<number>(3);
  constructor() { }

  @Output() pageChange = new EventEmitter<PageEvent>();
  @Output() download = new EventEmitter<{ id: number, name: string }>();

  onDownload(id: number, name: string) {
    this.download.emit({ id, name });
  }

  onPageChange(event: PageEvent) {
    this.pageChange.emit(event);
  }
}
