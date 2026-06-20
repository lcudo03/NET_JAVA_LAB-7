import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICustomerProfile, NewCustomerProfile } from '../customer-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomerProfile for edit and NewCustomerProfileFormGroupInput for create.
 */
type CustomerProfileFormGroupInput = ICustomerProfile | PartialWithRequiredKeyOf<NewCustomerProfile>;

type CustomerProfileFormDefaults = Pick<NewCustomerProfile, 'id' | 'verified'>;

type CustomerProfileFormGroupContent = {
  id: FormControl<ICustomerProfile['id'] | NewCustomerProfile['id']>;
  address: FormControl<ICustomerProfile['address']>;
  city: FormControl<ICustomerProfile['city']>;
  loyaltyPoints: FormControl<ICustomerProfile['loyaltyPoints']>;
  verified: FormControl<ICustomerProfile['verified']>;
  customer: FormControl<ICustomerProfile['customer']>;
};

export type CustomerProfileFormGroup = FormGroup<CustomerProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerProfileFormService {
  createCustomerProfileFormGroup(customerProfile?: CustomerProfileFormGroupInput): CustomerProfileFormGroup {
    const customerProfileRawValue = {
      ...this.getFormDefaults(),
      ...(customerProfile ?? { id: null }),
    };
    return new FormGroup<CustomerProfileFormGroupContent>({
      id: new FormControl(
        { value: customerProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      address: new FormControl(customerProfileRawValue.address, {
        validators: [Validators.required, Validators.maxLength(150)],
      }),
      city: new FormControl(customerProfileRawValue.city, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      loyaltyPoints: new FormControl(customerProfileRawValue.loyaltyPoints, {
        validators: [Validators.min(0)],
      }),
      verified: new FormControl(customerProfileRawValue.verified, {
        validators: [Validators.required],
      }),
      customer: new FormControl(customerProfileRawValue.customer, {
        validators: [Validators.required],
      }),
    });
  }

  getCustomerProfile(form: CustomerProfileFormGroup): ICustomerProfile | NewCustomerProfile {
    return form.getRawValue();
  }

  resetForm(form: CustomerProfileFormGroup, customerProfile: CustomerProfileFormGroupInput): void {
    const customerProfileRawValue = { ...this.getFormDefaults(), ...customerProfile };
    form.reset({
      ...customerProfileRawValue,
      id: { value: customerProfileRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CustomerProfileFormDefaults {
    return {
      id: null,
      verified: false,
    };
  }
}
