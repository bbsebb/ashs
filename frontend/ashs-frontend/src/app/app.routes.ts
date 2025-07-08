import {Routes} from '@angular/router';
import {MentionsLegalesComponent} from './feature/legals/mentions-legales/mentions-legales.component';
import {RgpdComponent} from './feature/legals/rgpd/rgpd.component';
import {PageNotFoundComponent} from './feature/page-not-found/page-not-found.component';
import {CoachesComponent} from './feature/coaches/coaches.component';
import {CoachComponent} from './feature/coaches/coach/coach.component';
import {HallsComponent} from './feature/halls/halls.component';
import {HallComponent} from './feature/halls/hall/hall.component';
import {TeamsComponent} from './feature/teams/teams.component';
import {TeamComponent} from './feature/teams/team/team.component';
import {ContactComponent} from './feature/contact/contact.component';
import {FacebookMediaComponent} from '@app/feature/instagram/facebook-media.component';

export const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'home', component: FacebookMediaComponent},
  {
    path: 'coaches',
    children: [
      {path: '', component: CoachesComponent},
      {path: ':uri', component: CoachComponent},
    ]
  },
  {
    path: 'halls',
    children: [
      {path: '', component: HallsComponent},
      {path: ':uri', component: HallComponent},
    ]
  },
  {
    path: 'teams',
    children: [
      {path: '', component: TeamsComponent},
      {path: ':uri', component: TeamComponent},
    ]
  },
  {path: 'coaches/:id', component: CoachComponent},
  {path: 'mentions-legales', component: MentionsLegalesComponent},
  {path: 'rgpd', component: RgpdComponent},
  {path: 'contact', component: ContactComponent},
  {path: '**', component: PageNotFoundComponent},
];
