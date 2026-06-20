import dayjs from 'dayjs/esm';

import { ICategory } from 'app/entities/category/category.model';
import { BikeType } from 'app/entities/enumerations/bike-type.model';

export interface IBike {
  id: number;
  name?: string | null;
  serialNumber?: string | null;
  bikeType?: keyof typeof BikeType | null;
  pricePerHour?: number | null;
  available?: boolean | null;
  productionDate?: dayjs.Dayjs | null;
  categorieses?: Pick<ICategory, 'id'>[] | null;
}

export type NewBike = Omit<IBike, 'id'> & { id: null };
