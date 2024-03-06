import { HuntListPage } from '../support/hunt-list.po';

const page = new HuntListPage();

describe('Hunt list', () => {

  before(() => {
    cy.task('seed:database');
  });

  beforeEach(() => {
    page.navigateTo();
  });

  it('Should have the correct title', () => {
    page.getHuntTitle().should('have.text', 'All Hunts');
  });

  // it('Should show 10 hunts in both card and list view', () => {
  //   page.getHuntCards().should('have.length', 10);
  //   // page.changeView('list');
  //   page.getHuntListItems().should('have.length', 10);
  // });

  // it('Should type something in the name filter and check that it returned correct elements', () => {
  //   // Filter for hunt 'Lynn Ferguson'
  //   cy.get('[data-test=userInput]').type('Lynn Ferguson');

  //   // All of the hunt cards should have the name we are filtering by
  //   page.getHuntCards().each(e => {
  //     cy.wrap(e).find('.hunt-card-name').should('have.text', 'Lynn Ferguson');
  //   });

  //   // (We check this two ways to show multiple ways to check this)
  //   page.getHuntCards().find('.hunt-card-name').each(el =>
  //     expect(el.text()).to.equal('Lynn Ferguson')
  //   );
  // });

  // it('Should type something in the company filter and check that it returned correct elements', () => {
  //   // Filter for company 'OHMNET'
  //   cy.get('[data-test=userCompanyInput]').type('OHMNET');

  //   page.getHuntCards().should('have.lengthOf.above', 0);

  //   // All of the hunt cards should have the company we are filtering by
  //   page.getHuntCards().find('.hunt-card-company').each(card => {
  //     cy.wrap(card).should('have.text', 'OHMNET');
  //   });
  // });

  // it('Should type something partial in the company filter and check that it returned correct elements', () => {
  //   // Filter for companies that contain 'ti'
  //   cy.get('[data-test=userCompanyInput]').type('ti');

  //   page.getHuntCards().should('have.lengthOf', 2);

  //   // Each hunt card's company name should include the text we are filtering by
  //   page.getUserCards().each(e => {
  //     cy.wrap(e).find('.hunt-card-company').should('include.text', 'TI');
  //   });
  // });

  // it('Should type something in the age filter and check that it returned correct elements', () => {
  //   // Filter for hunts of age '27'
  //   cy.get('[data-test=userAgeInput]').type('27');

  //   page.getUserCards().should('have.lengthOf', 3);

  //   // Go through each of the cards that are being shown and get the names
  //   page.getUserCards().find('.hunt-card-name')
  //     // We should see these hunts whose age is 27
  //     .should('contain.text', 'Stokes Clayton')
  //     .should('contain.text', 'Bolton Monroe')
  //     .should('contain.text', 'Merrill Parker')
  //     // We shouldn't see these hunts
  //     .should('not.contain.text', 'Connie Stewart')
  //     .should('not.contain.text', 'Lynn Ferguson');
  // });

  // it('Should change the view', () => {
  //   // Choose the view type "List"
  //   page.changeView('list');

  //   // We should not see any cards
  //   // There should be list items
  //   page.getUserCards().should('not.exist');
  //   page.getUserListItems().should('exist');

  //   // Choose the view type "Card"
  //   page.changeView('card');

  //   // There should be cards
  //   // We should not see any list items
  //   page.getUserCards().should('exist');
  //   page.getUserListItems().should('not.exist');
  // });

  // it('Should select a role, switch the view, and check that it returned correct elements', () => {
  //   // Filter for role 'viewer');
  //   page.selectRole('viewer');

  //   // Choose the view type "List"
  //   page.changeView('list');

  //   // Some of the hunts should be listed
  //   page.getUserListItems().should('have.lengthOf.above', 0);

  //   // All of the hunt list items that show should have the role we are looking for
  //   page.getUserListItems().each(el => {
  //     cy.wrap(el).find('.hunt-list-role').should('contain', 'viewer');
  //   });
  // });

  // it('Should click view profile on a hunt and go to the right URL', () => {
  //   page.getHuntCards().first().then((card) => {
  //     const firstHuntTitle = card.find('.hunt-card-title').text();
  //     // const firstUserCompany = card.find('.hunt-card-company').text();

  //     // When the view profile button on the first hunt card is clicked, the URL should have a valid mongo ID
  //     page.clickViewProfile(page.getHuntCards().first());

  //     // The URL should be '/hunts/' followed by a mongo ID
  //     cy.url().should('match', /\/hunts\/[0-9a-fA-F]{24}$/);

  //     // On this profile page we were sent to, the name and company should be correct
  //     cy.get('.hunt-card-name').first().should('have.text', firstHuntTitle);
  //     // cy.get('.hunt-card-company').first().should('have.text', firstUserCompany);
  //   });
  //  });

  it('Should click add hunt and go to the right URL', () => {
    // Click on the button for adding a new hunt
    page.addHuntButton().click();

    // The URL should end with '/hunts/new'
    cy.url().should(url => expect(url.endsWith('/hunts/new')).to.be.true);

    // On the page we were sent to, We should see the right title
    cy.get('.add-hunt-title').should('have.text', 'New Hunt');
  });

});
