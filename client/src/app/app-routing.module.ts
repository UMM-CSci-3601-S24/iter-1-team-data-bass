import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { PlayerComponent } from './player/player.component';
import { HuntListComponent } from './hunts/hunt-list.component';
import { HttpClientModule } from '@angular/common/http';
import { AddHuntComponent } from './hunts/add-hunt.component';
import { HuntProfileComponent } from './hunts/hunt-profile.component';


// Note that the 'users/new' route needs to come before 'users/:id'.
// If 'users/:id' came first, it would accidentally catch requests to
// 'users/new'; the router would just think that the string 'new' is a user ID.
const routes: Routes = [
  {path: '', component: HomeComponent, title: 'Home'},
  {path: 'hunts', component: HuntListComponent, title: 'Hunts'},
  {path: 'hunts/new', component: AddHuntComponent, title: 'Add Hunt'},
  {path: 'hunts/:id', component: HuntProfileComponent, title: 'User Profile'},
  {path: 'player', component: PlayerComponent, title: 'Player'},




];

@NgModule({
  imports: [RouterModule.forRoot(routes), HttpClientModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
