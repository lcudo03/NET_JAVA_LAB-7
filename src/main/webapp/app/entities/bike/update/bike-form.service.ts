import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IBike, NewBike } from '../bike.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBike for edit and NewBikeFormGroupInput for create.
 */
type BikeFormGroupInput = IBike | PartialWithRequiredKeyOf<NewBike>;

type BikeFormDefaults = Pick<NewBike, 'id' | 'available' | 'categorieses'>;

type BikeFormGroupContent = {
  id: FormControl<IBike['id'] | NewBike['id']>;
  name: FormControl<IBike['name']>;
  serialNumber: FormControl<IBike['serialNumber']>;
  bikeType: FormControl<IBike['bikeType']>;
  pricePerHour: FormControl<IBike['pricePerHour']>;
  available: FormControl<IBike['available']>;
  productionDate: FormControl<IBike['productionDate']>;
  categorieses: FormControl<IBike['categorieses']>;
};

export type BikeFormGroup = FormGroup<BikeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BikeFormService {
  createBikeFormGroup(bike?: BikeFormGroupInput): BikeFormGroup {
    const bikeRawValue = {
      ...this.getFormDefaults(),
      ...(bike ?? { id: null }),
    };
    return new FormGroup<BikeFormGroupContent>({
      id: new FormControl(
        { value: bikeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(bikeRawValue.name, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      serialNumber: new FormControl(bikeRawValue.serialNumber, {
        validators: [Validators.required],
      }),
      bikeType: new FormControl(bikeRawValue.bikeType, {
        validators: [Validators.required],
      }),
      pricePerHour: new FormControl(bikeRawValue.pricePerHour, {
        validators: [Validators.required, Validators.min(0)],
      }),
      available: new FormControl(bikeRawValue.available, {
        validators: [Validators.required],
      }),
      productionDate: new FormControl(bikeRawValue.productionDate),
      categorieses: new FormControl(bikeRawValue.categorieses ?? []),
    });
  }

  getBike(form: BikeFormGroup): IBike | NewBike {
    return form.getRawValue();
  }

  resetForm(form: BikeFormGroup, bike: BikeFormGroupInput): void {
    const bikeRawValue = { ...this.getFormDefaults(), ...bike };
    form.reset({
      ...bikeRawValue,
      id: { value: bikeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): BikeFormDefaults {
    return {
      id: null,
      available: false,
      categorieses: [],
    };
  }
}
