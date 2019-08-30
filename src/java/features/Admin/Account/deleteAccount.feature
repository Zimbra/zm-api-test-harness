@deleteAccount
Feature: Delete account mutation

  Scenario Outline: Log in with admin user
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Generate admin authToken for user '<username>' and password '<password>'
    Then set adminAuthToken in the cookie

    Examples: 
      | server       | path             | headers                                               | username | password | status | valid | authToken       |
      | zimbraServer | gqlAdminEndpoint | Accept:application/json,Content-type:application/json | admin    | test123  |    200 | valid | zimbraAuthToken |

  Scenario Outline: Delete account
    Given adminAuthToken is present in the cookies
    When Create and Delete user account with name '<name>' password '<password>'
    Then Validate account is deleted

    Examples: 
      | name          | password |
      | account1@host | test123  |
