import { HttpInterceptorFn } from '@angular/common/http';

export const devKeyInterceptor: HttpInterceptorFn = (req, next) => {
  const devKey = 'admin-token';
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${devKey}`
    }
  });
  return next(authReq);
};
