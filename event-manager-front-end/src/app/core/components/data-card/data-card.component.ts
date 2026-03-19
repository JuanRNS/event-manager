import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface DataCardItem {
  label: string;
  value: any;
}

@Component({
  selector: 'app-data-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-card.component.html',
  styleUrls: ['./data-card.component.scss']
})
export class DataCardComponent {
  @Input() title?: string;
  @Input() items: DataCardItem[] = [];
  @Input() status: string = 'ATIVO';

  public getStatusEmployee(): string {
    if (this.status === 'ATIVO') return 'status-active';
    if (this.status === 'INATIVO') return 'status-inactive';
    if (this.status === 'AGENDADA') return 'status-pending';
    if (this.status === 'REALIZADA') return 'status-done';
    if (this.status === 'CANCELADA') return 'status-canceled';
    return 'status-default';
  }
}
