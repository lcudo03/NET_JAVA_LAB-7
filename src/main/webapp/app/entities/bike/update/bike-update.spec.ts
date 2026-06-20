import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { IBike } from '../bike.model';
import { BikeService } from '../service/bike.service';

import { BikeFormService } from './bike-form.service';
import { BikeUpdate } from './bike-update';

describe('Bike Management Update Component', () => {
  let comp: BikeUpdate;
  let fixture: ComponentFixture<BikeUpdate>;
  let activatedRoute: ActivatedRoute;
  let bikeFormService: BikeFormService;
  let bikeService: BikeService;
  let categoryService: CategoryService;

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

    fixture = TestBed.createComponent(BikeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bikeFormService = TestBed.inject(BikeFormService);
    bikeService = TestBed.inject(BikeService);
    categoryService = TestBed.inject(CategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Category query and add missing value', () => {
      const bike: IBike = { id: 12903 };
      const categorieses: ICategory[] = [{ id: 6752 }];
      bike.categorieses = categorieses;

      const categoryCollection: ICategory[] = [{ id: 6752 }];
      vitest.spyOn(categoryService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const additionalCategories = [...categorieses];
      const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
      vitest.spyOn(categoryService, 'addCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ bike });
      comp.ngOnInit();

      expect(categoryService.query).toHaveBeenCalled();
      expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        categoryCollection,
        ...additionalCategories.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.categoriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const bike: IBike = { id: 12903 };
      const categories: ICategory = { id: 6752 };
      bike.categorieses = [categories];

      activatedRoute.data = of({ bike });
      comp.ngOnInit();

      expect(comp.categoriesSharedCollection()).toContainEqual(categories);
      expect(comp.bike).toEqual(bike);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBike>();
      const bike = { id: 9820 };
      vitest.spyOn(bikeFormService, 'getBike').mockReturnValue(bike);
      vitest.spyOn(bikeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bike });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(bike);
      saveSubject.complete();

      // THEN
      expect(bikeFormService.getBike).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bikeService.update).toHaveBeenCalledWith(expect.objectContaining(bike));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBike>();
      const bike = { id: 9820 };
      vitest.spyOn(bikeFormService, 'getBike').mockReturnValue({ id: null });
      vitest.spyOn(bikeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bike: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(bike);
      saveSubject.complete();

      // THEN
      expect(bikeFormService.getBike).toHaveBeenCalled();
      expect(bikeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IBike>();
      const bike = { id: 9820 };
      vitest.spyOn(bikeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bike });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bikeService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCategory', () => {
      it('should forward to categoryService', () => {
        const entity = { id: 6752 };
        const entity2 = { id: 4374 };
        vitest.spyOn(categoryService, 'compareCategory');
        comp.compareCategory(entity, entity2);
        expect(categoryService.compareCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
