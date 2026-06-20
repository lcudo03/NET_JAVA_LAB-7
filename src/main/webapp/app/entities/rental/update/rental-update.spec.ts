import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBike } from 'app/entities/bike/bike.model';
import { BikeService } from 'app/entities/bike/service/bike.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IRental } from '../rental.model';
import { RentalService } from '../service/rental.service';

import { RentalFormService } from './rental-form.service';
import { RentalUpdate } from './rental-update';

describe('Rental Management Update Component', () => {
  let comp: RentalUpdate;
  let fixture: ComponentFixture<RentalUpdate>;
  let activatedRoute: ActivatedRoute;
  let rentalFormService: RentalFormService;
  let rentalService: RentalService;
  let customerService: CustomerService;
  let bikeService: BikeService;

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

    fixture = TestBed.createComponent(RentalUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    rentalFormService = TestBed.inject(RentalFormService);
    rentalService = TestBed.inject(RentalService);
    customerService = TestBed.inject(CustomerService);
    bikeService = TestBed.inject(BikeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Customer query and add missing value', () => {
      const rental: IRental = { id: 17269 };
      const customer: ICustomer = { id: 26915 };
      rental.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 26915 }];
      vitest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      vitest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.customersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Bike query and add missing value', () => {
      const rental: IRental = { id: 17269 };
      const bike: IBike = { id: 9820 };
      rental.bike = bike;

      const bikeCollection: IBike[] = [{ id: 9820 }];
      vitest.spyOn(bikeService, 'query').mockReturnValue(of(new HttpResponse({ body: bikeCollection })));
      const additionalBikes = [bike];
      const expectedCollection: IBike[] = [...additionalBikes, ...bikeCollection];
      vitest.spyOn(bikeService, 'addBikeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      expect(bikeService.query).toHaveBeenCalled();
      expect(bikeService.addBikeToCollectionIfMissing).toHaveBeenCalledWith(
        bikeCollection,
        ...additionalBikes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.bikesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const rental: IRental = { id: 17269 };
      const customer: ICustomer = { id: 26915 };
      rental.customer = customer;
      const bike: IBike = { id: 9820 };
      rental.bike = bike;

      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      expect(comp.customersSharedCollection()).toContainEqual(customer);
      expect(comp.bikesSharedCollection()).toContainEqual(bike);
      expect(comp.rental).toEqual(rental);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRental>();
      const rental = { id: 12599 };
      vitest.spyOn(rentalFormService, 'getRental').mockReturnValue(rental);
      vitest.spyOn(rentalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rental);
      saveSubject.complete();

      // THEN
      expect(rentalFormService.getRental).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(rentalService.update).toHaveBeenCalledWith(expect.objectContaining(rental));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IRental>();
      const rental = { id: 12599 };
      vitest.spyOn(rentalFormService, 'getRental').mockReturnValue({ id: null });
      vitest.spyOn(rentalService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rental: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(rental);
      saveSubject.complete();

      // THEN
      expect(rentalFormService.getRental).toHaveBeenCalled();
      expect(rentalService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IRental>();
      const rental = { id: 12599 };
      vitest.spyOn(rentalService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ rental });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(rentalService.update).toHaveBeenCalled();
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

    describe('compareBike', () => {
      it('should forward to bikeService', () => {
        const entity = { id: 9820 };
        const entity2 = { id: 12903 };
        vitest.spyOn(bikeService, 'compareBike');
        comp.compareBike(entity, entity2);
        expect(bikeService.compareBike).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
