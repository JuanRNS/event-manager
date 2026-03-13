import { Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { IResponseListAddEmployee } from '../../../core/interface/modal-add-garcom.interface';
import { IPartyByWaiter, IResponseModalViewPartyWaiter } from '../../../core/interface/modal-view-party-waiter.interface';
import { CardCalendarComponent } from '../../../core/components/card-calendar/card-calendar.component';

@Component({
    selector: 'app-provider-parties',
    standalone: true,
    imports: [
        MatIconModule,
        MatSelectModule,
        MatFormFieldModule,
        FormsModule,
        CardCalendarComponent,
    ],
    templateUrl: './provider-parties.component.html',
    styleUrl: './provider-parties.component.scss',
})
export class ProviderPartiesComponent implements OnInit {
    public employees: IResponseListAddEmployee[] = [];
    public selectedEmployeeId: number | null = null;
    public providerData: IResponseModalViewPartyWaiter | null = null;
    public parties: IPartyByWaiter[] = [];
    public isLoading = false;

    constructor(private readonly _service: ApiService) { }

    ngOnInit(): void {
        this.loadEmployees();
    }

    public loadEmployees(): void {
        this._service.getListAddEmployee(0, 100).subscribe((response) => {
            this.employees = response.content;
        });
    }

    public onEmployeeSelected(): void {
        if (!this.selectedEmployeeId) return;

        this.isLoading = true;
        this.providerData = null;
        this.parties = [];

        this._service.getPartiesByWaiterId(this.selectedEmployeeId).subscribe({
            next: (response) => {
                this.providerData = response;
                this.parties = response.parties ?? [];
                this.isLoading = false;
            },
            error: () => {
                this.isLoading = false;
            },
        });
    }
}
