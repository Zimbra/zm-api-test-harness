@test
Feature: authMutation
  Scenario Outline: verify authMutation
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Get '<account>' '<authToken>' authToken for username '<username>' and password '<password>' using '<method>' method
    Then Validate status is '<status>'
    And Validate authToken is '<valid>' '<authToken>' token

    Examples: 
      | server       | path        | headers                                               | account | username | password        | method | status | valid   | authToken       |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | test123         | post   |    200 | valid   | zimbraAuthToken |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | test123         | post   |    200 | valid   | JWT             |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | invalidPassword | post   |    200 | invalid | zimbraAuthToken |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | invalidPassword | post   |    200 | invalid | JWT             |
