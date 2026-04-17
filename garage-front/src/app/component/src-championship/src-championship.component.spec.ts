import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SrcChampionshipComponent } from './src-championship.component';

describe('SrcChampionshipComponent', () => {
  let component: SrcChampionshipComponent;
  let fixture: ComponentFixture<SrcChampionshipComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SrcChampionshipComponent]
    });
    fixture = TestBed.createComponent(SrcChampionshipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
