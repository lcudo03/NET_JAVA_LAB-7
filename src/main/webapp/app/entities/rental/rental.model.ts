import dayjs from 'dayjs/esm';

import { IBike } from 'app/entities/bike/bike.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { RentalStatus } from 'app/entities/enumerations/rental-status.model';

export interface IRental {
  id: number;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  totalPrice?: number | null;
  status?: keyof typeof RentalStatus | null;
  customer?: Pick<ICustomer, 'id'> | null;
  bike?: Pick<IBike, 'id'> | null;
}

export type NewRental = Omit<IRental, 'id'> & { id: null };
