@prefs
Feature: Verify preference query

  Scenario Outline: Setup config and log in with user account
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'
    When Get '<account>' '<authToken>' authToken for username '<username>' and password '<password>' using '<method>' method
    Then set AuthToken in the cookie

    Examples: 
      | account | authToken       | method | server       | path        | headers                                               | account | username | password | status | valid | authToken       |
      | account | zimbraAuthToken | post   | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | test123  |    200 | valid | zimbraAuthToken |

  Scenario Outline: Query preference value for the user and verify the value.
    Given User queries value for preference '<pref>'
    Then '<prefValue>' should be returned for preference '<pref>'

    Examples: 
      | pref                                        | prefValue |
      | zimbraPrefAutocompleteAddressBubblesEnabled | TRUE      |
      | zimbraPrefVoiceItemsPerPage                 |        25 |
      | zimbraPrefFileSharingApplication            | briefcase |
      | zimbraPrefForwardReplyPrefixChar            | >         |
