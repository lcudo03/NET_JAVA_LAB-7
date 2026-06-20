import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICustomer, NewCustomer } from '../customer.model';

export type PartialUpdateCustomer = Partial<ICustomer> & Pick<ICustomer, 'id'>;

type RestOf<T extends ICustomer | NewCustomer> = Omit<T, 'registrationDate'> & {
  registrationDate?: string | null;
};

export type RestCustomer = RestOf<ICustomer>;

export type NewRestCustomer = RestOf<NewCustomer>;

export type PartialUpdateRestCustomer = RestOf<PartialUpdateCustomer>;

@Injectable()
export class CustomersService {
  readonly customersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly customersResource = httpResource<RestCustomer[]>(() => {
    const params = this.customersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of customer that have been fetched. It is updated when the customersResource emits a new value.
   * In case of error while fetching the customers, the signal is set to an empty array.
   */
  readonly customers = computed(() =>
    (this.customersResource.hasValue() ? this.customersResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/customers');

  protected convertValueFromServer(restCustomer: RestCustomer): ICustomer {
    return {
      ...restCustomer,
      registrationDate: restCustomer.registrationDate ? dayjs(restCustomer.registrationDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CustomerService extends CustomersService {
  protected readonly http = inject(HttpClient);

  create(customer: NewCustomer): Observable<ICustomer> {
    const copy = this.convertValueFromClient(customer);
    return this.http.post<RestCustomer>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(customer: ICustomer): Observable<ICustomer> {
    const copy = this.convertValueFromClient(customer);
    return this.http
      .put<RestCustomer>(`${this.resourceUrl}/${encodeURIComponent(this.getCustomerIdentifier(customer))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(customer: PartialUpdateCustomer): Observable<ICustomer> {
    const copy = this.convertValueFromClient(customer);
    return this.http
      .patch<RestCustomer>(`${this.resourceUrl}/${encodeURIComponent(this.getCustomerIdentifier(customer))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICustomer> {
    return this.http
      .get<RestCustomer>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICustomer[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCustomer[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCustomerIdentifier(customer: Pick<ICustomer, 'id'>): number {
    return customer.id;
  }

  compareCustomer(o1: Pick<ICustomer, 'id'> | null, o2: Pick<ICustomer, 'id'> | null): boolean {
    return o1 && o2 ? this.getCustomerIdentifier(o1) === this.getCustomerIdentifier(o2) : o1 === o2;
  }

  addCustomerToCollectionIfMissing<Type extends Pick<ICustomer, 'id'>>(
    customerCollection: Type[],
    ...customersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const customers: Type[] = customersToCheck.filter(isPresent);
    if (customers.length > 0) {
      const customerCollectionIdentifiers = customerCollection.map(customerItem => this.getCustomerIdentifier(customerItem));
      const customersToAdd = customers.filter(customerItem => {
        const customerIdentifier = this.getCustomerIdentifier(customerItem);
        if (customerCollectionIdentifiers.includes(customerIdentifier)) {
          return false;
        }
        customerCollectionIdentifiers.push(customerIdentifier);
        return true;
      });
      return [...customersToAdd, ...customerCollection];
    }
    return customerCollection;
  }

  protected convertValueFromClient<T extends ICustomer | NewCustomer | PartialUpdateCustomer>(customer: T): RestOf<T> {
    return {
      ...customer,
      registrationDate: customer.registrationDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCustomer): ICustomer {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCustomer[]): ICustomer[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
