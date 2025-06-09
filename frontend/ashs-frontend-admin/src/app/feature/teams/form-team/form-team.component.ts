import {Component, computed, effect, inject, input, linkedSignal, Signal, signal, WritableSignal} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatOption} from '@angular/material/core';
import {MatSelect} from '@angular/material/select';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatDivider} from '@angular/material/divider';
import {MatButton, MatFabButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatDialog} from '@angular/material/dialog';
import {Router, RouterLink} from '@angular/router';
import {AddTrainingSessionDialogComponent} from './add-training-session/add-training-session-dialog.component';
import {displayError, hasError} from '@app/share/util/form-error.util';
import {MatList, MatListItem} from '@angular/material/list';
import {
  AddRoleCoachDialogComponent
} from '@app/feature/teams/form-team/add-role-coach-dialog/add-role-coach-dialog.component';
import {NotificationService} from '@app/share/service/notification.service';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {
  Category,
  CategoryPipe,
  CreateTeamDTORequest,
  DayOfWeekPipe,
  FormRoleCoachDTO,
  FormTrainingSessionDTO,
  Gender,
  GenderPipe,
  RoleCoach,
  RoleToFrenchPipe,
  Team,
  TeamsStore,
  TeamStore,
  TimePipe,
  UpdateTeamDTORequest
} from 'ngx-training';
import {NgxApiError} from 'ngx-hal-forms';


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
    RoleToFrenchPipe,
    MatProgressBar,
    MatProgressSpinner,
    RouterLink,
  ],
  templateUrl: './form-team.component.html',
  styleUrl: './form-team.component.css'
})
export class FormTeamComponent {
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
  isCreateSignal: Signal<boolean>;
  isSubmitting: WritableSignal<boolean> = signal(false);


  constructor() {
    effect(() => this.teamStore.uri = this.uri());
    effect(() => this.teamForm = this.createTeamForm(this.teamStore.team()));
    this.trainingSessionsSignal = linkedSignal(() => this.teamStore.trainingSession() ?? [])
    this.roleCoachesSignal = linkedSignal(() => this.teamStore.roleCoach() ?? [])
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
    if (!this.teamForm.invalid) {
      if (this.isCreateSignal()) {
        this.createTeam();
      } else {
        this.updateTeam();
      }
    }
  }

  createTeam() {
    this.isSubmitting.set(true);
    this.teamsStore.createTeam(
      this.teamForm.getRawValue() as CreateTeamDTORequest,
      this.trainingSessionsSignal(),
      this.roleCoachesSignal())
      .subscribe({
        next: () => {
          this.notificationService.showSuccess(`L'équipe a été crée`);
          this.goBack();
          this.isSubmitting.set(false);
        },
        error: err => {
          this.notificationService.showError(NgxApiError.of(err.error).getMessageForField('teamDTOCreateRequest'));
          this.isSubmitting.set(false);
        }
      });
  }

  updateTeam() {
    this.isSubmitting.set(true);
    this.teamStore.updateTeam(
      this.teamForm.getRawValue() as UpdateTeamDTORequest,
      this.trainingSessionsSignal(),
      this.roleCoachesSignal()
    ).subscribe({
      next: () => {
        this.notificationService.showSuccess(`L'équipe a été modifiée`);
        this.isSubmitting.set(false);
      },
      error: err => {
        this.notificationService.showError(NgxApiError.of(err.error).getMessageForField('teamDTOCreateRequest'));
        this.isSubmitting.set(false);
      }
    })
  }


  protected readonly hasError = hasError;
  protected readonly displayError = displayError;

  goBack() {
    void this.router.navigate(['/teams']);
  }

  protected readonly encodeURIComponent = encodeURIComponent;
}
