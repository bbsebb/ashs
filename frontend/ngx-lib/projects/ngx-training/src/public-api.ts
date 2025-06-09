/*
 * Public API Surface of ngx-training
 */

// DTO exports
export * from './lib/dto/add-role-coach-in-team-d-t-o-request';
export * from './lib/dto/add-training-session-in-team-d-t-o-request';
export * from './lib/dto/create-coach-d-t-o-request';
export * from './lib/dto/create-hall-d-t-o-request';
export * from './lib/dto/create-team-d-t-o-request';
export * from './lib/dto/delete-role-coach-d-t-o-request';
export * from './lib/dto/delete-training-session-d-t-o-request';
export * from './lib/dto/form-role-coach-d-t-o';
export * from './lib/dto/form-training-session-d-t-o';
export * from './lib/dto/update-hall-d-t-o-request';
export * from './lib/dto/update-team-d-t-o-request';

// Model exports
export * from './lib/model/address';
export * from './lib/model/category';
export * from './lib/model/coach';
export * from './lib/model/day-of-week';
export * from './lib/model/gender';
export * from './lib/model/hall';
export * from './lib/model/role-coach';
export * from './lib/model/role';
export * from './lib/model/team';
export * from './lib/model/time-slot';
export * from './lib/model/time';
export * from './lib/model/training-session';

// Pipe exports
export * from './lib/pipe/category.pipe';
export * from './lib/pipe/day-of-week.pipe';
export * from './lib/pipe/gender.pipe';
export * from './lib/pipe/role-to-french.pipe';
export * from './lib/pipe/time.pipe';

// Service exports
export * from './lib/service/i-coach.service';
export * from './lib/service/i-hall.service';
export * from './lib/service/i-team.service';
export * from './lib/service/coach.service';
export * from './lib/service/hall.service';
export * from './lib/service/team.service';
export * from './lib/service/stub/coach-stub.service'
export * from './lib/service/stub/hall-stub.service'
export * from './lib/service/stub/team-stub.service'

// Store exports
export * from './lib/store/coach.store';
export * from './lib/store/coaches.store';
export * from './lib/store/hall.store';
export * from './lib/store/halls.store';
export * from './lib/store/team.store';
export * from './lib/store/teams.store';
