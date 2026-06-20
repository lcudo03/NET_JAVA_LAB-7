import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IBike, NewBike } from '../bike.model';

export type PartialUpdateBike = Partial<IBike> & Pick<IBike, 'id'>;

type RestOf<T extends IBike | NewBike> = Omit<T, 'productionDate'> & {
  productionDate?: string | null;
};

export type RestBike = RestOf<IBike>;

export type NewRestBike = RestOf<NewBike>;

export type PartialUpdateRestBike = RestOf<PartialUpdateBike>;

@Injectable()
export class BikesService {
  readonly bikesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(undefined);
  readonly bikesResource = httpResource<RestBike[]>(() => {
    const params = this.bikesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of bike that have been fetched. It is updated when the bikesResource emits a new value.
   * In case of error while fetching the bikes, the signal is set to an empty array.
   */
  readonly bikes = computed(() =>
    (this.bikesResource.hasValue() ? this.bikesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/bikes');

  protected convertValueFromServer(restBike: RestBike): IBike {
    return {
      ...restBike,
      productionDate: restBike.productionDate ? dayjs(restBike.productionDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class BikeService extends BikesService {
  protected readonly http = inject(HttpClient);

  create(bike: NewBike): Observable<IBike> {
    const copy = this.convertValueFromClient(bike);
    return this.http.post<RestBike>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(bike: IBike): Observable<IBike> {
    const copy = this.convertValueFromClient(bike);
    return this.http
      .put<RestBike>(`${this.resourceUrl}/${encodeURIComponent(this.getBikeIdentifier(bike))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(bike: PartialUpdateBike): Observable<IBike> {
    const copy = this.convertValueFromClient(bike);
    return this.http
      .patch<RestBike>(`${this.resourceUrl}/${encodeURIComponent(this.getBikeIdentifier(bike))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IBike> {
    return this.http.get<RestBike>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IBike[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestBike[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getBikeIdentifier(bike: Pick<IBike, 'id'>): number {
    return bike.id;
  }

  compareBike(o1: Pick<IBike, 'id'> | null, o2: Pick<IBike, 'id'> | null): boolean {
    return o1 && o2 ? this.getBikeIdentifier(o1) === this.getBikeIdentifier(o2) : o1 === o2;
  }

  addBikeToCollectionIfMissing<Type extends Pick<IBike, 'id'>>(
    bikeCollection: Type[],
    ...bikesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bikes: Type[] = bikesToCheck.filter(isPresent);
    if (bikes.length > 0) {
      const bikeCollectionIdentifiers = bikeCollection.map(bikeItem => this.getBikeIdentifier(bikeItem));
      const bikesToAdd = bikes.filter(bikeItem => {
        const bikeIdentifier = this.getBikeIdentifier(bikeItem);
        if (bikeCollectionIdentifiers.includes(bikeIdentifier)) {
          return false;
        }
        bikeCollectionIdentifiers.push(bikeIdentifier);
        return true;
      });
      return [...bikesToAdd, ...bikeCollection];
    }
    return bikeCollection;
  }

  protected convertValueFromClient<T extends IBike | NewBike | PartialUpdateBike>(bike: T): RestOf<T> {
    return {
      ...bike,
      productionDate: bike.productionDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestBike): IBike {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestBike[]): IBike[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
