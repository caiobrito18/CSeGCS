import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'status'
})
export class StatusPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'DISPONIVEL':
        return 'Disponível';
      case 'EMPRESTADO':
        return 'Emprestado';
      default:
        return value;
    }
  }
}
