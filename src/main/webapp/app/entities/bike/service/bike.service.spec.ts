import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IBike } from '../bike.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../bike.test-samples';

import { BikeService, RestBike } from './bike.service';

const requireRestSample: RestBike = {
  ...sampleWithRequiredData,
  productionDate: sampleWithRequiredData.productionDate?.format(DATE_FORMAT),
};

describe('Bike Service', () => {
  let service: BikeService;
  let httpMock: HttpTestingController;
  let expectedResult: IBike | IBike[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BikeService);
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

    it('should create a Bike', () => {
      const bike = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bike).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Bike', () => {
      const bike = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bike).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Bike', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Bike', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Bike', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addBikeToCollectionIfMissing', () => {
      it('should add a Bike to an empty array', () => {
        const bike: IBike = sampleWithRequiredData;
        expectedResult = service.addBikeToCollectionIfMissing([], bike);
        expect(expectedResult).toEqual([bike]);
      });

      it('should not add a Bike to an array that contains it', () => {
        const bike: IBike = sampleWithRequiredData;
        const bikeCollection: IBike[] = [
          {
            ...bike,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBikeToCollectionIfMissing(bikeCollection, bike);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Bike to an array that doesn't contain it", () => {
        const bike: IBike = sampleWithRequiredData;
        const bikeCollection: IBike[] = [sampleWithPartialData];
        expectedResult = service.addBikeToCollectionIfMissing(bikeCollection, bike);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bike);
      });

      it('should add only unique Bike to an array', () => {
        const bikeArray: IBike[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bikeCollection: IBike[] = [sampleWithRequiredData];
        expectedResult = service.addBikeToCollectionIfMissing(bikeCollection, ...bikeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bike: IBike = sampleWithRequiredData;
        const bike2: IBike = sampleWithPartialData;
        expectedResult = service.addBikeToCollectionIfMissing([], bike, bike2);
        expect(expectedResult).toEqual([bike, bike2]);
      });

      it('should accept null and undefined values', () => {
        const bike: IBike = sampleWithRequiredData;
        expectedResult = service.addBikeToCollectionIfMissing([], null, bike, undefined);
        expect(expectedResult).toEqual([bike]);
      });

      it('should return initial array if no Bike is added', () => {
        const bikeCollection: IBike[] = [sampleWithRequiredData];
        expectedResult = service.addBikeToCollectionIfMissing(bikeCollection, undefined, null);
        expect(expectedResult).toEqual(bikeCollection);
      });
    });

    describe('compareBike', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBike(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9820 };
        const entity2 = null;

        const compareResult1 = service.compareBike(entity1, entity2);
        const compareResult2 = service.compareBike(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9820 };
        const entity2 = { id: 12903 };

        const compareResult1 = service.compareBike(entity1, entity2);
        const compareResult2 = service.compareBike(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 9820 };
        const entity2 = { id: 9820 };

        const compareResult1 = service.compareBike(entity1, entity2);
        const compareResult2 = service.compareBike(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
