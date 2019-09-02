@smxProxyMailbox
  Feature: sxmProxyMutation

  Scenario Outline: Verify Smx Mailbox
    Given Set SmxProxy Host and Port details
    And Given set SmxAuthToken in the cookie
    And Request headers "Content-type:application/json"
    When Get Smx Mailbox
    Then Validate Smx Mailbox

    Examples: 
      | name          | password | attributes                                                                      |
      | account1@host | test123  | zimbraAccountStatus=active, zimbraQuotaWarnPercent=20                           |
      
  Scenario Outline: Verify Smx Mailbox WhiteLists Post
    Given Set SmxProxy Host and Port details
    And Given set SmxAuthToken in the cookie
    And Request headers "Content-type:application/json"
    When Post Smx Mailbox Whitelists
    Then Validate Smx Mailbox Whitelists

    Examples: 
      | name          | password | attributes                                                                      |
      | account1@host | test123  | zimbraAccountStatus=active, zimbraQuotaWarnPercent=20                           |