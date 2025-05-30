import {inject, Injectable} from '@angular/core';
import {HalFormService} from '@app/share/service/hal-form.service';
import {iif, switchMap} from 'rxjs';
import {AllHalResources, PaginatedHalResource} from '@app/share/model/hal/hal';
import {PaginationOption} from '@app/share/service/pagination-option';
import {Hall} from '@app/share/model/hall';
import {CreateHallDTORequest} from '@app/share/service/dto/create-hall-d-t-o-request';
import {
  ConfirmationDialogComponent
} from '@app/share/component/dialog/confirmation-dialog/confirmation-dialog.component';
import {MatDialog} from '@angular/material/dialog';

@Injectable({
  providedIn: 'root'
})
export class HallService {
  private static readonly PAGINATION_OPTION_DEFAULT: PaginationOption = {
    size: 20,
    page: 0
  };
  private readonly halFormService = inject(HalFormService);
  private readonly matDialog = inject(MatDialog);

  constructor() {
  }


  getHalls(paginationOption: PaginationOption = HallService.PAGINATION_OPTION_DEFAULT) {
    return this.halFormService.root.pipe(
      switchMap((root) =>
        iif(
          () => paginationOption == 'all',
          this.halFormService.follow<AllHalResources<Hall>>(root, "allHalls"),
          this.halFormService.follow<PaginatedHalResource<Hall>>(root, "halls", this.halFormService.buildParamPage(paginationOption))
        )
      )
    );
  }

  createHall(hallResource: AllHalResources<Hall> | PaginatedHalResource<Hall>, createHallDTORequest: CreateHallDTORequest) {
    if (!this.halFormService.canAction(hallResource, 'createHall')) {
      throw new Error("L'action createHall n'est pas disponible sur l'objet " + createHallDTORequest);
    }
    return this.halFormService.doAction<Hall>(hallResource, 'createHall', createHallDTORequest);
  }

  createDeleteConfirmation(hall: Hall) {
    return this.matDialog.open(ConfirmationDialogComponent, {
      data: {
        title: 'Suppression',
        content: `Etes-vous sur de vouloir supprimer : ${hall.name} ?`
      },
    });
  }

  deleteTeam(hall: Hall) {
    if (!this.halFormService.canAction(hall, 'deleteHall')) {
      throw new Error("L'action deleteHall n'est pas disponible sur l'objet " + hall);
    }
    return this.halFormService.doAction<void>(hall, 'deleteHall');
  }
}
