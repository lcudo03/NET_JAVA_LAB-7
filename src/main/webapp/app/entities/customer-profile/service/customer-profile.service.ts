import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICustomerProfile, NewCustomerProfile } from '../customer-profile.model';

export type PartialUpdateCustomerProfile = Partial<ICustomerProfile> & Pick<ICustomerProfile, 'id'>;

@Injectable()
export class CustomerProfilesService {
  readonly customerProfilesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly customerProfilesResource = httpResource<ICustomerProfile[]>(() => {
    const params = this.customerProfilesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of customerProfile that have been fetched. It is updated when the customerProfilesResource emits a new value.
   * In case of error while fetching the customerProfiles, the signal is set to an empty array.
   */
  readonly customerProfiles = computed(() => (this.customerProfilesResource.hasValue() ? this.customerProfilesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/customer-profiles');
}

@Injectable({ providedIn: 'root' })
export class CustomerProfileService extends CustomerProfilesService {
  protected readonly http = inject(HttpClient);

  create(customerProfile: NewCustomerProfile): Observable<ICustomerProfile> {
    return this.http.post<ICustomerProfile>(this.resourceUrl, customerProfile);
  }

  update(customerProfile: ICustomerProfile): Observable<ICustomerProfile> {
    return this.http.put<ICustomerProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCustomerProfileIdentifier(customerProfile))}`,
      customerProfile,
    );
  }

  partialUpdate(customerProfile: PartialUpdateCustomerProfile): Observable<ICustomerProfile> {
    return this.http.patch<ICustomerProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getCustomerProfileIdentifier(customerProfile))}`,
      customerProfile,
    );
  }

  find(id: number): Observable<ICustomerProfile> {
    return this.http.get<ICustomerProfile>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICustomerProfile[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICustomerProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCustomerProfileIdentifier(customerProfile: Pick<ICustomerProfile, 'id'>): number {
    return customerProfile.id;
  }

  compareCustomerProfile(o1: Pick<ICustomerProfile, 'id'> | null, o2: Pick<ICustomerProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getCustomerProfileIdentifier(o1) === this.getCustomerProfileIdentifier(o2) : o1 === o2;
  }

  addCustomerProfileToCollectionIfMissing<Type extends Pick<ICustomerProfile, 'id'>>(
    customerProfileCollection: Type[],
    ...customerProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const customerProfiles: Type[] = customerProfilesToCheck.filter(isPresent);
    if (customerProfiles.length > 0) {
      const customerProfileCollectionIdentifiers = customerProfileCollection.map(customerProfileItem =>
        this.getCustomerProfileIdentifier(customerProfileItem),
      );
      const customerProfilesToAdd = customerProfiles.filter(customerProfileItem => {
        const customerProfileIdentifier = this.getCustomerProfileIdentifier(customerProfileItem);
        if (customerProfileCollectionIdentifiers.includes(customerProfileIdentifier)) {
          return false;
        }
        customerProfileCollectionIdentifiers.push(customerProfileIdentifier);
        return true;
      });
      return [...customerProfilesToAdd, ...customerProfileCollection];
    }
    return customerProfileCollection;
  }
}
