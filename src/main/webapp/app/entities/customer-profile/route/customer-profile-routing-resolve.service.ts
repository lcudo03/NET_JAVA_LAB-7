import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, catchError, of } from 'rxjs';

import { ICustomerProfile } from '../customer-profile.model';
import { CustomerProfileService } from '../service/customer-profile.service';

const customerProfileResolve = (route: ActivatedRouteSnapshot): Observable<null | ICustomerProfile> => {
  const { id } = route.params;
  if (id) {
    const router = inject(Router);
    const service = inject(CustomerProfileService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default customerProfileResolve;
