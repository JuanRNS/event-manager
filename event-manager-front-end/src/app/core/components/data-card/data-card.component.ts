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
}
