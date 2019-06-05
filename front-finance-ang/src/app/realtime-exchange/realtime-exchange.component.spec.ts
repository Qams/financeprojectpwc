import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RealtimeExchangeComponent } from './realtime-exchange.component';

describe('RealtimeExchangeComponent', () => {
  let component: RealtimeExchangeComponent;
  let fixture: ComponentFixture<RealtimeExchangeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RealtimeExchangeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RealtimeExchangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
