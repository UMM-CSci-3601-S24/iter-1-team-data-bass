import { Hunt } from 'src/app/hunts/hunt';
import { AddHuntPage } from '../support/add-hunt.po';

describe('Add hunt', () => {
  const page = new AddHuntPage();

  beforeEach(() => {
    page.navigateTo();
  });

  it('Should have the correct title', () => {
    page.getTitle().should('have.text', 'New Hunt');
  });

  // it('Should enable and disable the add hunt button', () => {
  //   // ADD hunt button should be disabled until all the necessary fields
  //   // are filled. Once the last (`#emailField`) is filled, then the button should
  //   // become enabled.
  //   page.addHuntButton().should('be.disabled');
  //   page.getFormField('title').type('test');
  //   page.addHuntButton().should('be.disabled');
  //   page.getFormField('hostid').type('20');
  //   page.addHuntButton().should('be.disabled');
  //   page.getFormField('description').type('invalid');
  //   page.addHuntButton().should('be.disabled');
  //   // page.getFormField('email').clear().type('hunt@example.com');
  //   // all the required fields have valid input, then it should be enabled
  //   page.addHuntButton().should('be.enabled');
  // });

  // it('Should show error messages for invalid inputs', () => {
  //   // Before doing anything there shouldn't be an error
  //   cy.get('[data-test=nameError]').should('not.exist');
  //   // Just clicking the name field without entering anything should cause an error message
  //   page.getFormField('name').click().blur();
  //   cy.get('[data-test=nameError]').should('exist').and('be.visible');
  //   // Some more tests for various invalid name inputs
  //   page.getFormField('name').type('J').blur();
  //   cy.get('[data-test=nameError]').should('exist').and('be.visible');
  //   page.getFormField('name').clear().type('This is a very long name that goes beyond the 50 character limit').blur();
  //   cy.get('[data-test=nameError]').should('exist').and('be.visible');
  //   // Entering a valid name should remove the error.
  //   page.getFormField('name').clear().type('John Smith').blur();
  //   cy.get('[data-test=nameError]').should('not.exist');

  //   // Before doing anything there shouldn't be an error
  //   cy.get('[data-test=ageError]').should('not.exist');
  //   // Just clicking the age field without entering anything should cause an error message
  //   page.getFormField('age').click().blur();
  //   // Some more tests for various invalid age inputs
  //   cy.get('[data-test=ageError]').should('exist').and('be.visible');
  //   page.getFormField('age').type('5').blur();
  //   cy.get('[data-test=ageError]').should('exist').and('be.visible');
  //   page.getFormField('age').clear().type('500').blur();
  //   cy.get('[data-test=ageError]').should('exist').and('be.visible');
  //   page.getFormField('age').clear().type('asd').blur();
  //   cy.get('[data-test=ageError]').should('exist').and('be.visible');
  //   // Entering a valid age should remove the error.
  //   page.getFormField('age').clear().type('25').blur();
  //   cy.get('[data-test=ageError]').should('not.exist');

  //   // Before doing anything there shouldn't be an error
  //   cy.get('[data-test=emailError]').should('not.exist');
  //   // Just clicking the email field without entering anything should cause an error message
  //   page.getFormField('email').click().blur();
  //   // Some more tests for various invalid email inputs
  //   cy.get('[data-test=emailError]').should('exist').and('be.visible');
  //   page.getFormField('email').type('asd').blur();
  //   cy.get('[data-test=emailError]').should('exist').and('be.visible');
  //   page.getFormField('email').clear().type('@example.com').blur();
  //   cy.get('[data-test=emailError]').should('exist').and('be.visible');
  //   // Entering a valid email should remove the error.
  //   page.getFormField('email').clear().type('hunt@example.com').blur();
  //   cy.get('[data-test=emailError]').should('not.exist');
  // });

  describe('Adding a new hunt', () => {

    beforeEach(() => {
      cy.task('seed:database');
    });

    // it('Should go to the right page, and have the right info', () => {
    //   const hunt: Hunt = {
    //     _id: null,
    //     hostid: 'Joe',
    //     title: 'new hunt',
    //     description: 'cool hunt',
    //     task: 'go'

    //   };

    //   page.addHunt(hunt);

    //   // New URL should end in the 24 hex character Mongo ID of the newly added hunt
    //   cy.url()
    //     .should('match', /\/hunts\/[0-9a-fA-F]{24}$/)
    //     .should('not.match', /\/hunts\/new$/);

    //   // The new hunt should have all the same attributes as we entered
    //   cy.get('.hunt-card-title').should('have.text', hunt.title);
    //   cy.get('.hunt-card-hostid').should('have.text', hunt.hostid);
    //   cy.get('.hunt-card-description').should('have.text', hunt.description);
    //   cy.get('.hunt-card-task').should('have.text', hunt.task);
    //   // cy.get('.hunt-card-email').should('have.text', hunt.email);

    //   // We should see the confirmation message at the bottom of the screen
    //   page.getSnackBar().should('contain', `Added hunt ${hunt.title}`);
    // });

    // it('Should fail with no description', () => {
    //   const hunt: Hunt = {
    //     _id: null,
    //     title: 'Test hunt',
    //     hostid: 'joe',
    //     description: null, // The company being set to null means nothing will be typed for it
    //     task: 'test@example.com',
    //     // role: 'editor'
    //   };

  //     page.addHunt(hunt);

  //     // We should get an error message
  //     page.getSnackBar().should('contain', `Problem contacting the server – Error Code:`);

  //     // We should have stayed on the new hunt page
  //     cy.url()
  //       .should('not.match', /\/hunts\/[0-9a-fA-F]{24}$/)
  //       .should('match', /\/hunts\/new$/);

  //     // The things we entered in the form should still be there
  //     page.getFormField('title').should('have.value', hunt.title);
  //     page.getFormField('hostid').should('have.value', hunt.hostid);
  //     page.getFormField('task').should('have.value', hunt.task);
  //     page.getFormField('role').should('contain', 'Editor');
  //   });
  // });

});
});
