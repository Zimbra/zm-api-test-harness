@deleteAccount @smx
Feature: Delete account mutation

  Scenario Outline: Log in with admin user
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Generate admin authToken for user '<username>' and password '<password>'
    Then set adminAuthToken in the cookie

    Examples: 
      | server            | path             | headers                                               | username   | password | status | valid | authToken       |
      | zimbraAdminServer | gqlAdminEndpoint | Accept:application/json,Content-type:application/json | admin@host | test123  |    200 | valid | zimbraAuthToken |

  Scenario Outline: Delete account
    Given adminAuthToken is present in the cookies
    When Create user account with name '<name>' password '<password>'
    When Delete user account with name '<name>'
    Then Validate account is deleted

    Examples: 
      | name          | password |
      | account1@host | test123  |
