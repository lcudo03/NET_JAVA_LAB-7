import { IBike } from 'app/entities/bike/bike.model';

export interface ICategory {
  id: number;
  name?: string | null;
  description?: string | null;
  bikeses?: Pick<IBike, 'id'>[] | null;
}

export type NewCategory = Omit<ICategory, 'id'> & { id: null };
