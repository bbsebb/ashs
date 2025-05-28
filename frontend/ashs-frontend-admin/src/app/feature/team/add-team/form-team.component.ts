import {Component, computed, effect, inject, input, linkedSignal, signal, WritableSignal} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Gender} from '@app/share/model/gender';
import {GenderPipe} from '@app/share/pipe/gender.pipe';
import {Category} from '@app/share/model/category';
import {CategoryPipe} from '@app/share/pipe/category.pipe';
import {MatDivider} from '@angular/material/divider';
import {MatButton, MatFabButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatDialog} from '@angular/material/dialog';
import {Router} from '@angular/router';
import {AddTrainingSessionDialogComponent} from './add-training-session/add-training-session-dialog.component';
import {displayError, hasError} from '@app/share/validator/form-error.util';
import {DayOfWeekPipe} from '@app/share/pipe/day-of-week.pipe';
import {MatList, MatListItem} from '@angular/material/list';
import {TimePipe} from '@app/share/pipe/time.pipe';
import {
  AddRoleCoachDialogComponent
} from '@app/feature/team/add-team/add-role-coach-dialog/add-role-coach-dialog.component';
import {RoleToFrenchPipe} from '@app/share/pipe/role-to-french.pipe';
import {CreateTeamDTORequest} from '@app/share/service/dto/create-team-d-t-o-request';
import {TeamsStore} from '@app/share/store/teams.store';
import {RoleCoach} from '@app/share/model/role-coach';
import {NotificationService} from '@app/share/service/notification.service';
import {ApiError} from '@app/share/model/api-error';
import {TeamStore} from '@app/share/store/team.store';
import {Team} from '@app/share/model/team';
import {FormTrainingSessionDTO} from '@app/share/service/dto/form-training-session-d-t-o';
import {UpdateTeamDTORequest} from '@app/share/service/dto/update-team-d-t-o-request';
import {FormRoleCoachDTO} from '@app/share/service/dto/form-role-coach-d-t-o';
import {tap} from 'rxjs/operators';


@Component({
  selector: 'app-form-team',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    MatOption,
    MatSelect,
    MatRadioGroup,
    MatRadioButton,
    ReactiveFormsModule,
    GenderPipe,
    CategoryPipe,
    MatDivider,
    MatFabButton,
    MatIcon,
    MatError,
    MatButton,
    DayOfWeekPipe,
    MatList,
    MatListItem,
    TimePipe,
    MatIconButton,
    RoleToFrenchPipe
  ],
  templateUrl: './form-team.component.html',
  styleUrl: './form-team.component.css'
})
export class FormTeam {
  private readonly formBuild = inject(NonNullableFormBuilder);
  private readonly matDialog = inject(MatDialog);
  private readonly teamsStore = inject(TeamsStore);
  readonly teamStore = inject(TeamStore);
  private readonly notificationService = inject(NotificationService);
  private readonly router = inject(Router);
  uri = input<string>();
  trainingSessionsSignal: WritableSignal<FormTrainingSessionDTO[]> = signal([]);
  roleCoachesSignal: WritableSignal<FormRoleCoachDTO[]> = signal([]);
  teamForm = this.createTeamForm();
  isCreateSignal;


  constructor() {
    effect(() => this.teamStore.uri = this.uri());
    effect(() => {
      const team = this.teamStore.teamResource.value();
      this.teamForm = this.createTeamForm(team);
    });
    this.trainingSessionsSignal = linkedSignal(() => this.teamStore.trainingSessionResource.value() ?? [])
    this.roleCoachesSignal = linkedSignal(() => this.teamStore.roleCoachResource.value() ?? [])
    this.isCreateSignal = computed(() => this.uri() === undefined);
  }


  protected readonly Object = Object;
  protected readonly Gender = Gender;
  protected readonly Category = Category;

  private createTeamForm(team?: Partial<Team>) {
    return this.buildTeamForm({
      category: team?.category ?? undefined,
      gender: team?.gender ?? undefined,
      teamNumber: team?.teamNumber ?? 1,
    });
  }

  private buildTeamForm(values: { category?: Category, gender?: Gender, teamNumber?: number }) {
    return this.formBuild.group({
      category: this.formBuild.control<Category | undefined>(values.category, Validators.required),
      gender: this.formBuild.control<Gender | undefined>(values.gender, Validators.required),
      teamNumber: [values.teamNumber, [Validators.required, Validators.min(1)]],
    });
  }


  addTrainingSession() {
    const matDialogRef = this.matDialog.open<AddTrainingSessionDialogComponent, any, FormTrainingSessionDTO>(AddTrainingSessionDialogComponent);
    matDialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.trainingSessionsSignal.update((ts) => {
          ts.push(res)
          return ts;
        })
      }
    })
  }

  addCoach() {
    const matDialogRef = this.matDialog.open<AddRoleCoachDialogComponent, any, RoleCoach>(AddRoleCoachDialogComponent);
    matDialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.roleCoachesSignal.update((rc) => {
          rc.push(res)
          return rc;
        })
      }
    })
  }

  deleteTrainingSession(trainingSession: FormTrainingSessionDTO) {
    this.trainingSessionsSignal.update((trainingSessions) => trainingSessions.filter(ts => ts !== trainingSession));
  }

  deleteCoach(roleCoach: FormRoleCoachDTO) {
    this.roleCoachesSignal.update((roleCoaches) => roleCoaches.filter(rc => rc !== roleCoach));
  }


  submit() {
    if (this.isCreateSignal()) {
      this.createTeam();
    } else {
      this.updateTeam();
    }
  }

  createTeam() {
    this.teamsStore.createTeam(
      this.teamForm.getRawValue() as CreateTeamDTORequest,
      this.trainingSessionsSignal(),
      this.roleCoachesSignal())
      .subscribe({
        next: () => this.notificationService.showSuccess(`L'équipe a été crée`),
        error: err => this.notificationService.showError(ApiError.of(err.error).getMessageForField('teamDTOCreateRequest'))
      });
  }

  updateTeam() {
    this.teamStore.updateTeam(
      this.teamForm.getRawValue() as UpdateTeamDTORequest,
      this.trainingSessionsSignal(),
      this.roleCoachesSignal()
    ).pipe(tap(() => console.log("test"))).subscribe({
      next: () => this.notificationService.showSuccess(`L'équipe a été modifiée`),
      error: err => this.notificationService.showError(ApiError.of(err.error).getMessageForField('teamDTOCreateRequest'))
    })
  }


  protected readonly hasError = hasError;
  protected readonly displayError = displayError;

  goBack() {
    this.router.navigate(['/teams']);
  }
}
