import {computed, inject, Injectable, signal} from '@angular/core';
import {CoachesStore} from './coaches.store';
import {COACH_SERVICE} from '../service/i-coach.service';
import {NGX_LOGGER} from 'ngx-logger';
import {CreateCoachDTORequest} from 'ngx-training';

@Injectable()
export class CoachStore {
  private readonly coachService = inject(COACH_SERVICE);
  private readonly coachesStore = inject(CoachesStore);
  private readonly logger = inject(NGX_LOGGER);
  private readonly _uri = signal<string | undefined>(undefined);
  private readonly coachSignal = computed(() => this.coachesStore.coaches().find(coach => coach._links.self.href === this._uri()));

  constructor() {


  }

  /**
   * Sets the URI and triggers a resource reload
   * @param uri The new URI
   */
  set uri(uri: string | undefined) {
    this._uri.set(uri);
  }


  get coach() {
    return this.coachSignal;
  }

  updateCoach(updateCoachDTORequest: CreateCoachDTORequest) {

  }

}
