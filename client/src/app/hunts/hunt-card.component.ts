import { Component, Input } from '@angular/core';
import { Hunt } from './hunt';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-hunt-card',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatListModule, MatIconModule, RouterLink],
  templateUrl: './hunt-card.component.html',
  styleUrl: './hunt-card.component.scss'
})
export class HuntCardComponent {

  @Input() hunt: Hunt;
  @Input() simple?: boolean = false;
}
