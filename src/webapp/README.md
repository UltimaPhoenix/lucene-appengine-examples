# Pre-Setup:
* `sudo npm install -g @angular/cli`

## Creation:
* `ng new index-manager`
  * Routing: `Y`
  * Stylesheet: `Sass`

## Additional Lib:
* Boostrap added to index via cdn [guide](https://getbootstrap.com/docs/5.2/getting-started/download/#cdn-via-jsdelivr)
* not added angular bootstrap bad compatibility manage for `popper` etc...

## Setup Environment:
* go in `src/environments` and add for both the api url for prod/local
  * `environment.prod.ts` &rarr;  under environment `apiBaseUrl: "https://bigtable-lucene.appspot.com"`
  * `environment.ts` &rarr;  under environment  `apiBaseUrl: "https://bigtable-lucene.appspot.com"`
  * Generate your swagger api for `angular-typescript` and extract the zip rename to your preference under `src/libs` name and setup the file `app.module.ts`  
    ```typescript
    import {ApiModule, Configuration, ConfigurationParameters} from '../libs/index-manager-api-angular-client';
    import {environment} from "../environments/environment";
    //...
    const configurationFactory = () => {
      const configParams: ConfigurationParameters = {
          basePath: environment.apiBaseUrl
      };
      return new Configuration(configParams);
    }
    //
    @NgModule({
    declarations: [
      //...
    ],
    imports: [
      //...
      HttpClientModule,
      ApiModule.forRoot(configurationFactory),
      //...
    ],
    //...
    })
    export class AppModule { }
    //...
  ```

# Run:
* `ng serve`
* Open http://localhost:4200/