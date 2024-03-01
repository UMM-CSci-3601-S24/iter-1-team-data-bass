// import { UserRole } from 'src/app/users/hunt';

export class HuntListPage {
  private readonly baseUrl = '/hunts';
  private readonly pageTitle = '.hunt-list-title';
  private readonly huntCardSelector = '.hunt-cards-container app-hunt-card';
  private readonly huntListItemsSelector = '.hunt-nav-list .hunt-list-item';
  private readonly profileButtonSelector = '[data-test=viewProfileButton]';
  private readonly radioButtonSelector = `[data-test=viewTypeRadio] mat-radio-button`;
  // private readonly userRoleDropdownSelector = '[data-test=userRoleSelect]';
  private readonly dropdownOptionSelector = `mat-option`;
  private readonly addHuntButtonSelector = '[data-test=addHuntButton]';

  navigateTo() {
    return cy.visit(this.baseUrl);
  }

  /**
   * Gets the title of the app when visiting the `/users` page.
   *
   * @returns the value of the element with the ID `.hunt-list-title`
   */
  getHuntTitle() {
    return cy.get(this.pageTitle);
  }

  /**
   * Get all the `app-hunt-card` DOM elements. This will be
   * empty if we're using the list view of the users.
   *
   * @returns an iterable (`Cypress.Chainable`) containing all
   *   the `app-hunt-card` DOM elements.
   */
   getHuntCards() {
    return cy.get(this.huntCardSelector);
  }

  /**
   * Get all the `.hunt-list-item` DOM elements. This will
   * be empty if we're using the card view of the users.
   *
   * @returns an iterable (`Cypress.Chainable`) containing all
   *   the `.hunt-list-item` DOM elements.
   */
  getHuntListItems() {
    return cy.get(this.huntListItemsSelector);
  }

  /**
   * Clicks the "view profile" button for the given hunt card.
   * Requires being in the "card" view.
   *
   * @param card The hunt card
   */
  clickViewProfile(card: Cypress.Chainable<JQuery<HTMLElement>>) {
    return card.find<HTMLButtonElement>(this.profileButtonSelector).click();
  }

  // /**
  //  * Change the view of users.
  //  *
  //  * @param viewType Which view type to change to: "card" or "list".
  //  */
  // changeView(viewType: 'card' | 'list') {
  //   return cy.get(`${this.radioButtonSelector}[value="${viewType}"]`).click();
  // }

  // /**
  //  * Selects a role to filter in the "Role" selector.
  //  *
  //  * @param value The role *value* to select, this is what's found in the mat-option "value" attribute.
  //  */
  // selectRole(value: UserRole) {
  //   // Find and click the drop down
  //   cy.get(this.userRoleDropdownSelector).click();
  //   // Select and click the desired value from the resulting menu
  //   return cy.get(`${this.dropdownOptionSelector}[value="${value}"]`).click();
  // }

  addHuntButton() {
    return cy.get(this.addHuntButtonSelector);
  }
}
