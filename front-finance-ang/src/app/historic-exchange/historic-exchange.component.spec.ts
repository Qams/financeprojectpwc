import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricExchangeComponent } from './historic-exchange.component';

describe('HistoricExchangeComponent', () => {
  let component: HistoricExchangeComponent;
  let fixture: ComponentFixture<HistoricExchangeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HistoricExchangeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoricExchangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
