import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DealershipComponent } from './dealership.component';

describe('DealershipComponent', () => {
  let component: DealershipComponent;
  let fixture: ComponentFixture<DealershipComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DealershipComponent]
    });
    fixture = TestBed.createComponent(DealershipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
