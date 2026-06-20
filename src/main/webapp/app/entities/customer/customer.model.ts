import dayjs from 'dayjs/esm';

export interface ICustomer {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
  registrationDate?: dayjs.Dayjs | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
