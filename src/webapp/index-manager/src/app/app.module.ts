import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {ApiModule, Configuration, ConfigurationParameters} from "../libs/index-manager-api-angular-client";
import {environment} from "../environments/environment";
import {FormsModule} from "@angular/forms";

const configurationFactory = () => {
  const configParams: ConfigurationParameters = {
    basePath: environment.apiBaseUrl
  };
  return new Configuration(configParams);
}

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ApiModule.forRoot(configurationFactory),
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
