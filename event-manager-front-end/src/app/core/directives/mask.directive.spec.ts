import { ElementRef } from '@angular/core';
import { MaskDirective } from './mask.directive';

describe('MaskDirective', () => {
  it('should create an instance', () => {
    const directive = new MaskDirective(new ElementRef(document.createElement('input')));
    expect(directive).toBeTruthy();
  });
});
