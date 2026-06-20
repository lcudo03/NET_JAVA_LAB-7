import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../bike.test-samples';

import { BikeFormService } from './bike-form.service';

describe('Bike Form Service', () => {
  let service: BikeFormService;

  beforeEach(() => {
    service = TestBed.inject(BikeFormService);
  });

  describe('Service methods', () => {
    describe('createBikeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBikeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            serialNumber: expect.any(Object),
            bikeType: expect.any(Object),
            pricePerHour: expect.any(Object),
            available: expect.any(Object),
            productionDate: expect.any(Object),
            categorieses: expect.any(Object),
          }),
        );
      });

      it('passing IBike should create a new form with FormGroup', () => {
        const formGroup = service.createBikeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            serialNumber: expect.any(Object),
            bikeType: expect.any(Object),
            pricePerHour: expect.any(Object),
            available: expect.any(Object),
            productionDate: expect.any(Object),
            categorieses: expect.any(Object),
          }),
        );
      });
    });

    describe('getBike', () => {
      it('should return NewBike for default Bike initial value', () => {
        const formGroup = service.createBikeFormGroup(sampleWithNewData);

        const bike = service.getBike(formGroup);

        expect(bike).toMatchObject(sampleWithNewData);
      });

      it('should return NewBike for empty Bike initial value', () => {
        const formGroup = service.createBikeFormGroup();

        const bike = service.getBike(formGroup);

        expect(bike).toMatchObject({});
      });

      it('should return IBike', () => {
        const formGroup = service.createBikeFormGroup(sampleWithRequiredData);

        const bike = service.getBike(formGroup);

        expect(bike).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBike should not enable id FormControl', () => {
        const formGroup = service.createBikeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBike should disable id FormControl', () => {
        const formGroup = service.createBikeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
