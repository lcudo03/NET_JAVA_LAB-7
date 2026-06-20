import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../customer-profile.test-samples';

import { CustomerProfileFormService } from './customer-profile-form.service';

describe('CustomerProfile Form Service', () => {
  let service: CustomerProfileFormService;

  beforeEach(() => {
    service = TestBed.inject(CustomerProfileFormService);
  });

  describe('Service methods', () => {
    describe('createCustomerProfileFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCustomerProfileFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            address: expect.any(Object),
            city: expect.any(Object),
            loyaltyPoints: expect.any(Object),
            verified: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });

      it('passing ICustomerProfile should create a new form with FormGroup', () => {
        const formGroup = service.createCustomerProfileFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            address: expect.any(Object),
            city: expect.any(Object),
            loyaltyPoints: expect.any(Object),
            verified: expect.any(Object),
            customer: expect.any(Object),
          }),
        );
      });
    });

    describe('getCustomerProfile', () => {
      it('should return NewCustomerProfile for default CustomerProfile initial value', () => {
        const formGroup = service.createCustomerProfileFormGroup(sampleWithNewData);

        const customerProfile = service.getCustomerProfile(formGroup);

        expect(customerProfile).toMatchObject(sampleWithNewData);
      });

      it('should return NewCustomerProfile for empty CustomerProfile initial value', () => {
        const formGroup = service.createCustomerProfileFormGroup();

        const customerProfile = service.getCustomerProfile(formGroup);

        expect(customerProfile).toMatchObject({});
      });

      it('should return ICustomerProfile', () => {
        const formGroup = service.createCustomerProfileFormGroup(sampleWithRequiredData);

        const customerProfile = service.getCustomerProfile(formGroup);

        expect(customerProfile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICustomerProfile should not enable id FormControl', () => {
        const formGroup = service.createCustomerProfileFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCustomerProfile should disable id FormControl', () => {
        const formGroup = service.createCustomerProfileFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
