import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'bikeRentalAppApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'customer',
    data: { pageTitle: 'bikeRentalAppApp.customer.home.title' },
    loadChildren: () => import('./customer/customer.routes'),
  },
  {
    path: 'customer-profile',
    data: { pageTitle: 'bikeRentalAppApp.customerProfile.home.title' },
    loadChildren: () => import('./customer-profile/customer-profile.routes'),
  },
  {
    path: 'bike',
    data: { pageTitle: 'bikeRentalAppApp.bike.home.title' },
    loadChildren: () => import('./bike/bike.routes'),
  },
  {
    path: 'rental',
    data: { pageTitle: 'bikeRentalAppApp.rental.home.title' },
    loadChildren: () => import('./rental/rental.routes'),
  },
  {
    path: 'category',
    data: { pageTitle: 'bikeRentalAppApp.category.home.title' },
    loadChildren: () => import('./category/category.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
