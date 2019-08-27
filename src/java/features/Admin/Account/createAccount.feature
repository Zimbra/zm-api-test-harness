@createAccount
Feature: Create account mutation

  Scenario Outline: Log in with admin user
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Generate admin authToken for user '<username>' and password '<password>'
    Then set adminAuthToken in the cookie

    Examples: 
      | server       | path             | headers                                               | username | password | status | valid | authToken       |
      | zimbraServer | gqlAdminEndpoint | Accept:application/json,Content-type:application/json | admin    | test123  |    200 | valid | zimbraAuthToken |

  Scenario Outline: Create account
    Given adminAuthToken is present in the cookies
    When Create user with name '<name>' password '<password>' attribute '<attributes>'
    Then Validate account is created/modified with attributes '<attributes>'

    Examples: 
      | name          | password | attributes                                                                      |
      | account1@host | test123  | zimbraAccountStatus=active, zimbraQuotaWarnPercent=20                           |
      | account2@host | test123  | zimbraFeatureMobileSyncEnabled=FALSE, zimbraMailForwardingAddressMaxLength=2048 |

  Scenario Outline: Create account
    Given adminAuthToken is present in the cookies
    When Create user account with name '<name>' password '<password>'
    Then Validate account is created/modified with attributes '<attributes>'

    Examples: 
      | name          | password | attributes                                            |
      | account1@host | test123  | zimbraAccountStatus=active, zimbraQuotaWarnPercent=90 |
