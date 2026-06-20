import dayjs from 'dayjs/esm';

import { IBike, NewBike } from './bike.model';

export const sampleWithRequiredData: IBike = {
  id: 14937,
  name: 'or',
  serialNumber: 'triumphantly zowie',
  bikeType: 'CITY',
  pricePerHour: 12502.83,
  available: false,
};

export const sampleWithPartialData: IBike = {
  id: 457,
  name: 'climb',
  serialNumber: 'memorable',
  bikeType: 'CITY',
  pricePerHour: 9690.77,
  available: true,
  productionDate: dayjs('2026-06-11'),
};

export const sampleWithFullData: IBike = {
  id: 28560,
  name: 'disrespect at',
  serialNumber: 'strictly',
  bikeType: 'CITY',
  pricePerHour: 14619.53,
  available: true,
  productionDate: dayjs('2026-06-11'),
};

export const sampleWithNewData: NewBike = {
  name: 'wearily',
  serialNumber: 'notwithstanding polished',
  bikeType: 'ELECTRIC',
  pricePerHour: 19802.78,
  available: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
