import {Component, input} from '@angular/core';
import {Feed} from '@app/share/model/feed';
import {MatCard, MatCardActions, MatCardContent, MatCardImage} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {FormatDatePipe} from '@app/share/pipe/format-date.pipe';

@Component({
  selector: 'app-feed',
  imports: [
    MatCard,
    MatCardContent,
    MatCardImage,
    MatCardActions,
    MatButton,
    FormatDatePipe
  ],
  templateUrl: './feed.component.html',
  styleUrl: './feed.component.scss'
})
export class FeedComponent {
  feedSignal = input.required<Feed>({
    alias: 'feed'
  })
}
