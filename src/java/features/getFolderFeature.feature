Feature: getFolder Query

  @post
  Scenario Outline: test gql post with new client
    Given url '<url>'
    And path '/service/extension/graphql'
    #And configure ssl = true
    And header "Accept" = "application/json"
    And header "Content-type" = "application/json"
    And cookie ZM_AUTH_TOKEN = '0_840258f75fb85b2caebb60661151bd3f0ed1973c_69643d33363a33363731393331362d636436392d346161332d623363662d6638393133373737376531343b6578703d31333a313533333830353031373838393b747970653d363a7a696d6272613b753d313a613b7469643d31303a313739363437373631343b76657273696f6e3d31343a382e382e31305f47415f323032303b637372663d313a313b'
    When request {'query':'{ folder(getFolder: {folderId: \"<folderId>\"}) { name}}'}
    And method <method>
    Then status 200
    Then match response contains '{data={folder={name=Briefcase}}}'

    Examples: 
      | url                               | method |folderId|
      | https://zdev-vm002.eng.zimbra.com | post   |16|
      #| https://zcs-dev.test:8443         | get    |