import dayjs from 'dayjs/esm';

import { IRental, NewRental } from './rental.model';

export const sampleWithRequiredData: IRental = {
  id: 5216,
  startDate: dayjs('2026-06-11'),
  status: 'ACTIVE',
};

export const sampleWithPartialData: IRental = {
  id: 16924,
  startDate: dayjs('2026-06-11'),
  totalPrice: 1801.83,
  status: 'CANCELLED',
};

export const sampleWithFullData: IRental = {
  id: 4722,
  startDate: dayjs('2026-06-11'),
  endDate: dayjs('2026-06-11'),
  totalPrice: 2444.61,
  status: 'ACTIVE',
};

export const sampleWithNewData: NewRental = {
  startDate: dayjs('2026-06-11'),
  status: 'FINISHED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
