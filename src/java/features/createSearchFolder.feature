@searchFolderCreate
Feature: SearchFolder Mutation, Query

  Scenario Outline: Log in with user account
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Get '<account>' '<authToken>' authToken for username '<username>' and password '<password>' using '<method>' method
    Then set AuthToken in the cookie

    Examples: 
      | server       | path        | headers                                               | account | username    | password | method | status | valid | authToken       |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | user_2@host | test123  | post   |    200 | valid | zimbraAuthToken |

  Scenario Outline: CreateSearchFolder Mutation
    Given authToken is present in the cookies
    When Create search folder for query '<query>', name '<name>', parentFolderId '<folderId>', searchType '<searchType>' using '<method>' method
    Then Get all search folders using '<method>' method

    Examples: 
      | query   | name       | folderId     | searchType | method |
      | in:Sent | Automation | defaultInbox | message    | post   |
