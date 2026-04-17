import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { UserFormComponent }          from './component/user-form/user-form.component';
import { UserListComponent }          from './component/user-list/user-list.component';
import { LoginComponent }             from './component/login/login.component';
import { DashboardComponent }         from './component/dashboard/dashboard.component';
import { DealershipComponent }        from './component/dealership/dealership.component';
import { DealershipDetailComponent }  from './component/dealership-detail/dealership-detail.component';
import { ParkingComponent }           from './component/parking/parking.component';
import { ParkingDetailComponent }     from './component/parking-detail/parking-detail.component';
import { GarageUpgradeComponent }     from './component/garage-upgrade/garage-upgrade.component';
import { AutoShopComponent }          from './component/auto-shop/auto-shop.component';
import { SrcChampionshipComponent }    from './component/src-championship/src-championship.component';

@NgModule({
  declarations: [
    AppComponent, UserFormComponent, UserListComponent, LoginComponent,
    DashboardComponent, DealershipComponent, DealershipDetailComponent,
    ParkingComponent, ParkingDetailComponent,
    GarageUpgradeComponent, AutoShopComponent, SrcChampionshipComponent,
  ],
  imports: [BrowserModule, AppRoutingModule, HttpClientModule, FormsModule],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {}
