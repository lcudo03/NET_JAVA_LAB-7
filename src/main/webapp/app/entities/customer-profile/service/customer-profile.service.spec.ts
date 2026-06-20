import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICustomerProfile } from '../customer-profile.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../customer-profile.test-samples';

import { CustomerProfileService } from './customer-profile.service';

const requireRestSample: ICustomerProfile = {
  ...sampleWithRequiredData,
};

describe('CustomerProfile Service', () => {
  let service: CustomerProfileService;
  let httpMock: HttpTestingController;
  let expectedResult: ICustomerProfile | ICustomerProfile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CustomerProfileService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a CustomerProfile', () => {
      const customerProfile = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(customerProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CustomerProfile', () => {
      const customerProfile = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(customerProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CustomerProfile', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CustomerProfile', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CustomerProfile', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addCustomerProfileToCollectionIfMissing', () => {
      it('should add a CustomerProfile to an empty array', () => {
        const customerProfile: ICustomerProfile = sampleWithRequiredData;
        expectedResult = service.addCustomerProfileToCollectionIfMissing([], customerProfile);
        expect(expectedResult).toEqual([customerProfile]);
      });

      it('should not add a CustomerProfile to an array that contains it', () => {
        const customerProfile: ICustomerProfile = sampleWithRequiredData;
        const customerProfileCollection: ICustomerProfile[] = [
          {
            ...customerProfile,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, customerProfile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CustomerProfile to an array that doesn't contain it", () => {
        const customerProfile: ICustomerProfile = sampleWithRequiredData;
        const customerProfileCollection: ICustomerProfile[] = [sampleWithPartialData];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, customerProfile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerProfile);
      });

      it('should add only unique CustomerProfile to an array', () => {
        const customerProfileArray: ICustomerProfile[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const customerProfileCollection: ICustomerProfile[] = [sampleWithRequiredData];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, ...customerProfileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const customerProfile: ICustomerProfile = sampleWithRequiredData;
        const customerProfile2: ICustomerProfile = sampleWithPartialData;
        expectedResult = service.addCustomerProfileToCollectionIfMissing([], customerProfile, customerProfile2);
        expect(expectedResult).toEqual([customerProfile, customerProfile2]);
      });

      it('should accept null and undefined values', () => {
        const customerProfile: ICustomerProfile = sampleWithRequiredData;
        expectedResult = service.addCustomerProfileToCollectionIfMissing([], null, customerProfile, undefined);
        expect(expectedResult).toEqual([customerProfile]);
      });

      it('should return initial array if no CustomerProfile is added', () => {
        const customerProfileCollection: ICustomerProfile[] = [sampleWithRequiredData];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, undefined, null);
        expect(expectedResult).toEqual(customerProfileCollection);
      });
    });

    describe('compareCustomerProfile', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCustomerProfile(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31508 };
        const entity2 = null;

        const compareResult1 = service.compareCustomerProfile(entity1, entity2);
        const compareResult2 = service.compareCustomerProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31508 };
        const entity2 = { id: 23448 };

        const compareResult1 = service.compareCustomerProfile(entity1, entity2);
        const compareResult2 = service.compareCustomerProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31508 };
        const entity2 = { id: 31508 };

        const compareResult1 = service.compareCustomerProfile(entity1, entity2);
        const compareResult2 = service.compareCustomerProfile(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
