import { ICustomerProfile, NewCustomerProfile } from './customer-profile.model';

export const sampleWithRequiredData: ICustomerProfile = {
  id: 32117,
  address: 'amnesty though sedately',
  city: 'East Cary',
  verified: true,
};

export const sampleWithPartialData: ICustomerProfile = {
  id: 18632,
  address: 'stealthily up intellect',
  city: 'New Antonioburgh',
  loyaltyPoints: 16855,
  verified: false,
};

export const sampleWithFullData: ICustomerProfile = {
  id: 11021,
  address: 'grouper in affiliate',
  city: 'Strackestead',
  loyaltyPoints: 12910,
  verified: true,
};

export const sampleWithNewData: NewCustomerProfile = {
  address: 'ah',
  city: 'West Violet',
  verified: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
