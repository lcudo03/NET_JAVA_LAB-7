import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { BikeType } from 'app/entities/enumerations/bike-type.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IBike } from '../bike.model';
import { BikeService } from '../service/bike.service';

import { BikeFormGroup, BikeFormService } from './bike-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-bike-update',
  templateUrl: './bike-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class BikeUpdate implements OnInit {
  readonly isSaving = signal(false);
  bike: IBike | null = null;
  bikeTypeValues = Object.keys(BikeType);

  categoriesSharedCollection = signal<ICategory[]>([]);

  protected bikeService = inject(BikeService);
  protected bikeFormService = inject(BikeFormService);
  protected categoryService = inject(CategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BikeFormGroup = this.bikeFormService.createBikeFormGroup();

  compareCategory = (o1: ICategory | null, o2: ICategory | null): boolean => this.categoryService.compareCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bike }) => {
      this.bike = bike;
      if (bike) {
        this.updateForm(bike);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const bike = this.bikeFormService.getBike(this.editForm);
    if (bike.id === null) {
      this.subscribeToSaveResponse(this.bikeService.create(bike));
    } else {
      this.subscribeToSaveResponse(this.bikeService.update(bike));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IBike | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(bike: IBike): void {
    this.bike = bike;
    this.bikeFormService.resetForm(this.editForm, bike);

    this.categoriesSharedCollection.update(categories =>
      this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, ...(bike.categorieses ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing<ICategory>(categories, ...(this.bike?.categorieses ?? [])),
        ),
      )
      .subscribe((categories: ICategory[]) => this.categoriesSharedCollection.set(categories));
  }
}
