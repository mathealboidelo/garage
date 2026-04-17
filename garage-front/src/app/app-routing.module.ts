import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserListComponent }         from './component/user-list/user-list.component';
import { UserFormComponent }         from './component/user-form/user-form.component';
import { LoginComponent }            from './component/login/login.component';
import { DashboardComponent }        from './component/dashboard/dashboard.component';
import { DealershipComponent }       from './component/dealership/dealership.component';
import { DealershipDetailComponent } from './component/dealership-detail/dealership-detail.component';
import { ParkingComponent }          from './component/parking/parking.component';
import { ParkingDetailComponent }    from './component/parking-detail/parking-detail.component';
import { GarageUpgradeComponent }    from './component/garage-upgrade/garage-upgrade.component';
import { AutoShopComponent }         from './component/auto-shop/auto-shop.component';
import { SrcChampionshipComponent }   from './component/src-championship/src-championship.component';

const routes: Routes = [
  { path: '',               component: LoginComponent },
  { path: 'users',          component: UserListComponent },
  { path: 'adduser',        component: UserFormComponent },
  { path: 'dashboard',      component: DashboardComponent },
  { path: 'dealership',     component: DealershipComponent },
  { path: 'dealership/:id', component: DealershipDetailComponent },
  { path: 'parkings',       component: ParkingComponent },
  { path: 'parkings/:id',   component: ParkingDetailComponent },
  { path: 'upgrade',        component: GarageUpgradeComponent },
  { path: 'autoshop',       component: AutoShopComponent },
  { path: 'src',            component: SrcChampionshipComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
