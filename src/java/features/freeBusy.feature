@freeBusy
Feature: FreeBusy Query

  Scenario Outline: Setup config
    Given Application url '<server>' and path '<path>'
    And Request headers '<headers>'

    Examples: 
      | server       | path        | headers                                               | account | username | password | method | status | valid | authToken       |
      | zimbraServer | gqlEndpoint | Accept:application/json,Content-type:application/json | account | admin    | test123  | post   |    200 | valid | zimbraAuthToken |

  Scenario Outline: Create Test appointments
    When Get '<account>' '<authToken>' authToken for username '<user1>' and password '<password>' using '<method>' method
    Then set AuthToken in the cookie
    Then User '<user1>' schedules an appointment '<subject>' with user '<user2>' from '<startTime>' to '<endTime>'

    Examples: 
      | account | user1       | password | method | authToken       | user2       | subject       | startTime       | endTime         |
      | account | user_3@host | test123  | post   | zimbraAuthToken | user_4@host | test_subject1 | 20190318T093000 | 20190318T103000 |
      | account | user_4@host | test123  | post   | zimbraAuthToken | user_3@host | test_subject2 | 20190318T103000 | 20190318T113000 |

  Scenario Outline: FreeBusy Query
    When Get '<account>' '<authToken>' authToken for username '<user>' and password '<password>' using '<method>' method
    Then set AuthToken in the cookie
    When FreeBusy status is queried for user '<user>' for startTime '<sTime>' and endTime '<eTime>'
    Then Timeslot '<timeslot1>' - '<timeslot2>' should be marked as '<status>'

    Examples: 
      | account | authToken       | method | sTime         | eTime         | user        | password | status | timeslot1     | timeslot2     |
      | account | zimbraAuthToken | post   | 1552847400000 | 1552933800000 | user_3@host | test123  | f      | 1552847400000 | 1552915800000 |
      | account | zimbraAuthToken | post   | 1552847400000 | 1552933800000 | user_3@host | test123  | t      | 1552915800000 | 1552919400000 |
      | account | zimbraAuthToken | post   | 1552847400000 | 1552933800000 | user_3@host | test123  | b      | 1552919400000 | 1552923000000 |
      | account | zimbraAuthToken | post   | 1552847400000 | 1552933800000 | user_3@host | test123  | f      | 1552923000000 | 1552933800000 |
