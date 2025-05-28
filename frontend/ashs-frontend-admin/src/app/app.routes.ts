import {Routes} from '@angular/router';
import {TestComponent} from './feature/test/test.component';
import {HallComponent} from './feature/hall/hall.component';
import {TeamsComponent} from './feature/team/teams.component';
import {PageNotFoundComponent} from './feature/page-not-found/page-not-found.component';
import {TeamComponent} from './feature/team/team/team.component';
import {FormTeam} from './feature/team/add-team/form-team.component';

export const routes: Routes = [
  {path: 'home', component: TestComponent},
  {path: 'hall', component: HallComponent},
  {
    path: 'teams',
    children: [
      {path: '', component: TeamsComponent},
      {path: 'create', component: FormTeam},
      {path: ':uri/update', component: FormTeam},
      {path: ':uri', component: TeamComponent},
    ]
  },
  {path: '', redirectTo: '/home', pathMatch: 'full'}, // redirect to `first-component`
  {path: '**', component: PageNotFoundComponent},
];
