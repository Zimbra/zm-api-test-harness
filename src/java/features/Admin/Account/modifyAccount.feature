@modifyAccount @smx
Feature: Modify account mutation

  Scenario Outline: Log in with admin user
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Generate admin authToken for user '<username>' and password '<password>'
    Then set adminAuthToken in the cookie

    Examples: 
      | server            | path             | headers                                               | username   | password | status | valid | authToken       |
      | zimbraAdminServer | gqlAdminEndpoint | Accept:application/json,Content-type:application/json | admin@host | test123  |    200 | valid | zimbraAuthToken |

  Scenario Outline: Modify account
    Given adminAuthToken is present in the cookies
    And Create user account with name '<name>' password '<password>'
    When Attributes '<attributes>' are modified for user '<name>'
    Then Validate account has attributes '<attributes>'

    Examples: 
      | name          | password | attributes                                             |
      | account1@host | test123  | zimbraAccountStatus=pending, zimbraQuotaWarnPercent=20 |
