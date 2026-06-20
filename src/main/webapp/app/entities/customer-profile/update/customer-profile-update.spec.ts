import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { ICustomerProfile } from '../customer-profile.model';
import { CustomerProfileService } from '../service/customer-profile.service';

import { CustomerProfileFormService } from './customer-profile-form.service';
import { CustomerProfileUpdate } from './customer-profile-update';

describe('CustomerProfile Management Update Component', () => {
  let comp: CustomerProfileUpdate;
  let fixture: ComponentFixture<CustomerProfileUpdate>;
  let activatedRoute: ActivatedRoute;
  let customerProfileFormService: CustomerProfileFormService;
  let customerProfileService: CustomerProfileService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(CustomerProfileUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customerProfileFormService = TestBed.inject(CustomerProfileFormService);
    customerProfileService = TestBed.inject(CustomerProfileService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call customer query and add missing value', () => {
      const customerProfile: ICustomerProfile = { id: 23448 };
      const customer: ICustomer = { id: 26915 };
      customerProfile.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      vitest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const expectedCollection: ICustomer[] = [customer, ...customerCollection];
      vitest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(customerCollection, customer);
      expect(comp.customersCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const customerProfile: ICustomerProfile = { id: 23448 };
      const customer: ICustomer = { id: 26915 };
      customerProfile.customer = customer;

      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      expect(comp.customersCollection()).toContainEqual(customer);
      expect(comp.customerProfile).toEqual(customerProfile);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICustomerProfile>();
      const customerProfile = { id: 31508 };
      vitest.spyOn(customerProfileFormService, 'getCustomerProfile').mockReturnValue(customerProfile);
      vitest.spyOn(customerProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(customerProfile);
      saveSubject.complete();

      // THEN
      expect(customerProfileFormService.getCustomerProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(customerProfileService.update).toHaveBeenCalledWith(expect.objectContaining(customerProfile));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICustomerProfile>();
      const customerProfile = { id: 31508 };
      vitest.spyOn(customerProfileFormService, 'getCustomerProfile').mockReturnValue({ id: null });
      vitest.spyOn(customerProfileService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(customerProfile);
      saveSubject.complete();

      // THEN
      expect(customerProfileFormService.getCustomerProfile).toHaveBeenCalled();
      expect(customerProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICustomerProfile>();
      const customerProfile = { id: 31508 };
      vitest.spyOn(customerProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customerProfileService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCustomer', () => {
      it('should forward to customerService', () => {
        const entity = { id: 26915 };
        const entity2 = { id: 21032 };
        vitest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
