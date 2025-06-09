import {TrainingSession} from '../model/training-session';

export interface DeleteTrainingSessionDTORequest {
  id: number;
}

export function toDeleteTrainingSessionDTORequest(trainingSession: TrainingSession): DeleteTrainingSessionDTORequest {
  return {
    id: trainingSession.id
  }
}
