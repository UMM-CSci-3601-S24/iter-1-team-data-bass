import { Component } from '@angular/core';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-player',
  standalone: true,
  imports: [NgFor],
  templateUrl: './player.component.html',
  styleUrl: './player.component.scss'
})
export class PlayerComponent {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  textAreasList:any = [];

  addTextarea(){
      this.textAreasList.push('text_area'+ (this.textAreasList.length + 1));
  }


  removeTextArea(index){
      this.textAreasList.splice(index, 1);
  }

}
