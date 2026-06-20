import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import BikeResolve from './route/bike-routing-resolve.service';

const bikeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/bike').then(m => m.Bike),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/bike-detail').then(m => m.BikeDetail),
    resolve: {
      bike: BikeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/bike-update').then(m => m.BikeUpdate),
    resolve: {
      bike: BikeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/bike-update').then(m => m.BikeUpdate),
    resolve: {
      bike: BikeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default bikeRoute;
