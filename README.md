#Oidc and Jwt application

## About

This is an Okta example.   The main app uses Oidc for login.   It can optionally contact a resource app that uses a jwt token.  Both have integration tests.

## To set it up for yourself... 

1. Create an okta developer account
1. Name it
1. Select OIDC -- Open Connect
1. Select Web Application
1. Select Next
1. Add the info to the application.yml file
1. Add http://localhost:8080/login/oauth2/code/okta to the sign in redirect urls
1. Go to Directory &gt; Users
1. Add a user
1. Go to Security  &gt; API
1. Add authorization and connect it with your authentication instance
1. Goto http://localhost:8080 and try it out



## a note on testing

Unlike mvc security @WithMockUser* is not available with either the an oidc login app or a oauth resource app.  This probably has something to do with 
oauth integrating into spring security.  To make it work we had to throw a couple of mock beans in, override the webclient instantiator 
then use WebTestClient's mutatewith to put in what we needed.