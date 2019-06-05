import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaysimComponent } from './paysim.component';

describe('PaysimComponent', () => {
  let component: PaysimComponent;
  let fixture: ComponentFixture<PaysimComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PaysimComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaysimComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
