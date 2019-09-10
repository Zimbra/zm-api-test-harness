@adminAuth @smx
Feature: Admin auth mutation

  Scenario Outline: Validate authentication for admin account
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Generate admin authToken for user '<username>' and password '<password>'
    Then Validate status is '<status>'
    And Validate token is '<valid>' '<authToken>' token

    Examples: 
      | server            | path             | headers                                               | account    | username   | password        | status | valid   | authToken       |
      | zimbraAdminServer | gqlAdminEndpoint | Accept:application/json,Content-type:application/json | admin@host | admin@host | test123         |    200 | valid   | zimbraAuthToken |
      | zimbraAdminServer | gqlAdminEndpoint | Accept:application/json,Content-type:application/json | admin@host | admin@host | invalidPassword |    200 | invalid | zimbraAuthToken |
