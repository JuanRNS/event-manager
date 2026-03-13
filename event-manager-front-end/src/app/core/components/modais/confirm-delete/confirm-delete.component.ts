import { Component } from '@angular/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-delete',
  imports: [
    MatDialogModule
  ],
  templateUrl: './confirm-delete.component.html',
  styleUrl: './confirm-delete.component.scss',
})
export class ConfirmDeleteComponent {
  constructor(
    private readonly _dialogRef: MatDialogRef<ConfirmDeleteComponent>,
  ) { }

  onCancel() {
    this._dialogRef.close(false);
  }

  onConfirm() {
    this._dialogRef.close(true);
  }
}
