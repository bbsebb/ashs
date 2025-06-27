import {Component, inject, Signal} from '@angular/core';
import {InstagramService} from '@app/share/service/instagram.service';
import {InstagramStore} from '@app/share/service/instagram.store';
import {Feed} from '@app/share/model/feed';
import {FeedComponent} from '@app/feature/instagram/feed/feed.component';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-instagram',
  imports: [
    FeedComponent,
    MatButton
  ],
  templateUrl: './instagram.component.html',
  styleUrl: './instagram.component.scss'
})
export class InstagramComponent {
  private readonly instagramService = inject(InstagramService);
  private readonly instagramStore = inject(InstagramStore);
  feedsSignal: Signal<Feed[]> = this.instagramStore.getFeeds();
  paginationSignal = this.instagramStore.getPagination();

  constructor() {
  }

  next() {
    this.instagramStore.next();
  }
}
