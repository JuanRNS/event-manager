import { Component, Inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../../../features/services/api.service';
import { IResponseModalViewParty } from '../../../interface/modal-view-party.interface';
import { ParseDateUtil } from '../../../utils/parse-date.util';
import { DataCardComponent } from '../../data-card/data-card.component';

@Component({
  selector: 'app-modal-view-party',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    DataCardComponent
  ],
  templateUrl: './modal-view-party.component.html',
  styleUrl: './modal-view-party.component.scss'
})
export class ModalViewPartyComponent implements OnInit{
  public listViewParty!: IResponseModalViewParty; 

  constructor(
    private readonly _service: ApiService,
    @Inject(MAT_DIALOG_DATA) public data: { id: number },
  ) { }

  ngOnInit(): void {
      this._service.getFestaEmployeeById(this.data.id).subscribe((response) => {
        this.listViewParty = {
          ...response,
          date: ParseDateUtil.parseDate(response.date),
        };
      });
  }

  public getEmployeeCardItems(employee: any) {
    return [
      { label: 'Telefone', value: employee.phone },
      { label: 'Pix', value: employee.pixKey },
      { label: 'Status', value: employee.statusEmployee }
    ];
  }
}
