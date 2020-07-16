#Tickets associated : ZCS-6135
@cp
Feature: Change password mutation

  Scenario Outline: Log in with user account
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Get '<account>' '<authToken>' authToken for username '<username>' and password '<password>' using '<method>' method
    Then set AuthToken in the cookie

    Examples: 
      | server       | path        | headers                                               | account | username | password | method | status | valid | authToken       |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | test124  | post   |    200 | valid | zimbraAuthToken |

  Scenario Outline: Change password for given account
    Given authToken is present in the cookies
    When '<user>' changes old password '<oPwd>' to new password '<nPwd>' for '<vHostName>' using '<identifier>'
    Then verify that valid authToken '<status>' being returned
    And verify that valid lifetime '<status>' being returned

    Examples: 
      | user  | oPwd    | nPwd    | vHostName | identifier | status |
      | admin | test124 | test123 | default   | name       | is     |
