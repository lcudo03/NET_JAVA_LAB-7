import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CustomerProfileResolve from './route/customer-profile-routing-resolve.service';

const customerProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/customer-profile').then(m => m.CustomerProfile),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/customer-profile-detail').then(m => m.CustomerProfileDetail),
    resolve: {
      customerProfile: CustomerProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/customer-profile-update').then(m => m.CustomerProfileUpdate),
    resolve: {
      customerProfile: CustomerProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/customer-profile-update').then(m => m.CustomerProfileUpdate),
    resolve: {
      customerProfile: CustomerProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default customerProfileRoute;
