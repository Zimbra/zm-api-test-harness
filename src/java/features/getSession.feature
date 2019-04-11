@session
Feature: getSession

  Scenario Outline: verify getSession
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Get '<account>' '<authToken>' authToken for username '<username>' and password '<password>' using '<method>' method
    Then set AuthToken in the cookie
    Given user queries session information

    Examples: 
      | server       | path        | headers                                               | account | username | password | method | status | valid | authToken       |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | test123  | post   |    200 | valid | zimbraAuthToken |

  Scenario Outline: Verify getSession
    Then validate valid response is returned for '<param>'

    Examples: 
      | param            |
      | sessionId        |
      | createdDate      |
      | lastAccessed     |
      | requestIPAddress |
      | userAgent        |