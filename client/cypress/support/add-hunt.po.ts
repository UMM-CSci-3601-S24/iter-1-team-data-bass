import { Hunt } from 'src/app/hunts/hunt';

export class AddHuntPage {

  private readonly url = '/hunts/new';
  private readonly title = '.add-hunt-title';
  private readonly button = '[data-test=confirmAddHuntButton]';
  private readonly snackBar = '.mat-mdc-simple-snack-bar';
  private readonly titleFieldName = 'title';
  private readonly hostidFieldName = 'hostid';
  private readonly taskFieldName = 'task';
  private readonly descriptionFieldName = 'description';
  private readonly formFieldSelector = `mat-form-field`;
  private readonly dropDownSelector = `mat-option`;

  navigateTo() {
    return cy.visit(this.url);
  }

  getTitle() {
    return cy.get(this.title);
  }

  addHuntButton() {
    return cy.get(this.button);
  }

  selectMatSelectValue(select: Cypress.Chainable, value: string) {
    // Find and click the drop down
    return select.click()
      // Select and click the desired value from the resulting menu
      .get(`${this.dropDownSelector}[value="${value}"]`).click();
  }

  getFormField(fieldName: string) {
    return cy.get(`${this.formFieldSelector} [formcontrolname=${fieldName}]`);
  }

  getSnackBar() {
    return cy.get(this.snackBar);
  }

  addHunt(newHunt: Hunt) {
    this.getFormField(this.titleFieldName).type(newHunt.title);
    this.getFormField(this.hostidFieldName).type(newHunt.hostid.toString());
    if (newHunt.task) {
      this.getFormField(this.taskFieldName).type(newHunt.task);
    }
    if (newHunt.description) {
      this.getFormField(this.descriptionFieldName).type(newHunt.description);
    }
    // this.selectMatSelectValue(this.getFormField('role'), newHunt.role);
    return this.addHuntButton().click();
  }
}
