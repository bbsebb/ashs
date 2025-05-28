import {TimeSlot} from '../../model/time-slot';
import {TrainingSession} from '@app/share/model/training-session';
import {FormTrainingSessionDTO} from '@app/share/service/dto/form-training-session-d-t-o';

export interface AddTrainingSessionInTeamDTORequest {
  trainingSessionDTORequest: {
    timeSlot: TimeSlot
  };
  hallId: number;
}

export function toAddTrainingSessionInTeamDTORequest(trainingSession: TrainingSession | FormTrainingSessionDTO): AddTrainingSessionInTeamDTORequest {
  return {
    trainingSessionDTORequest: {
      timeSlot: trainingSession.timeSlot
    },
    hallId: trainingSession.hall.id,
  }
}



