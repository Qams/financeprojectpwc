import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionStatsComponent } from './transaction-stats.component';

describe('TransactionStatsComponent', () => {
  let component: TransactionStatsComponent;
  let fixture: ComponentFixture<TransactionStatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TransactionStatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
