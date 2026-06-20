import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IBike } from 'app/entities/bike/bike.model';
import { BikeService } from 'app/entities/bike/service/bike.service';
import { ICategory } from '../category.model';
import { CategoryService } from '../service/category.service';

import { CategoryFormService } from './category-form.service';
import { CategoryUpdate } from './category-update';

describe('Category Management Update Component', () => {
  let comp: CategoryUpdate;
  let fixture: ComponentFixture<CategoryUpdate>;
  let activatedRoute: ActivatedRoute;
  let categoryFormService: CategoryFormService;
  let categoryService: CategoryService;
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

    fixture = TestBed.createComponent(CategoryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    categoryFormService = TestBed.inject(CategoryFormService);
    categoryService = TestBed.inject(CategoryService);
    bikeService = TestBed.inject(BikeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Bike query and add missing value', () => {
      const category: ICategory = { id: 4374 };
      const bikeses: IBike[] = [{ id: 9820 }];
      category.bikeses = bikeses;

      const bikeCollection: IBike[] = [{ id: 9820 }];
      vitest.spyOn(bikeService, 'query').mockReturnValue(of(new HttpResponse({ body: bikeCollection })));
      const additionalBikes = [...bikeses];
      const expectedCollection: IBike[] = [...additionalBikes, ...bikeCollection];
      vitest.spyOn(bikeService, 'addBikeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      expect(bikeService.query).toHaveBeenCalled();
      expect(bikeService.addBikeToCollectionIfMissing).toHaveBeenCalledWith(
        bikeCollection,
        ...additionalBikes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.bikesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const category: ICategory = { id: 4374 };
      const bikes: IBike = { id: 9820 };
      category.bikeses = [bikes];

      activatedRoute.data = of({ category });
      comp.ngOnInit();

      expect(comp.bikesSharedCollection()).toContainEqual(bikes);
      expect(comp.category).toEqual(category);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICategory>();
      const category = { id: 6752 };
      vitest.spyOn(categoryFormService, 'getCategory').mockReturnValue(category);
      vitest.spyOn(categoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(category);
      saveSubject.complete();

      // THEN
      expect(categoryFormService.getCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(categoryService.update).toHaveBeenCalledWith(expect.objectContaining(category));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICategory>();
      const category = { id: 6752 };
      vitest.spyOn(categoryFormService, 'getCategory').mockReturnValue({ id: null });
      vitest.spyOn(categoryService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(category);
      saveSubject.complete();

      // THEN
      expect(categoryFormService.getCategory).toHaveBeenCalled();
      expect(categoryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICategory>();
      const category = { id: 6752 };
      vitest.spyOn(categoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ category });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(categoryService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
