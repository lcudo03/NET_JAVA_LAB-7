import { ICustomer } from 'app/entities/customer/customer.model';

export interface ICustomerProfile {
  id: number;
  address?: string | null;
  city?: string | null;
  loyaltyPoints?: number | null;
  verified?: boolean | null;
  customer?: Pick<ICustomer, 'id'> | null;
}

export type NewCustomerProfile = Omit<ICustomerProfile, 'id'> & { id: null };
