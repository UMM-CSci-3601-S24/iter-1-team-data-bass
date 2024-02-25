import { Component } from '@angular/core';

@Component({
  selector: 'app-host',
  standalone: true,
  imports: [],
  templateUrl: './host.component.html',
  styleUrl: './host.component.scss'
})
export class HostComponent {
  // test: string;
  // count: number = 0;


  // fun(): void {
  //   this.test = 'You are my hero!';
  //   this.count++;
  //   alert('Test message :'+this.test+' Count :'+this.count);
  // }
  name: string = "Joe Mama";

  clickSave(): void{
    this.name = "slasenflkjn";


  }

  over(): void{
    this.name = "aefliaemfln";
  }




}
