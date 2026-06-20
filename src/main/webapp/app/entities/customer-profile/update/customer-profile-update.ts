import { HttpResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, finalize, map } from 'rxjs';

import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICustomerProfile } from '../customer-profile.model';
import { CustomerProfileService } from '../service/customer-profile.service';

import { CustomerProfileFormGroup, CustomerProfileFormService } from './customer-profile-form.service';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'jhi-customer-profile-update',
  templateUrl: './customer-profile-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CustomerProfileUpdate implements OnInit {
  readonly isSaving = signal(false);
  customerProfile: ICustomerProfile | null = null;

  customersCollection = signal<ICustomer[]>([]);

  protected customerProfileService = inject(CustomerProfileService);
  protected customerProfileFormService = inject(CustomerProfileFormService);
  protected customerService = inject(CustomerService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CustomerProfileFormGroup = this.customerProfileFormService.createCustomerProfileFormGroup();

  compareCustomer = (o1: ICustomer | null, o2: ICustomer | null): boolean => this.customerService.compareCustomer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customerProfile }) => {
      this.customerProfile = customerProfile;
      if (customerProfile) {
        this.updateForm(customerProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const customerProfile = this.customerProfileFormService.getCustomerProfile(this.editForm);
    if (customerProfile.id === null) {
      this.subscribeToSaveResponse(this.customerProfileService.create(customerProfile));
    } else {
      this.subscribeToSaveResponse(this.customerProfileService.update(customerProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICustomerProfile | null>): void {
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

  protected updateForm(customerProfile: ICustomerProfile): void {
    this.customerProfile = customerProfile;
    this.customerProfileFormService.resetForm(this.editForm, customerProfile);

    this.customersCollection.set(
      this.customerService.addCustomerToCollectionIfMissing<ICustomer>(this.customersCollection(), customerProfile.customer),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customerService
      .query({ 'profileId.specified': 'false' })
      .pipe(map((res: HttpResponse<ICustomer[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomer[]) =>
          this.customerService.addCustomerToCollectionIfMissing<ICustomer>(customers, this.customerProfile?.customer),
        ),
      )
      .subscribe((customers: ICustomer[]) => this.customersCollection.set(customers));
  }
}
