import {Component, OnInit} from '@angular/core';
import {DefaultService as IndexingService, IndexSearchResult} from "../libs/index-manager-api-angular-client";
import {firstValueFrom, single} from "rxjs";

type ActionResult = "textIndexed" | "textDeindexed" | "indexCreated" | "indexDeleted" | "successfulSearch" | "error";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent implements OnInit {
  title = 'index-manager';
  //state
  availableIndexes:string[] | undefined;
  indexSearchResult:IndexSearchResult | undefined;
  indexIsEmpty = false;
  highlightIndexButton = false;
  errorMessage = "";

  //UI
  selectedIndex  = "defaultIndex";
  searchQuery = "";
  textToIndex = "";
  indexToCreate = "";
  actionResult:ActionResult | undefined;

  constructor(
    private indexingService: IndexingService
  ) { }

  range(start: number, end: number) {
    return Array.from({ length: (end - start) }, (v, k) => k + start);
  }


  ngOnInit() {
    this.loadAvailableIndexes();
  }

  private loadAvailableIndexes() {
    this.indexingService.listIndexes().pipe(single()).subscribe(response =>
      this.availableIndexes = response
    );
  }


  async searchText(index: string, query: string, page = 0, size = 10) {
    this.clearAllFields();
    this.indexSearchResult = await firstValueFrom(this.indexingService.search(index, query, page, size));
    this.indexIsEmpty = this.indexSearchResult.query == "*" && this.indexSearchResult.totalHits == 0;
    this.highlightIndexButton = this.indexSearchResult.totalHits == 0;
  }

  async deindexText(index: string, docId: string) {
    this.clearAllFields();
    await firstValueFrom(this.indexingService.deindexText(index, docId));
    await this.searchText(this.indexSearchResult!.index,
      this.indexSearchResult!.query,
      this.indexSearchResult?.currentPage,
      this.indexSearchResult?.pageSize);
  }

  async indexText(index: string, text: string) {
    this.clearAllFields();
    this.indexIsEmpty = false;
    this.highlightIndexButton = false;
    await firstValueFrom(this.indexingService.indexText(text, index));
    this.textToIndex = "";
  }

  createIndex(indexName: string) {
    this.clearAllFields();
    this.indexingService.createIndex(indexName).pipe(single()).subscribe(response => {
      this.actionResult = "indexCreated";
    });
    this.loadAvailableIndexes();
    this.selectedIndex = indexName;
  }

  deleteIndex(indexName: string) {
    this.clearAllFields();
    this.indexingService.deleteIndex(indexName).pipe(single()).subscribe(response => {
      this.actionResult = "indexCreated";
    });
  }

  purgeIndex(indexName: string) {
    this.clearAllFields();
    this.indexingService.purgeIndex(indexName).pipe(single()).subscribe(response => {
      this.actionResult = "indexCreated";
    });
  }

  private clearAllFields() {
    this.textToIndex = "";
    this.errorMessage = "";
  }

}
