import {Routes} from '@angular/router';
import {TeamsComponent} from '@app/feature/teams/teams.component';
import {PageNotFoundComponent} from '@app/feature/page-not-found/page-not-found.component';
import {TeamComponent} from '@app/feature/teams/team/team.component';
import {FormTeamComponent} from '@app/feature/teams/form-team/form-team.component';
import {CoachesComponent} from '@app/feature/coaches/coaches.component';
import {HallsComponent} from '@app/feature/halls/halls.component';
import {TestComponent} from '@app/feature/test/test.component';
import {HallComponent} from '@app/feature/halls/hall/hall.component';
import {FormHallComponent} from '@app/feature/halls/form-hall/form-hall.component';
import {FormCoachComponent} from '@app/feature/coaches/form-coach/form-coach.component';
import {CoachComponent} from '@app/feature/coaches/coach/coach.component';

export const routes: Routes = [
  {path: 'home', component: TestComponent},
  {
    path: 'halls',
    children: [
      {path: '', component: HallsComponent},
      {path: 'create', component: FormHallComponent},
      {path: ':uri/update', component: FormHallComponent},
      {path: ':uri', component: HallComponent},
    ]
  },
  {
    path: 'coaches',
    children: [
      {path: '', component: CoachesComponent},
      {path: 'create', component: FormCoachComponent},
      {path: ':uri/update', component: FormCoachComponent},
      {path: ':uri', component: CoachComponent},
    ]
  },
  {
    path: 'teams',
    children: [
      {path: '', component: TeamsComponent},
      {path: 'create', component: FormTeamComponent},
      {path: ':uri/update', component: FormTeamComponent},
      {path: ':uri', component: TeamComponent},
    ]
  },
  {path: '', redirectTo: '/home', pathMatch: 'full'}, // redirect to `first-component`
  {path: '**', component: PageNotFoundComponent},
];
